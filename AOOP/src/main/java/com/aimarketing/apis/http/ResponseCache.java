package com.aimarketing.apis.http;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ResponseCache {
    private static final Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    
    private static class CacheEntry {
        private final Response response;
        private final long expirationTime;
        
        public CacheEntry(Response response, long ttlMillis) {
            this.response = response;
            this.expirationTime = System.currentTimeMillis() + ttlMillis;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expirationTime;
        }
        
        public Response getResponse() {
            return response;
        }
    }
    
    public static void put(String key, Response response, long ttl, TimeUnit unit) {
        cache.put(key, new CacheEntry(response, unit.toMillis(ttl)));
    }
    
    public static Response get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null || entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.getResponse();
    }
    
    public static void invalidate(String key) {
        cache.remove(key);
    }
    
    public static void clear() {
        cache.clear();
    }
    
    public static String generateKey(String url, Map<String, String> headers, String body) {
        StringBuilder key = new StringBuilder(url);
        if (headers != null) {
            headers.forEach((k, v) -> key.append(":").append(k).append("=").append(v));
        }
        if (body != null) {
            key.append(":").append(body);
        }
        return key.toString();
    }
}