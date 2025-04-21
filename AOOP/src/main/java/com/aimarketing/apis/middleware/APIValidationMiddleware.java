package com.aimarketing.apis.middleware;

import com.aimarketing.apis.config.APIAuthConfig;
import com.aimarketing.apis.config.APIEndpointConfig;
import com.aimarketing.apis.http.ApiResponse.ApiException;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.time.Instant;

public class APIValidationMiddleware {
    private static final Map<String, Integer> requestCounts = new ConcurrentHashMap<>();
    private static final Map<String, Instant> lastRequestTimes = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 60;
    
    public static void validateRequest(String path, String method, String token, String apiType) throws ApiException {
        validateEndpoint(path, method);
        validateAuth(token, apiType);
        validateRateLimit(token);
    }
    
    private static void validateEndpoint(String path, String method) throws ApiException {
        APIEndpointConfig.EndpointConfig config = APIEndpointConfig.getEndpointConfig(path);
        if (config == null) {
            throw new ApiException("Invalid endpoint: " + path, 404, "Endpoint not found");
        }
        
        if (!config.getMethod().equalsIgnoreCase(method)) {
            throw new ApiException("Method not allowed", 405, "Invalid HTTP method");
        }
    }
    
    private static void validateAuth(String token, String apiType) throws ApiException {
        APIAuthConfig.AuthConfig authConfig = APIAuthConfig.getAuthConfig(apiType);
        
        if (authConfig.requiresToken() && (token == null || token.isEmpty())) {
            throw new ApiException("Authentication required", 401, "Missing authentication token");
        }
        
        // Additional token validation logic can be added here
    }
    
    private static void validateRateLimit(String token) throws ApiException {
        Instant now = Instant.now();
        Instant lastRequest = lastRequestTimes.get(token);
        
        // Reset counter if it's been more than a minute
        if (lastRequest == null || lastRequest.plusSeconds(60).isBefore(now)) {
            requestCounts.put(token, 1);
            lastRequestTimes.put(token, now);
            return;
        }
        
        int currentCount = requestCounts.getOrDefault(token, 0) + 1;
        if (currentCount > MAX_REQUESTS_PER_MINUTE) {
            throw new ApiException("Rate limit exceeded", 429, "Too many requests");
        }
        
        requestCounts.put(token, currentCount);
        lastRequestTimes.put(token, now);
    }
    
    public static void clearRateLimitData(String token) {
        requestCounts.remove(token);
        lastRequestTimes.remove(token);
    }
}