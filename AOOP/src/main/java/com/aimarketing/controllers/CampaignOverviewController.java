package com.aimarketing.controllers;

import com.aimarketing.models.Campaign;
import com.aimarketing.services.AnalyticsService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.Map;

public class CampaignOverviewController {
    @FXML private Label nameLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label budgetLabel;
    @FXML private Label statusLabel;
    @FXML private GridPane metricsGrid;
    @FXML private TextArea insightsArea;
    
    private AnalyticsService analyticsService;
    private Campaign currentCampaign;
    
    @FXML
    public void initialize() {
        analyticsService = new AnalyticsService();
    }
    
    public void setCampaign(Campaign campaign) {
        this.currentCampaign = campaign;
        updateCampaignDisplay();
    }
    
    private void updateCampaignDisplay() {
        if (currentCampaign == null) return;
        
        // Update basic info
        nameLabel.setText(currentCampaign.getName());
        descriptionLabel.setText(currentCampaign.getDescription());
        budgetLabel.setText(String.format("$%.2f", currentCampaign.getBudget()));
        statusLabel.setText(currentCampaign.getStatus());
        
        // Update metrics
        Map<String, Double> metrics = analyticsService.calculateCampaignMetrics(currentCampaign);
        updateMetricsDisplay(metrics);
        
        // Update AI insights and optimization status
        Map<String, Object> report = analyticsService.generateCampaignReport(currentCampaign);
        updateAIOptimizationStatus(report);
        
        // Update A/B testing results if enabled
        if (currentCampaign.getAbTestSettings().containsKey("enabled") && 
            (Boolean)currentCampaign.getAbTestSettings().get("enabled")) {
            updateABTestingResults();
        }
        
        // Update target audience insights
        if (currentCampaign.getTargetAudience() != null) {
            updateTargetAudienceInsights();
        }
    }
    
    private void updateMetricsDisplay(Map<String, Double> metrics) {
        metricsGrid.getChildren().clear();
        int row = 0;
        
        for (Map.Entry<String, Double> entry : metrics.entrySet()) {
            Label keyLabel = new Label(formatMetricKey(entry.getKey()) + ":");
            Label valueLabel = new Label(formatMetricValue(entry.getKey(), entry.getValue()));
            
            metricsGrid.add(keyLabel, 0, row);
            metricsGrid.add(valueLabel, 1, row);
            row++;
        }
    }
    
    private void updateInsightsDisplay(java.util.List<String> insights) {
        StringBuilder sb = new StringBuilder();
        sb.append("AI Insights:\n\n");
        
        for (String insight : insights) {
            sb.append("• ").append(insight).append("\n");
        }
        
        insightsArea.setText(sb.toString());
    }
    
    private String formatMetricKey(String key) {
        return key.substring(0, 1).toUpperCase() +
               key.substring(1).replace('_', ' ');
    }
    
    private String formatMetricValue(String key, Double value) {
        switch (key.toLowerCase()) {
            case "ctr":
            case "conversion_rate":
                return String.format("%.2f%%", value * 100);
            case "cpa":
                return String.format("$%.2f", value);
            case "roas":
                return String.format("%.2fx", value);
            default:
                return String.format("%.0f", value);
        }
    }
}