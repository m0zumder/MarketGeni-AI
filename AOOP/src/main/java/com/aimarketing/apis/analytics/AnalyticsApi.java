package com.aimarketing.apis.analytics;

import com.aimarketing.apis.ApiConfig;
import com.aimarketing.apis.http.HttpClient;
import com.aimarketing.apis.http.HttpClient.Response;
import java.io.IOException;
import java.util.*;

public class AnalyticsApi {
    private final String apiKey;
    private final Map<String, String> config;
    
    public AnalyticsApi() {
        this.config = ApiConfig.getConfig("analytics");
        this.apiKey = ApiConfig.getApiKey("analytics");
    }
    
    public Map<String, Object> getMetrics(String campaignId, List<String> metrics) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("campaign_id", campaignId);
        requestBody.put("metrics", metrics);
        
        String url = config.get("base_url") + "/metrics/get";
        Response response = HttpClient.post(url, headers, requestBody.toString());
        return ApiResponse.parseResponse(response);
    }
    
    public Map<String, Object> generateReport(String campaignId, Date startDate, Date endDate) 
            throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("campaign_id", campaignId);
        requestBody.put("start_date", startDate.getTime());
        requestBody.put("end_date", endDate.getTime());
        
        String url = config.get("base_url") + "/reports/generate";
        Response response = HttpClient.post(url, headers, requestBody.toString());
        return ApiResponse.parseResponse(response);
    }
    
    public Map<String, Object> getAudienceInsights(String campaignId) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        
        String url = config.get("base_url") + "/audience/insights/" + campaignId;
        Response response = HttpClient.get(url, headers);
        return ApiResponse.parseResponse(response);
    }
    
    public Map<String, Object> trackConversions(String campaignId, List<Map<String, Object>> events) 
            throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("campaign_id", campaignId);
        requestBody.put("events", events);
        
        String url = config.get("base_url") + "/conversions/track";
        Response response = HttpClient.post(url, headers, requestBody.toString());
        return ApiResponse.parseResponse(response);
    }
}