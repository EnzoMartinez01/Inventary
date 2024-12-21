package com.inventary.Controller.Authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inventary.Dto.Authentication.LoginDto;
import com.inventary.Dto.Authentication.UserDto;
import com.inventary.Model.Authentication.Security.CustomUserDetails;
import com.inventary.Model.Authentication.Users;
import com.inventary.Repository.Authentication.RoleRepository;
import com.inventary.Repository.Authentication.UsersRepository;
import com.inventary.Security.JWTService;
import com.inventary.Security.LoginResponse;
import com.inventary.Services.Authentication.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final static Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final UserService userService;
    private final JWTService jwtService;
    private final UsersRepository userRepository;
    private final RoleRepository roleRepository;

    public AuthController(UserService userService,
                          JWTService jwtService,
                          UsersRepository userRepository,
                          RoleRepository roleRepository) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    // Register User
    @PostMapping("/register/users/{idRole}")
    public ResponseEntity<Map<String, String>> registerPersonal(
            @PathVariable Long idRole,
            @RequestParam(value = "file", required = false) MultipartFile file,  // El archivo ahora es opcional
            @RequestParam("users") String usersJson
    ) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            Users users = objectMapper.readValue(usersJson, Users.class);

            if (users.getEmail() == null || users.getPassword() == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Email y contraseña son requeridos."));
            }

            if (!roleRepository.existsById(idRole)) {
                return ResponseEntity.badRequest().body(Map.of("message", "El rol con ID " + idRole + " no existe."));
            }

            userService.registerUser(users, idRole, file);

            return ResponseEntity.ok().body(Map.of("message", "Usuario Registrado Satisfactoriamente.", "email", users.getEmail()));

        } catch (IOException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Error interno al registrar usuario."));
        }
    }

    //Resend Verification Code
    @PostMapping("/resend-code")
    public ResponseEntity<String> resendVerificationCode(@RequestParam String email) {
        try {
            boolean isResent = userService.resendVerificationCode(email);
            if (isResent) {
                return ResponseEntity.ok("El código de verificación ha sido enviado nuevamente.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No se encontró un usuario con el correo proporcionado.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al reenviar el código de verificación.");
        }
    }

    //Verify User
    @PostMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyUser(@RequestParam String email, @RequestParam String code) {
        try {
            boolean verified = userService.verifyUsers(email, code);
            return verified ? ResponseEntity.ok(Map.of("message", "Usuario verificado exitosamente.")) : ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Verification failed or code expired."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Error verifying personal."));        }
    }

    //Login
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticateUser(@RequestBody LoginDto loginDto) {
        try {
            Users authenticatedPersonal = userService.authenticate(loginDto);

            if (authenticatedPersonal.getState()) {
                String role = authenticatedPersonal.getRole() != null ? authenticatedPersonal.getRole().getName() : "ROLE_USER";

                String jwtToken = jwtService.generateToken(new CustomUserDetails(authenticatedPersonal));

                LoginResponse loginResponse = new LoginResponse(jwtToken, null, role);
                return ResponseEntity.ok(loginResponse);
            } else {
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }
        } catch (RuntimeException e) {
            System.out.println("No se encontró el personal tampoco.");
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    //Logout
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token no proporcionado o inválido."));
        }

        String token = authHeader.substring(7);

        userService.logout(token);

        return ResponseEntity.ok(Map.of("message", "Logged out successfully."));
    }
}
