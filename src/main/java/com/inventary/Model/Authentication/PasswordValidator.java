package com.inventary.Model.Authentication;

import java.util.List;
import java.util.regex.Pattern;

public class PasswordValidator {
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    // Lista de contraseñas comunes
    private static final List<String> PASSWORDS_COMUNES = List.of(
            "12345678", "password", "123456", "123456789", "qwerty",
            "abc123", "111111", "123123", "1234", "000000", "password1",
            "qwerty123", "admin", "letmein", "12345", "asdfgh", "zxcvbn"
    );

    public static boolean esPasswordValida(String password, String email, List<String> datosPersonales) {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            return false;
        }

        String emailPrefix = email.split("@")[0];
        if (password.toLowerCase().contains(emailPrefix.toLowerCase())) {
            return false;
        }

        for (String dato : datosPersonales) {
            if (dato != null && password.toLowerCase().contains(dato.toLowerCase())) {
                return false;
            }
        }

        return !PASSWORDS_COMUNES.contains(password.toLowerCase());
    }
}
