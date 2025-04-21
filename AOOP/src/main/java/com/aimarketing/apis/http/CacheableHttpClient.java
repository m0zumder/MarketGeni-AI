package com.aimarketing.apis.http;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CacheableHttpClient extends RetryableHttpClient {
    private static final long DEFAULT_CACHE_TTL = TimeUnit.MINUTES.toMillis(5);
    
    @Override
    public static Response get(String url, Map<String, String> headers) throws IOException {
        String cacheKey = ResponseCache.generateKey(url, headers, null);
        Response cachedResponse = ResponseCache.get(cacheKey);
        
        if (cachedResponse != null) {
            return cachedResponse;
        }
        
        Response response = super.get(url, headers);
        if (response.getStatusCode() < 300) {
            ResponseCache.put(cacheKey, response, DEFAULT_CACHE_TTL, TimeUnit.MILLISECONDS);
        }
        
        return response;
    }
    
    public static Response getWithTtl(String url, Map<String, String> headers, long ttl, TimeUnit unit) 
            throws IOException {
        String cacheKey = ResponseCache.generateKey(url, headers, null);
        Response cachedResponse = ResponseCache.get(cacheKey);
        
        if (cachedResponse != null) {
            return cachedResponse;
        }
        
        Response response = super.get(url, headers);
        if (response.getStatusCode() < 300) {
            ResponseCache.put(cacheKey, response, ttl, unit);
        }
        
        return response;
    }
    
    public static void invalidateCache(String url, Map<String, String> headers) {
        String cacheKey = ResponseCache.generateKey(url, headers, null);
        ResponseCache.invalidate(cacheKey);
    }
    
    public static void clearCache() {
        ResponseCache.clear();
    }
}