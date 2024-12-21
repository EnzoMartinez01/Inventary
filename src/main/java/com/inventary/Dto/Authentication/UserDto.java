package com.inventary.Dto.Authentication;

import lombok.Data;

import java.time.LocalDate;
import java.util.Date;

@Data
public class UserDto {
    private Long idUser;
    private String username;
    private String roleName;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private LocalDate birthDate;
    private LocalDate registrationDate;
    private Boolean state;
    private String photoFile;
}
