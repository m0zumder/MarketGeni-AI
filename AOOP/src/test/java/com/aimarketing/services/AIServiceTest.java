package com.aimarketing.services;

import com.aimarketing.models.Campaign;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class AIServiceTest {
    
    private AIService aiService;
    
    @BeforeEach
    void setUp() {
        aiService = new AIService();
    }
    
    @Test
    void optimizeCampaign_ValidParameters_ReturnsOptimizationResult() {
        // Arrange
        Campaign campaign = new Campaign();
        String targetMetric = "engagement";
        double learningRate = 0.1;
        
        // Act
        Map<String, Object> result = aiService.optimizeCampaign(campaign, targetMetric, learningRate);
        
        // Assert
        assertNotNull(result);
        assertTrue(result.containsKey("status"));
        assertTrue(result.containsKey("jobId"));
    }
    
    @Test
    void predictMetrics_ValidInput_ReturnsPredictions() {
        // Arrange
        Campaign campaign = new Campaign();
        List<String> metrics = Arrays.asList("engagement", "conversion");
        
        // Act
        Map<String, Double> predictions = aiService.predictMetrics(campaign, metrics);
        
        // Assert
        assertNotNull(predictions);
        assertTrue(predictions.containsKey("engagement"));
        assertTrue(predictions.containsKey("conversion"));
        assertTrue(predictions.get("engagement") >= 0 && predictions.get("engagement") <= 1);
        assertTrue(predictions.get("conversion") >= 0 && predictions.get("conversion") <= 1);
    }
    
    @Test
    void getABTestResults_TestEnabled_ReturnsResults() {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setAbTestSetting("enabled", true);
        campaign.setAbTestSetting("variantA", "Original Content");
        campaign.setAbTestSetting("variantB", "Test Content");
        
        // Act
        Map<String, Object> results = aiService.getABTestResults(campaign);
        
        // Assert
        assertNotNull(results);
        assertTrue(results.containsKey("variantAMetrics"));
        assertTrue(results.containsKey("variantBMetrics"));
    }
    
    @Test
    void updateModel_ValidData_ReturnsSuccess() {
        // Arrange
        Campaign campaign = new Campaign();
        Map<String, Object> trainingData = new HashMap<>();
        trainingData.put("features", Arrays.asList("content_length", "keyword_density"));
        trainingData.put("target", "engagement");
        
        // Act
        boolean result = aiService.updateModel(campaign, trainingData);
        
        // Assert
        assertTrue(result);
        assertEquals("success", campaign.getAiSetting("modelUpdateStatus"));
    }
    
    @Test
    void optimizeCampaign_InvalidParameters_ThrowsException() {
        // Arrange
        Campaign campaign = new Campaign();
        String invalidMetric = "invalid_metric";
        double learningRate = -0.1;
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            aiService.optimizeCampaign(campaign, invalidMetric, learningRate);
        });
    }
    
    @Test
    void predictMetrics_EmptyMetricsList_ThrowsException() {
        // Arrange
        Campaign campaign = new Campaign();
        List<String> emptyMetrics = Arrays.asList();
        
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            aiService.predictMetrics(campaign, emptyMetrics);
        });
    }
}