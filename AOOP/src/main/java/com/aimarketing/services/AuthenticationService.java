package com.aimarketing.services;

import com.aimarketing.models.User;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthenticationService {
    private static final Map<String, User> users = new HashMap<>();
    private static final Map<String, String> tokens = new HashMap<>();
    private static final SecureRandom secureRandom = new SecureRandom();

    public User register(String username, String email, String password, User.Role role) {
        if (users.values().stream().anyMatch(u -> u.getUsername().equals(username) || u.getEmail().equals(email))) {
            throw new IllegalArgumentException("Username or email already exists");
        }

        String salt = generateSalt();
        String passwordHash = hashPassword(password, salt);

        User user = new User(username, email, salt + ":" + passwordHash, role);
        user.setId(UUID.randomUUID().toString());
        users.put(user.getId(), user);

        return user;
    }

    public String login(String usernameOrEmail, String password) {
        User user = users.values().stream()
                .filter(u -> u.getUsername().equals(usernameOrEmail) || u.getEmail().equals(usernameOrEmail))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        String[] hashParts = user.getPasswordHash().split(":");
        String salt = hashParts[0];
        String storedHash = hashParts[1];

        if (!hashPassword(password, salt).equals(storedHash)) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        user.setLastLoginAt(LocalDateTime.now());
        String token = generateToken();
        tokens.put(token, user.getId());

        return token;
    }

    public void logout(String token) {
        tokens.remove(token);
    }

    public User getCurrentUser(String token) {
        String userId = tokens.get(token);
        if (userId == null) {
            throw new IllegalArgumentException("Invalid or expired token");
        }
        return users.get(userId);
    }

    private String generateSalt() {
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Failed to hash password", e);
        }
    }

    private String generateToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getEncoder().encodeToString(tokenBytes);
    }
}