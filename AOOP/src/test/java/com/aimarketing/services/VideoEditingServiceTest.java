package com.aimarketing.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class VideoEditingServiceTest {
    
    private VideoEditingService videoEditingService;
    
    @BeforeEach
    void setUp() {
        videoEditingService = new VideoEditingService();
    }
    
    @Test
    void editVideo_ValidParameters_ReturnsJobId() {
        // Arrange
        String videoUrl = "http://example.com/video.mp4";
        Map<String, Object> params = new HashMap<>();
        params.put("trim", Map.of("start", 0, "end", 60));
        params.put("filter", "brightness");
        
        // Act
        Map<String, Object> result = videoEditingService.editVideo(videoUrl, params);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("jobId"));
        assertTrue(result.containsKey("status"));
        assertEquals("pending", result.get("status"));
    }
    
    @Test
    void analyzeVideo_ValidParameters_ReturnsAnalysis() {
        // Arrange
        String videoUrl = "http://example.com/video.mp4";
        String analysisType = "engagement";
        
        // Act
        Map<String, Object> analysis = videoEditingService.analyzeVideo(videoUrl, analysisType);
        
        // Assert
        assertNotNull(analysis);
        assertTrue(analysis.containsKey("engagement"));
        assertTrue((Double) analysis.get("engagement") >= 0.0);
        assertTrue((Double) analysis.get("engagement") <= 1.0);
    }
    
    @Test
    void getJobStatus_ValidJobId_ReturnsStatus() {
        // Arrange
        String jobId = "test-job-123";
        
        // Act
        Map<String, Object> status = videoEditingService.getJobStatus(jobId);
        
        // Assert
        assertNotNull(status);
        assertTrue(status.containsKey("status"));
        assertTrue(status.containsKey("progress"));
    }
    
    @Test
    void editVideo_InvalidUrl_ThrowsException() {
        // Arrange
        String invalidUrl = "invalid-url";
        Map<String, Object> params = new HashMap<>();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            videoEditingService.editVideo(invalidUrl, params);
        });
    }
    
    @Test
    void analyzeVideo_UnsupportedType_ThrowsException() {
        // Arrange
        String videoUrl = "http://example.com/video.mp4";
        String invalidType = "unsupported";
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            videoEditingService.analyzeVideo(videoUrl, invalidType);
        });
    }
    
    @Test
    void getJobStatus_InvalidJobId_ThrowsException() {
        // Arrange
        String invalidJobId = "invalid-id";
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            videoEditingService.getJobStatus(invalidJobId);
        });
    }
}