package com.dataservices.ssoma.flujos_trabajo_documentacion.util;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class PasswordUtil {

    private final PasswordEncoder passwordEncoder;
    private static final SecureRandom secureRandom = new SecureRandom();

    /**
     * Genera un hash de la contraseña usando BCrypt
     */
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Verifica si una contraseña coincide con su hash
     */
    public boolean verifyPassword(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    /**
     * Genera una contraseña aleatoria
     */
    public String generarPasswordAleatoria(int longitud) {
        String caracteres = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%";
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < longitud; i++) {
            int index = secureRandom.nextInt(caracteres.length());
            password.append(caracteres.charAt(index));
        }

        return password.toString();
    }
}
