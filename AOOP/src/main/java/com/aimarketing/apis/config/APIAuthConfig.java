package com.aimarketing.apis.config;

import java.util.HashMap;
import java.util.Map;

public class APIAuthConfig {
    private static final Map<String, AuthConfig> authConfigs = new HashMap<>();
    
    static {
        // Default authentication settings
        authConfigs.put("default", new AuthConfig(
            true,  // requiresToken
            3600,  // tokenExpirySeconds
            new String[]{"read", "write"}
        ));
        
        // Content API authentication
        authConfigs.put("content", new AuthConfig(
            true,
            7200,
            new String[]{"read", "write", "analyze"}
        ));
        
        // SEO API authentication
        authConfigs.put("seo", new AuthConfig(
            true,
            3600,
            new String[]{"read", "analyze", "optimize"}
        ));
        
        // Social Media API authentication
        authConfigs.put("social", new AuthConfig(
            true,
            3600,
            new String[]{"read", "post", "analyze"}
        ));
        
        // Video API authentication
        authConfigs.put("video", new AuthConfig(
            true,
            7200,
            new String[]{"read", "edit", "analyze"}
        ));
    }
    
    public static AuthConfig getAuthConfig(String apiType) {
        return authConfigs.getOrDefault(apiType, authConfigs.get("default"));
    }
    
    public static class AuthConfig {
        private final boolean requiresToken;
        private final int tokenExpirySeconds;
        private final String[] permissions;
        
        public AuthConfig(boolean requiresToken, int tokenExpirySeconds, String[] permissions) {
            this.requiresToken = requiresToken;
            this.tokenExpirySeconds = tokenExpirySeconds;
            this.permissions = permissions;
        }
        
        public boolean requiresToken() {
            return requiresToken;
        }
        
        public int getTokenExpirySeconds() {
            return tokenExpirySeconds;
        }
        
        public String[] getPermissions() {
            return permissions;
        }
    }
}