package com.aimarketing.apis.config;

import java.util.HashMap;
import java.util.Map;

public class APIEndpointConfig {
    private static final Map<String, EndpointConfig> endpoints = new HashMap<>();
    
    static {
        // Content API endpoints
        endpoints.put("/api/content/generate", new EndpointConfig(
            "POST",
            true,
            "Generate optimized content for campaigns"
        ));
        endpoints.put("/api/content/analyze", new EndpointConfig(
            "POST",
            true,
            "Analyze content performance"
        ));
        
        // SEO API endpoints
        endpoints.put("/api/seo/analyze", new EndpointConfig(
            "POST",
            true,
            "Analyze SEO metrics and provide recommendations"
        ));
        endpoints.put("/api/seo/keywords", new EndpointConfig(
            "GET",
            true,
            "Get keyword rankings and performance"
        ));
        
        // Social Media API endpoints
        endpoints.put("/api/social/analyze", new EndpointConfig(
            "POST",
            true,
            "Analyze social media campaign performance"
        ));
        endpoints.put("/api/social/optimize", new EndpointConfig(
            "POST",
            true,
            "Optimize social media campaign settings"
        ));
        
        // Video API endpoints
        endpoints.put("/api/video/edit", new EndpointConfig(
            "POST",
            true,
            "Edit and optimize video content"
        ));
        endpoints.put("/api/video/analyze", new EndpointConfig(
            "POST",
            true,
            "Analyze video performance metrics"
        ));
    }
    
    public static EndpointConfig getEndpointConfig(String path) {
        return endpoints.get(path);
    }
    
    public static class EndpointConfig {
        private final String method;
        private final boolean requiresAuth;
        private final String description;
        
        public EndpointConfig(String method, boolean requiresAuth, String description) {
            this.method = method;
            this.requiresAuth = requiresAuth;
            this.description = description;
        }
        
        public String getMethod() {
            return method;
        }
        
        public boolean requiresAuth() {
            return requiresAuth;
        }
        
        public String getDescription() {
            return description;
        }
    }
}