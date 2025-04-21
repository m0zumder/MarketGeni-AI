package com.aimarketing.controllers;

import com.aimarketing.models.Campaign;
import com.aimarketing.services.AnalyticsService;
import com.aimarketing.services.AIService;
import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.collections.FXCollections;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AnalyticsDashboardController {
    @FXML private LineChart<String, Number> metricsChart;
    @FXML private PieChart conversionChart;
    @FXML private TableView<Map<String, Object>> metricsTable;
    @FXML private TextArea insightsArea;
    @FXML private ComboBox<String> timeRangeCombo;
    @FXML private ComboBox<String> metricTypeCombo;
    @FXML private Button exportPdfButton;
    @FXML private Button exportCsvButton;
    
    private AnalyticsService analyticsService;
    private AIService aiService;
    private Campaign currentCampaign;
    private Timer refreshTimer;
    
    @FXML
    public void initialize() {
        analyticsService = new AnalyticsService();
        aiService = new AIService();
        
        initializeControls();
        setupRefreshTimer();
    }
    
    private void initializeControls() {
        // Initialize time range options
        timeRangeCombo.setItems(FXCollections.observableArrayList(
            "Today", "Last 7 Days", "Last 30 Days", "Custom Range"
        ));
        timeRangeCombo.getSelectionModel().selectFirst();
        
        // Initialize metric type options
        metricTypeCombo.setItems(FXCollections.observableArrayList(
            "Ad Spend", "Clicks", "Conversions", "ROI", "CTR"
        ));
        metricTypeCombo.getSelectionModel().selectFirst();
        
        // Setup chart axes
        metricsChart.getXAxis().setLabel("Time");
        metricsChart.getYAxis().setLabel("Value");
        
        // Setup event handlers
        timeRangeCombo.setOnAction(e -> refreshDashboard());
        metricTypeCombo.setOnAction(e -> updateMetricsChart());
        exportPdfButton.setOnAction(e -> exportReport("pdf"));
        exportCsvButton.setOnAction(e -> exportReport("csv"));
    }
    
    private void setupRefreshTimer() {
        refreshTimer = new Timer(true);
        refreshTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                javafx.application.Platform.runLater(() -> refreshDashboard());
            }
        }, 0, 60000); // Refresh every minute
    }
    
    public void setCampaign(Campaign campaign) {
        this.currentCampaign = campaign;
        refreshDashboard();
    }
    
    private void refreshDashboard() {
        if (currentCampaign == null) return;
        
        updateMetricsChart();
        updateConversionChart();
        updateMetricsTable();
        updateAIInsights();
    }
    
    private void updateMetricsChart() {
        String metricType = metricTypeCombo.getValue();
        String timeRange = timeRangeCombo.getValue();
        
        Map<String, List<Double>> trends = analyticsService.getMetricsTrend(
            currentCampaign,
            metricType.toLowerCase().replace(" ", "_"),
            getTimeRangeDays(timeRange)
        );
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(metricType);
        
        // Add data points to series
        List<Double> values = trends.get("values");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");
        LocalDateTime endDate = LocalDateTime.now();
        
        for (int i = 0; i < values.size(); i++) {
            LocalDateTime date = endDate.minusDays(values.size() - i - 1);
            series.getData().add(new XYChart.Data<>(date.format(formatter), values.get(i)));
        }
        
        metricsChart.getData().clear();
        metricsChart.getData().add(series);
    }
    
    private void updateConversionChart() {
        Map<String, Double> metrics = analyticsService.calculateCampaignMetrics(currentCampaign);
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Conversions", metrics.get("conversions")),
            new PieChart.Data("Non-Conversions", metrics.get("clicks") - metrics.get("conversions"))
        );
        
        conversionChart.setData(pieChartData);
    }
    
    private void updateMetricsTable() {
        Map<String, Double> metrics = analyticsService.calculateCampaignMetrics(currentCampaign);
        ObservableList<Map<String, Object>> tableData = FXCollections.observableArrayList();
        
        for (Map.Entry<String, Double> entry : metrics.entrySet()) {
            Map<String, Object> row = new HashMap<>();
            row.put("metric", formatMetricName(entry.getKey()));
            row.put("value", formatMetricValue(entry.getKey(), entry.getValue()));
            tableData.add(row);
        }
        
        metricsTable.setItems(tableData);
    }
    
    private void updateAIInsights() {
        Map<String, Object> report = analyticsService.generateCampaignReport(currentCampaign);
        @SuppressWarnings("unchecked")
        List<String> insights = (List<String>) report.get("ai_insights");
        
        StringBuilder sb = new StringBuilder();
        sb.append("AI-Driven Insights:\n\n");
        
        for (String insight : insights) {
            sb.append("• ").append(insight).append("\n");
        }
        
        insightsArea.setText(sb.toString());
    }
    
    private void exportReport(String format) {
        // TODO: Implement report export functionality
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Report");
        alert.setHeaderText(null);
        alert.setContentText("Exporting report in " + format.toUpperCase() + " format...");
        alert.showAndWait();
    }
    
    private int getTimeRangeDays(String timeRange) {
        switch (timeRange) {
            case "Today": return 1;
            case "Last 7 Days": return 7;
            case "Last 30 Days": return 30;
            default: return 30;
        }
    }
    
    private String formatMetricName(String key) {
        return key.substring(0, 1).toUpperCase() +
               key.substring(1).replace('_', ' ');
    }
    
    private String formatMetricValue(String key, Double value) {
        switch (key.toLowerCase()) {
            case "ctr":
            case "conversion_rate":
                return String.format("%.2f%%", value * 100);
            case "cpa":
            case "ad_spend":
                return String.format("$%.2f", value);
            case "roas":
                return String.format("%.2fx", value);
            default:
                return String.format("%.0f", value);
        }
    }
    
    @Override
    protected void finalize() {
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }
}