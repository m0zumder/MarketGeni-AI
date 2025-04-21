package com.aimarketing.apis.social;

import com.aimarketing.apis.ApiConfig;
import com.aimarketing.apis.http.HttpClient;
import com.aimarketing.apis.http.HttpClient.Response;
import java.io.IOException;
import java.util.*;

public class SocialMediaApi {
    private final String platform;
    private final String apiKey;
    private final Map<String, String> config;
    
    public SocialMediaApi(String platform) {
        this.platform = platform;
        this.config = ApiConfig.getConfig("social_media");
        this.apiKey = ApiConfig.getApiKey("social_media");
    }
    
    public Map<String, Object> schedulePost(String content, Date scheduledTime, List<String> mediaUrls) 
            throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", content);
        requestBody.put("scheduled_time", scheduledTime.getTime());
        requestBody.put("media_urls", mediaUrls);
        requestBody.put("platform", platform);
        
        String url = config.get("base_url") + "/posts/schedule";
        Response response = HttpClient.post(url, headers, requestBody.toString());
        return ApiResponse.parseResponse(response);
    }
    
    public Map<String, Object> getEngagementMetrics(String postId) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        
        String url = config.get("base_url") + "/posts/" + postId + "/metrics";
        Response response = HttpClient.get(url, headers);
        return ApiResponse.parseResponse(response);
    }
    
    public Map<String, Object> getAudienceInsights() throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        
        String url = config.get("base_url") + "/audience/insights";
        Response response = HttpClient.get(url, headers);
        return ApiResponse.parseResponse(response);
    }
    
    public Map<String, Object> createAutomationRule(Map<String, Object> ruleConfig) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");
        
        String url = config.get("base_url") + "/automation/rules";
        Response response = HttpClient.post(url, headers, ruleConfig.toString());
        return ApiResponse.parseResponse(response);
    }
}