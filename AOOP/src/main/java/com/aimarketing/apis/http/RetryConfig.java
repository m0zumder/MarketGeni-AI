package com.aimarketing.apis.http;

public class RetryConfig {
    private final int maxRetries;
    private final long initialDelayMs;
    private final long maxDelayMs;
    private final double backoffMultiplier;
    
    public static final RetryConfig DEFAULT = new RetryConfig(3, 1000, 5000, 2.0);
    
    public RetryConfig(int maxRetries, long initialDelayMs, long maxDelayMs, double backoffMultiplier) {
        this.maxRetries = maxRetries;
        this.initialDelayMs = initialDelayMs;
        this.maxDelayMs = maxDelayMs;
        this.backoffMultiplier = backoffMultiplier;
    }
    
    public int getMaxRetries() {
        return maxRetries;
    }
    
    public long getInitialDelayMs() {
        return initialDelayMs;
    }
    
    public long getMaxDelayMs() {
        return maxDelayMs;
    }
    
    public double getBackoffMultiplier() {
        return backoffMultiplier;
    }
    
    public long getDelayForAttempt(int attempt) {
        if (attempt <= 0) return 0;
        
        double delay = initialDelayMs * Math.pow(backoffMultiplier, attempt - 1);
        return (long) Math.min(delay, maxDelayMs);
    }
}