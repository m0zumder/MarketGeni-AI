package com.aimarketing.apis;

import java.util.*;

public class ApiConfig {
    private static final Map<String, Map<String, String>> API_CONFIGS = new HashMap<>();
    
    static {
        // Social Media API configurations
        Map<String, String> socialMediaConfig = new HashMap<>();
        socialMediaConfig.put("base_url", "https://api.socialmedia.com/v1");
        socialMediaConfig.put("timeout", "30000");
        API_CONFIGS.put("social_media", socialMediaConfig);
        
        // SEO API configurations
        Map<String, String> seoConfig = new HashMap<>();
        seoConfig.put("base_url", "https://api.seotools.com/v2");
        seoConfig.put("timeout", "45000");
        API_CONFIGS.put("seo", seoConfig);
        
        // Analytics API configurations
        Map<String, String> analyticsConfig = new HashMap<>();
        analyticsConfig.put("base_url", "https://api.analytics.com/v1");
        analyticsConfig.put("timeout", "60000");
        API_CONFIGS.put("analytics", analyticsConfig);
    }
    
    public static Map<String, String> getConfig(String apiName) {
        return API_CONFIGS.getOrDefault(apiName, new HashMap<>());
    }
    
    public static void setApiKey(String apiName, String apiKey) {
        API_CONFIGS.computeIfAbsent(apiName, k -> new HashMap<>())
                   .put("api_key", apiKey);
    }
    
    public static String getApiKey(String apiName) {
        return API_CONFIGS.getOrDefault(apiName, new HashMap<>())
                          .getOrDefault("api_key", "");
    }
}