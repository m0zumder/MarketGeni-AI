package com.aimarketing.controllers.api;

import com.aimarketing.services.VideoEditingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class VideoAPIControllerTest {
    
    @Mock
    private VideoEditingService videoEditingService;
    
    private VideoAPIController videoAPIController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        videoAPIController = new VideoAPIController();
    }
    
    @Test
    void editVideo_ValidRequest_ReturnsSuccess() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("videoUrl", "http://example.com/video.mp4");
        request.put("params", new HashMap<String, Object>());
        String token = "valid-token";
        
        Map<String, Object> expectedResult = new HashMap<>();
        expectedResult.put("jobId", "123");
        when(videoEditingService.editVideo(any(), any())).thenReturn(expectedResult);
        
        // Act
        ResponseEntity<Map<String, Object>> response = videoAPIController.editVideo(request, token);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("data"));
    }
    
    @Test
    void analyzeVideo_ValidRequest_ReturnsAnalysis() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("videoUrl", "http://example.com/video.mp4");
        request.put("analysisType", "engagement");
        String token = "valid-token";
        
        Map<String, Object> expectedAnalysis = new HashMap<>();
        expectedAnalysis.put("engagement", 0.85);
        when(videoEditingService.analyzeVideo(any(), any())).thenReturn(expectedAnalysis);
        
        // Act
        ResponseEntity<Map<String, Object>> response = videoAPIController.analyzeVideo(request, token);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("data"));
    }
    
    @Test
    void getVideoJobStatus_ValidJobId_ReturnsStatus() {
        // Arrange
        String jobId = "123";
        String token = "valid-token";
        
        Map<String, Object> expectedStatus = new HashMap<>();
        expectedStatus.put("status", "completed");
        when(videoEditingService.getJobStatus(jobId)).thenReturn(expectedStatus);
        
        // Act
        ResponseEntity<Map<String, Object>> response = videoAPIController.getVideoJobStatus(jobId, token);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("data"));
    }
    
    @Test
    void editVideo_InvalidRequest_ReturnsBadRequest() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        String token = "valid-token";
        
        // Act
        ResponseEntity<Map<String, Object>> response = videoAPIController.editVideo(request, token);
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }
}