package com.inventary.Model.Authentication;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.inventary.Deserializer.Authentication.RoleDeserializer;
import com.inventary.Model.Authentication.Security.Role;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "security.users")
@Data
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idUser;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String middleName;
    @Column(nullable = false)
    private String lastName;
    @Column(nullable = false)
    private String mothersSurname;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private LocalDate registrationDate;
    private LocalDate connectionDate;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(unique = true, nullable = false)
    private String email;
    private String phoneNumber;
    @Column(nullable = false)
    private String password;
    private Boolean state;

    @Column(name = "photo_file")
    private String photoFile;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    @JsonDeserialize(using = RoleDeserializer.class)
    private Role role;


    //Historial de Contraseñas
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private List<PasswordHistory> passwordHistory = new ArrayList<>();

    public void addPasswordToHistory(String oldPassword) {
        PasswordHistory history = new PasswordHistory();
        history.setPassword(oldPassword);
        history.setUsuarios(this);
        passwordHistory.add(history);
    }

    //Verificación Correo
    private String verificationCode;
    private LocalDateTime verificationCodeExpiry;
}
