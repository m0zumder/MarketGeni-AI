package com.aimarketing.controllers.api;

import com.aimarketing.models.Campaign;
import com.aimarketing.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class MarketingAPIControllerTest {
    
    @Mock
    private AIService aiService;
    
    @Mock
    private SEOService seoService;
    
    @Mock
    private ContentWritingService contentWritingService;
    
    @Mock
    private SocialMediaService socialMediaService;
    
    @Mock
    private VideoEditingService videoEditingService;
    
    private MarketingAPIController marketingAPIController;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        marketingAPIController = new MarketingAPIController();
    }
    
    @Test
    void optimizeCampaign_ValidRequest_ReturnsSuccess() {
        // Arrange
        Campaign campaign = new Campaign();
        String targetMetric = "engagement";
        double learningRate = 0.1;
        
        // Act
        ResponseEntity<Map<String, Object>> response = 
            marketingAPIController.optimizeCampaign(campaign, targetMetric, learningRate);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
    }
    
    @Test
    void analyzeSEO_ValidContent_ReturnsAnalysis() {
        // Arrange
        Map<String, String> content = new HashMap<>();
        content.put("content", "Test content for SEO analysis");
        
        Map<String, Object> expectedAnalysis = new HashMap<>();
        expectedAnalysis.put("score", 85);
        when(seoService.analyzeContent(any())).thenReturn(expectedAnalysis);
        
        // Act
        ResponseEntity<Map<String, Object>> response = 
            marketingAPIController.analyzeSEO(content);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    
    @Test
    void generateContent_ValidRequest_ReturnsContent() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("topic", "AI Marketing");
        request.put("type", "blog");
        request.put("wordCount", 500);
        
        String expectedContent = "Generated content for testing";
        when(contentWritingService.generateContent(any(), any(), any()))
            .thenReturn(expectedContent);
        
        // Act
        ResponseEntity<Map<String, Object>> response = 
            marketingAPIController.generateContent(request);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
    }
    
    @Test
    void analyzeSocialMedia_ValidCampaign_ReturnsAnalysis() {
        // Arrange
        Campaign campaign = new Campaign();
        
        Map<String, Object> expectedAnalysis = new HashMap<>();
        expectedAnalysis.put("engagement", 0.75);
        when(socialMediaService.analyzeCampaign(any())).thenReturn(expectedAnalysis);
        
        // Act
        ResponseEntity<Map<String, Object>> response = 
            marketingAPIController.analyzeSocialMedia(campaign);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
    
    @Test
    void editVideo_ValidRequest_ReturnsEditedUrl() {
        // Arrange
        Map<String, Object> request = new HashMap<>();
        request.put("videoUrl", "http://example.com/video.mp4");
        request.put("effects", Arrays.asList("trim", "filter"));
        
        String expectedUrl = "http://example.com/edited-video.mp4";
        when(videoEditingService.editVideo(any(), any())).thenReturn(expectedUrl);
        
        // Act
        ResponseEntity<Map<String, Object>> response = 
            marketingAPIController.editVideo(request);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("success", response.getBody().get("status"));
    }
    
    @Test
    void predictMetrics_ValidRequest_ReturnsPredictions() {
        // Arrange
        Campaign campaign = new Campaign();
        List<String> metrics = Arrays.asList("engagement", "conversion");
        
        Map<String, Double> expectedPredictions = new HashMap<>();
        expectedPredictions.put("engagement", 0.8);
        expectedPredictions.put("conversion", 0.6);
        when(aiService.predictMetrics(any(), any())).thenReturn(expectedPredictions);
        
        // Act
        ResponseEntity<Map<String, Double>> response = 
            marketingAPIController.predictMetrics(campaign, metrics);
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("engagement"));
        assertTrue(response.getBody().containsKey("conversion"));
    }
}