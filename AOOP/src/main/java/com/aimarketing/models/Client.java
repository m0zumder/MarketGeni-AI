package com.aimarketing.models;

import org.bson.types.ObjectId;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Client {
    private ObjectId id;
    private String username;
    private String email;
    private String hashedPassword;
    private String companyName;
    private String industry;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;
    private Map<String, Object> preferences;
    private boolean isActive;

    public Client() {
        this.id = new ObjectId();
        this.createdAt = LocalDateTime.now();
        this.preferences = new HashMap<>();
        this.isActive = true;
    }

    // Getters and Setters
    public ObjectId getId() { return id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getHashedPassword() { return hashedPassword; }
    public void setHashedPassword(String hashedPassword) { this.hashedPassword = hashedPassword; }

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public LocalDateTime getCreatedAt() { return createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public Map<String, Object> getPreferences() { return preferences; }
    public void setPreference(String key, Object value) { this.preferences.put(key, value); }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    @Override
    public String toString() {
        return String.format("%s (%s)", username, email);
    }
}