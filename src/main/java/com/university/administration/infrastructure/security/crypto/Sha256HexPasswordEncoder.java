package com.university.administration.infrastructure.security.crypto;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Sha256HexPasswordEncoder implements PasswordEncoder {

    @Override
    public String encode(CharSequence rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(rawPassword.toString().getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar password", e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {

        String rawEncoded = encode(rawPassword);

        System.out.println("RAW ENCODED: " + rawEncoded);
        System.out.println("DB VALUE   : " + encodedPassword);

        return rawEncoded.equals(encodedPassword);
    }
}

