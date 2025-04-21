package com.aimarketing.apis.seo;

import com.aimarketing.apis.ApiConfig;
import com.aimarketing.apis.http.HttpClient;
import com.aimarketing.apis.http.HttpClient.Response;
import java.io.IOException;
import java.util.*;

public class SEOApi {
    private final String apiKey;
    private final Map<String, String> config;
    
    public SEOApi() {
        this.config = ApiConfig.getConfig("seo");
        this.apiKey = ApiConfig.getApiKey("seo");
    }
    
    public Map<String, Object> analyzeKeywords(String content) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("content", content);
        
        String url = config.get("base_url") + "/keywords/analyze";
        Response response = HttpClient.post(url, headers, requestBody.toString());
        return ApiResponse.parseResponse(response);
    }
    
    public Map<String, Object> trackBacklinks(String domain) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        
        String url = config.get("base_url") + "/backlinks/" + domain;
        Response response = HttpClient.get(url, headers);
        return ApiResponse.parseResponse(response);
    }
    
    public Map<String, Object> getKeywordRankings(String domain, List<String> keywords) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        headers.put("Content-Type", "application/json");
        
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("domain", domain);
        requestBody.put("keywords", keywords);
        
        String url = config.get("base_url") + "/rankings/check";
        Response response = HttpClient.post(url, headers, requestBody.toString());
        return ApiResponse.parseResponse(response);
    }
    
    public Map<String, Object> getSiteAudit(String domain) throws IOException {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + apiKey);
        
        String url = config.get("base_url") + "/audit/" + domain;
        Response response = HttpClient.get(url, headers);
        return ApiResponse.parseResponse(response);
    }
}