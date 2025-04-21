package com.aimarketing.apis.http;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.nio.charset.StandardCharsets;

public class HttpClient {
    private static final int DEFAULT_TIMEOUT = 30000;
    
    public static class Response {
        private int statusCode;
        private String body;
        private Map<String, String> headers;
        
        public Response(int statusCode, String body, Map<String, String> headers) {
            this.statusCode = statusCode;
            this.body = body;
            this.headers = headers;
        }
        
        public int getStatusCode() { return statusCode; }
        public String getBody() { return body; }
        public Map<String, String> getHeaders() { return headers; }
    }
    
    public static Response get(String url, Map<String, String> headers) throws IOException {
        return sendRequest(url, "GET", headers, null);
    }
    
    public static Response post(String url, Map<String, String> headers, String body) throws IOException {
        return sendRequest(url, "POST", headers, body);
    }
    
    public static Response put(String url, Map<String, String> headers, String body) throws IOException {
        return sendRequest(url, "PUT", headers, body);
    }
    
    public static Response delete(String url, Map<String, String> headers) throws IOException {
        return sendRequest(url, "DELETE", headers, null);
    }
    
    private static Response sendRequest(String url, String method, Map<String, String> headers, String body) 
            throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(DEFAULT_TIMEOUT);
        connection.setReadTimeout(DEFAULT_TIMEOUT);
        
        // Set headers
        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        
        // Write body if present
        if (body != null) {
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = body.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }
        
        // Read response
        int statusCode = connection.getResponseCode();
        InputStream inputStream = statusCode >= 400 ? 
            connection.getErrorStream() : connection.getInputStream();
        
        StringBuilder responseBody = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                responseBody.append(line);
            }
        }
        
        return new Response(statusCode, responseBody.toString(), headers);
    }
}