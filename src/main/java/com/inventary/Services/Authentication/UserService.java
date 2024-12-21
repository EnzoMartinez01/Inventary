package com.inventary.Services.Authentication;

import com.inventary.Dto.Authentication.LoginDto;
import com.inventary.Dto.Authentication.UserDto;
import com.inventary.Model.Authentication.Security.ActiveToken;
import com.inventary.Model.Authentication.Security.CustomUserDetails;
import com.inventary.Model.Authentication.Security.Role;
import com.inventary.Model.Authentication.Users;
import com.inventary.Repository.Authentication.RoleRepository;
import com.inventary.Repository.Authentication.Security.ActiveTokenRepository;
import com.inventary.Repository.Authentication.UsersRepository;
import com.inventary.Security.JWTService;
import com.inventary.Services.Email.EmailService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UsersRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final RoleRepository roleRepository;
    private final EmailService emailService;
    private final ActiveTokenRepository activeTokenRepository;
    private final JWTService jwtService;

    public UserService(UsersRepository userRepository,
                       BCryptPasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       RoleRepository roleRepository,
                       EmailService emailService,
                       ActiveTokenRepository activeTokenRepository,
                       JWTService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.roleRepository = roleRepository;
        this.emailService = emailService;
        this.activeTokenRepository = activeTokenRepository;
        this.jwtService = jwtService;
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Page<UserDto> allUsers(int page, int size,
                                  Boolean state, String email,
                                  String username, String phoneNumber,
                                  String lastName, String name) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Users> users = userRepository.searchUsers(state, email, username, phoneNumber, lastName, name, pageable);

        return users.map(this::mapToDto);
    }

    // Mapping User to UserDTO
    public UserDto mapToDto(Users users) {
        UserDto dto = new UserDto();
        dto.setIdUser(users.getIdUser());
        dto.setUsername(users.getUsername());
        dto.setRoleName(users.getRole().getName());
        dto.setFirstName(users.getName() + " " + users.getMiddleName());
        dto.setLastName(users.getLastName() + " " + users.getMothersSurname());
        dto.setEmail(users.getEmail());
        dto.setPhoneNumber(users.getPhoneNumber());
        dto.setBirthDate(users.getBirthDate());
        dto.setRegistrationDate(users.getRegistrationDate());
        dto.setState(users.getState());
        dto.setPhotoFile(users.getPhotoFile());
        return dto;
    }

    public UserDto getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(this::mapToDto)
                .orElseThrow(() -> new UsernameNotFoundException("User not fount with username: " + username));
    }

    public Optional<Users> findById(Long idUser) {
        return userRepository.findById(idUser);
    }

    //Register Users
    @Transactional
    public void registerUser(Users users, Long idRole, MultipartFile file) {
        try {
            users.setPassword(passwordEncoder.encode(users.getPassword()));

            Role role = roleRepository.findById(idRole)
                    .orElseThrow(() -> new RuntimeException("Role not found with id: " + idRole));
            users.setRole(role);

            if (file != null && !file.isEmpty()) {
                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new IllegalArgumentException("El archivo debe ser una imagen válida (JPG, PNG, etc.).");
                }

                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replaceAll(" ", "_");

                Path uploadDir = Paths.get("uploads/images/users/" + users.getName() + users.getLastName());
                if (!Files.exists(uploadDir)) {
                    Files.createDirectories(uploadDir);
                }

                Path targetLocation = uploadDir.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

                users.setPhotoFile(fileName);
            }

            users.setRegistrationDate(LocalDate.now());
            users.setState(false);

            Users savedUsers = userRepository.save(users);

            logger.info("User " + savedUsers.getUsername() + " created successfully");

            sendVerificationCode(savedUsers);

        } catch (IOException e) {
            logger.error("Error guardando el archivo de imagen", e);
            throw new RuntimeException("Error guardando el archivo de imagen", e);
        } catch (Exception e) {
            logger.error("Error creando usuario", e);
            throw new RuntimeException("Error creando usuario", e);
        }
    }


    // Send verification code
    public void sendVerificationCode(Users usuarios) {
        Random random = new Random();
        String code = String.format("%06d", random.nextInt(1000000));
        usuarios.setVerificationCode(code);
        usuarios.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(2));
        userRepository.save(usuarios);
        emailService.sendVerificationEmail(usuarios.getEmail(), code, usuarios.getLastName());
    }

    // Re send verification code
    public boolean resendVerificationCode(String email) {
        Optional<Users> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            Users user = userOptional.get();

            sendVerificationCode(user);
            return true;
        }
        return false;
    }


    // Verify users
    @Transactional
    public boolean verifyUsers(String email, String code) {
        Optional<Users> personalOptional = userRepository.findByEmail(email);

        if (personalOptional.isPresent()) {
            Users users = personalOptional.get();
            if (users.getVerificationCodeExpiry().isBefore(LocalDateTime.now())) {
                userRepository.delete(users);
                return false;
            }
            if (users.getVerificationCode().equals(code) && users.getVerificationCodeExpiry().isAfter(LocalDateTime.now())) {
                users.setState(true);
                users.setVerificationCode(null);
                users.setVerificationCodeExpiry(null);
                userRepository.save(users);
                return true;
            }
        }
        return false;
    }

    // Authentication
    public Users authenticate(LoginDto loginDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        Users usuarios = userRepository.findByUsername(loginDto.getUsername()).orElseThrow();
        usuarios.setConnectionDate(LocalDate.now());
        userRepository.save(usuarios);

        return usuarios;
    }

    //Logout
    @Transactional
    public void logout(String token) {
        activeTokenRepository.deleteByToken(token);
        System.out.println("Token eliminado correctamente.");
    }

    //Obtener la imagen del usuario
    public Resource getProfilePicture(String fileName, String username) {
        try {
            // Consultar el usuario en base al username
            Users user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

            // Construir la ruta del archivo
            String userDirectory = user.getName() + user.getLastName(); // Nombre + Apellido
            Path baseDirectory = Paths.get("uploads/images/users");
            Path filePath = baseDirectory.resolve(userDirectory).resolve(fileName).normalize();

            // Verificar que el archivo exista
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new FileNotFoundException("Archivo no encontrado: " + filePath.toString());
            }

            return resource;
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener la imagen: " + e.getMessage(), e);
        }
    }
}
