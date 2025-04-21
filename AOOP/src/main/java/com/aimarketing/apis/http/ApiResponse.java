package com.aimarketing.apis.http;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

public class ApiResponse {
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public static Map<String, Object> parseResponse(Response response) throws IOException {
        if (response.getStatusCode() >= 400) {
            throw new ApiException("API request failed with status code: " + response.getStatusCode(), 
                response.getStatusCode(), response.getBody());
        }
        
        try {
            return mapper.readValue(response.getBody(), new TypeReference<Map<String, Object>>() {});
        } catch (Exception e) {
            throw new ApiException("Failed to parse API response: " + e.getMessage(), 
                response.getStatusCode(), response.getBody());
        }
    }
    
    public static class ApiException extends IOException {
        private final int statusCode;
        private final String responseBody;
        
        public ApiException(String message, int statusCode, String responseBody) {
            super(message);
            this.statusCode = statusCode;
            this.responseBody = responseBody;
        }
        
        public int getStatusCode() { return statusCode; }
        public String getResponseBody() { return responseBody; }
    }
}