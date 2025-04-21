package com.aimarketing.models;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Campaign {
    private String id;
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private double budget;
    private String status;
    private Map<String, Double> metrics;
    private Map<String, Object> aiSettings;
    private String targetAudience;
    private Map<String, Object> audienceSettings;
    private boolean aiOptimizationEnabled;
    private Map<String, Object> abTestSettings;

    public Campaign() {
        this.id = UUID.randomUUID().toString();
        this.metrics = new HashMap<>();
        this.aiSettings = new HashMap<>();
        this.audienceSettings = new HashMap<>();
        this.abTestSettings = new HashMap<>();
        this.status = "DRAFT";
        this.aiOptimizationEnabled = false;
    }

    public Campaign(String name, String description, double budget) {
        this();
        this.name = name;
        this.description = description;
        this.budget = budget;
    }

    // Getters and Setters
    public String getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public double getBudget() { return budget; }
    public void setBudget(double budget) { this.budget = budget; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Map<String, Double> getMetrics() { return metrics; }
    public void setMetric(String key, Double value) { this.metrics.put(key, value); }

    public Map<String, Object> getAiSettings() { return aiSettings; }
    public void setAiSetting(String key, Object value) { this.aiSettings.put(key, value); }

    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }

    public Map<String, Object> getAudienceSettings() { return audienceSettings; }
    public void setAudienceSetting(String key, Object value) { this.audienceSettings.put(key, value); }

    public boolean isAiOptimizationEnabled() { return aiOptimizationEnabled; }
    public void setAiOptimizationEnabled(boolean enabled) { this.aiOptimizationEnabled = enabled; }

    public Map<String, Object> getAbTestSettings() { return abTestSettings; }
    public void setAbTestSetting(String key, Object value) { this.abTestSettings.put(key, value); }

    @Override
    public String toString() {
        return name;
    }
}