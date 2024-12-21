package com.inventary.Model.Authentication.Security;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "security.roles")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role", nullable = false)
    private Long idRole;

    @Column(name = "name_role", nullable = false)
    private String name;
}
