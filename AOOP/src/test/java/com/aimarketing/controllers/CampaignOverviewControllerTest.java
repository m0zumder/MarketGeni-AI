package com.aimarketing.controllers;

import com.aimarketing.models.Campaign;
import com.aimarketing.services.AIService;
import com.aimarketing.services.AnalyticsService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.testfx.assertions.api.Assertions.assertThat;

@ExtendWith(ApplicationExtension.class)
public class CampaignOverviewControllerTest {
    
    @Mock
    private AIService aiService;
    
    @Mock
    private AnalyticsService analyticsService;
    
    private CampaignOverviewController controller;
    
    @Start
    private void start(Stage stage) throws Exception {
        MockitoAnnotations.openMocks(this);
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CampaignOverview.fxml"));
        Parent root = loader.load();
        controller = loader.getController();
        
        // Initialize mocked services
        controller.setAiService(aiService);
        controller.setAnalyticsService(analyticsService);
        
        stage.setScene(new Scene(root));
        stage.show();
    }
    
    @Test
    void updateMetrics_ValidData_UpdatesUI(FxRobot robot) {
        // Arrange
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("engagement", 0.85);
        metrics.put("conversion", 0.65);
        when(analyticsService.getLatestMetrics(any())).thenReturn(metrics);
        
        // Act
        robot.clickOn("#refreshButton");
        
        // Assert
        Label engagementLabel = robot.lookup("#engagementMetric").queryAs(Label.class);
        Label conversionLabel = robot.lookup("#conversionMetric").queryAs(Label.class);
        
        assertThat(engagementLabel).hasText("85%");
        assertThat(conversionLabel).hasText("65%");
    }
    
    @Test
    void updateABTestingResults_TestEnabled_ShowsResults(FxRobot robot) {
        // Arrange
        Campaign campaign = new Campaign();
        campaign.setAbTestSetting("enabled", true);
        campaign.setAbTestSetting("variantA", "Original Content");
        campaign.setAbTestSetting("variantB", "Test Content");
        
        Map<String, Object> testResults = new HashMap<>();
        testResults.put("variantAMetrics", Map.of("engagement", 0.75));
        testResults.put("variantBMetrics", Map.of("engagement", 0.82));
        when(aiService.getABTestResults(any())).thenReturn(testResults);
        
        // Act
        controller.setCampaign(campaign);
        robot.clickOn("#updateTestResults");
        
        // Assert
        assertThat(robot.lookup("#testResultsContainer").queryAs(Parent.class)).isVisible();
        assertThat(robot.lookup("#variantAMetrics").queryAs(Label.class)).hasText("75%");
        assertThat(robot.lookup("#variantBMetrics").queryAs(Label.class)).hasText("82%");
    }
    
    @Test
    void optimizeCampaign_ValidParameters_StartsOptimization(FxRobot robot) {
        // Arrange
        Campaign campaign = new Campaign();
        Map<String, Object> optimizationResult = new HashMap<>();
        optimizationResult.put("status", "started");
        optimizationResult.put("jobId", "opt-123");
        when(aiService.optimizeCampaign(any(), any(), any())).thenReturn(optimizationResult);
        
        // Act
        controller.setCampaign(campaign);
        robot.clickOn("#optimizeButton");
        
        // Assert
        Button optimizeButton = robot.lookup("#optimizeButton").queryAs(Button.class);
        Label statusLabel = robot.lookup("#optimizationStatus").queryAs(Label.class);
        
        assertThat(optimizeButton).isDisabled();
        assertThat(statusLabel).hasText("Optimization in progress");
    }
}