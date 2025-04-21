package com.aimarketing.controllers;

import com.aimarketing.models.Campaign;
import com.aimarketing.services.AIService;
import com.aimarketing.services.CampaignService;
import com.aimarketing.services.AnalyticsService;
import com.aimarketing.services.PDFReportService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainViewController {

    @FXML private ListView<Campaign> campaignListView;
    @FXML private BorderPane campaignOverviewPane;
    @FXML private BorderPane analyticsPane;
    @FXML private Slider learningRateSlider;
    @FXML private ComboBox<String> optimizationTargetCombo;
    @FXML private Label statusLabel;
    @FXML private ProgressBar progressBar;

    private CampaignService campaignService;
    private AIService aiService;
    private AnalyticsService analyticsService;

    @FXML
    public void initialize() {
        campaignService = new CampaignService();
        aiService = new AIService();
        analyticsService = new AnalyticsService();

        // Initialize optimization targets
        ObservableList<String> targets = FXCollections.observableArrayList(
            "Conversion Rate",
            "Click-through Rate",
            "Cost per Acquisition",
            "Return on Ad Spend"
        );
        optimizationTargetCombo.setItems(targets);
        optimizationTargetCombo.getSelectionModel().selectFirst();

        // Load campaigns
        refreshCampaignList();
    }

    @FXML
    private void handleNewCampaign() {
        try {
            NewCampaignDialog dialog = new NewCampaignDialog();
            Optional<Campaign> result = dialog.showAndWait();
            
            if (result.isPresent()) {
                Campaign newCampaign = result.get();
                campaignService.saveCampaign(newCampaign);
                refreshCampaignList();
                
                // Select and load the new campaign
                campaignListView.getSelectionModel().select(newCampaign);
                loadCampaignDetails(newCampaign);
                
                showInfoAlert("Campaign Created", "New campaign has been created successfully!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to create new campaign: " + e.getMessage());
        }
    }

    @FXML
    private void handleOpenCampaign() {
        Campaign selected = campaignListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            loadCampaignDetails(selected);
        }
    }

    @FXML
    private void handleStartOptimization() {
        String target = optimizationTargetCombo.getValue();
        double learningRate = learningRateSlider.getValue();
        
        // TODO: Implement AI optimization logic
        showNotImplementedAlert("AI Optimization");
    }

    @FXML
    private void handleDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AnalyticsDashboard.fxml"));
            BorderPane dashboardPane = loader.load();
            AnalyticsDashboardController controller = loader.getController();
            
            // Set the selected campaign if any
            Campaign selectedCampaign = campaignListView.getSelectionModel().getSelectedItem();
            if (selectedCampaign != null) {
                controller.setCampaign(selectedCampaign);
            }
            
            analyticsPane.setCenter(dashboardPane);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error loading analytics dashboard", e.getMessage());
        }
    }

    @FXML
    private void handleReports() {
        Campaign selectedCampaign = campaignListView.getSelectionModel().getSelectedItem();
        if (selectedCampaign == null) {
            showErrorAlert("No Campaign Selected", "Please select a campaign to generate reports.");
            return;
        }
        
        try {
            Map<String, Object> report = analyticsService.generateCampaignReport(selectedCampaign);
            // Show report preview or export dialog
            showReportDialog(report);
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Error generating report", e.getMessage());
        }
    }
    
    private void showReportDialog(Map<String, Object> report) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Campaign Report");
        dialog.setHeaderText("Export Options");
        
        ButtonType pdfButton = new ButtonType("Export PDF", ButtonBar.ButtonData.LEFT);
        ButtonType csvButton = new ButtonType("Export CSV", ButtonBar.ButtonData.LEFT);
        ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        dialog.getDialogPane().getButtonTypes().addAll(pdfButton, csvButton, closeButton);
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        // Add report preview
        TextArea previewArea = new TextArea();
        previewArea.setEditable(false);
        previewArea.setPrefRowCount(10);
        previewArea.setText(formatReportPreview(report));
        
        content.getChildren().add(new Label("Report Preview:"));
        content.getChildren().add(previewArea);
        
        dialog.getDialogPane().setContent(content);
        
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent()) {
            if (result.get() == pdfButton) {
                exportReport(report, "pdf");
            } else if (result.get() == csvButton) {
                exportReport(report, "csv");
            }
        }
    }
    
    private String formatReportPreview(Map<String, Object> report) {
        StringBuilder preview = new StringBuilder();
        preview.append("Campaign Performance Summary\n\n");
        
        @SuppressWarnings("unchecked")
        Map<String, Double> metrics = (Map<String, Double>) report.get("current_metrics");
        if (metrics != null) {
            for (Map.Entry<String, Double> entry : metrics.entrySet()) {
                preview.append(formatMetricName(entry.getKey())).append(": ")
                       .append(formatMetricValue(entry.getKey(), entry.getValue()))
                       .append("\n");
            }
        }
        
        @SuppressWarnings("unchecked")
        List<String> insights = (List<String>) report.get("ai_insights");
        if (insights != null && !insights.isEmpty()) {
            preview.append("\nAI Insights:\n");
            for (String insight : insights) {
                preview.append("• ").append(insight).append("\n");
            }
        }
        
        return preview.toString();
    }
    
    private void exportReport(Map<String, Object> report, String format) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Report");
        
        String extension = format.equals("pdf") ? ".pdf" : ".csv";
        fileChooser.setInitialFileName("campaign_report" + extension);
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter(format.toUpperCase() + " Files", "*" + extension)
        );
        
        File file = fileChooser.showSaveDialog(analyticsPane.getScene().getWindow());
        if (file != null) {
            try {
                if (format.equals("pdf")) {
                    PDFReportService pdfService = new PDFReportService();
                    pdfService.exportToPdf(report, file);
                } else {
                    exportToCsv(report, file);
                }
                showInfoAlert("Export Successful", "Report has been exported to: " + file.getPath());
            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Export Failed", e.getMessage());
            }
        }
    }
    
    private void exportToCsv(Map<String, Object> report, File file) throws IOException {
        try (PrintWriter writer = new PrintWriter(file)) {
            // Write headers
            writer.println("Metric,Value");
            
            // Write metrics
            @SuppressWarnings("unchecked")
            Map<String, Double> metrics = (Map<String, Double>) report.get("current_metrics");
            if (metrics != null) {
                for (Map.Entry<String, Double> entry : metrics.entrySet()) {
                    writer.printf("%s,%s%n",
                        formatMetricName(entry.getKey()),
                        formatMetricValue(entry.getKey(), entry.getValue())
                    );
                }
            }
            
            // Write insights
            writer.println("\nAI Insights");
            @SuppressWarnings("unchecked")
            List<String> insights = (List<String>) report.get("ai_insights");
            if (insights != null) {
                for (String insight : insights) {
                    writer.println(insight);
                }
            }
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
    
    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showInfoAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About AI Marketing Platform");
        alert.setHeaderText(null);
        alert.setContentText("AI Marketing Platform v1.0\nPowered by Advanced AI Technology");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        // TODO: Implement proper cleanup
        System.exit(0);
    }

    private void refreshCampaignList() {
        ObservableList<Campaign> campaigns = FXCollections.observableArrayList(
            campaignService.getAllCampaigns()
        );
        campaignListView.setItems(campaigns);
        
        // Setup cell factory for custom display
        campaignListView.setCellFactory(lv -> new ListCell<Campaign>() {
            @Override
            protected void updateItem(Campaign campaign, boolean empty) {
                super.updateItem(campaign, empty);
                if (empty || campaign == null) {
                    setText(null);
                } else {
                    setText(campaign.getName() + " - " + campaign.getStatus());
                }
            }
        });
        
        // Add selection listener
        campaignListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    loadCampaignDetails(newValue);
                }
            }
        );
    }

    private void initializeGuideSystem() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Guide.fxml"));
            VBox guidePane = loader.load();
            guideController = loader.getController();

            // Register guide targets
            guideController.registerGuideTarget("newCampaign", campaignListView);
            guideController.registerGuideTarget("campaignList", campaignListView);
            guideController.registerGuideTarget("optimization", optimizationTargetCombo);
            guideController.registerGuideTarget("dashboard", analyticsPane);
            guideController.registerGuideTarget("aiSettings", learningRateSlider);
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to initialize guide system: " + e.getMessage());
        }
    }

    @FXML
    private void showGuide() {
        Stage guideStage = new Stage();
        guideStage.initModality(Modality.NONE);
        guideStage.initStyle(StageStyle.DECORATED);
        guideStage.setTitle("AI Marketing Platform Guide");
        
        Scene guideScene = new Scene(guideController.getGuideBox());
        guideScene.getStylesheets().add(getClass().getResource("/styles/guide.css").toExternalForm());
        
        guideStage.setScene(guideScene);
        guideStage.show();
    }

    private void loadCampaignDetails(Campaign campaign) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CampaignOverview.fxml"));
            BorderPane overviewPane = loader.load();
            CampaignOverviewController controller = loader.getController();
            controller.setCampaign(campaign);
            
            campaignOverviewPane.setCenter(overviewPane);
            
            // Update status
            statusLabel.setText("Loaded campaign: " + campaign.getName());
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Error", "Failed to load campaign details: " + e.getMessage());
        }
    }
}