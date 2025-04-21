package com.aimarketing.apis.http;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class RetryableHttpClient extends HttpClient {
    private final RetryConfig retryConfig;
    
    public RetryableHttpClient() {
        this(RetryConfig.DEFAULT);
    }
    
    public RetryableHttpClient(RetryConfig retryConfig) {
        this.retryConfig = retryConfig;
    }
    
    @Override
    public static Response get(String url, Map<String, String> headers) throws IOException {
        return withRetry(() -> HttpClient.get(url, headers));
    }
    
    @Override
    public static Response post(String url, Map<String, String> headers, String body) throws IOException {
        return withRetry(() -> HttpClient.post(url, headers, body));
    }
    
    @Override
    public static Response put(String url, Map<String, String> headers, String body) throws IOException {
        return withRetry(() -> HttpClient.put(url, headers, body));
    }
    
    @Override
    public static Response delete(String url, Map<String, String> headers) throws IOException {
        return withRetry(() -> HttpClient.delete(url, headers));
    }
    
    private static Response withRetry(RequestExecutor executor) throws IOException {
        int attempts = 0;
        IOException lastException = null;
        
        while (attempts <= retryConfig.getMaxRetries()) {
            try {
                Response response = executor.execute();
                if (response.getStatusCode() < 500) {
                    return response;
                }
                lastException = new IOException("Server error: " + response.getStatusCode());
            } catch (IOException e) {
                lastException = e;
            }
            
            attempts++;
            if (attempts <= retryConfig.getMaxRetries()) {
                try {
                    TimeUnit.MILLISECONDS.sleep(retryConfig.getDelayForAttempt(attempts));
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw lastException;
                }
            }
        }
        
        throw lastException;
    }
    
    @FunctionalInterface
    private interface RequestExecutor {
        Response execute() throws IOException;
    }
}