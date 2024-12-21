package com.inventary.Dto.Authentication;

import jakarta.validation.constraints.NotBlank;

public class LoginDto {
    @NotBlank
    private String username;
    @NotBlank
    private String password;

    public @NotBlank String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank String email) {
        this.username = email;
    }

    public @NotBlank String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank String password) {
        this.password = password;
    }
}
