package com.aimarketing.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.util.HashMap;
import java.util.Map;

public class GuideController {
    private Map<String, String> tooltipContent;
    private Map<String, Node> guideTargets;
    private Popup guidePopup;
    private int currentStep;
    private String[] guidePath;

    @FXML
    private VBox guideBox;

    @FXML
    public void initialize() {
        tooltipContent = new HashMap<>();
        guideTargets = new HashMap<>();
        setupTooltipContent();
        initializeGuidePopup();
    }

    private void setupTooltipContent() {
        // Campaign Management
        tooltipContent.put("newCampaign", "Create a new marketing campaign with AI-powered optimization");
        tooltipContent.put("campaignList", "View and manage your existing campaigns");
        tooltipContent.put("optimization", "Configure AI settings to optimize campaign performance");

        // Analytics
        tooltipContent.put("dashboard", "View comprehensive analytics and campaign insights");
        tooltipContent.put("metrics", "Track key performance indicators and ROI");
        tooltipContent.put("reports", "Generate detailed campaign performance reports");

        // AI Features
        tooltipContent.put("aiSettings", "Configure AI model parameters and learning rate");
        tooltipContent.put("automation", "Set up automated campaign optimization rules");
    }

    private void initializeGuidePopup() {
        guidePopup = new Popup();
        VBox popupContent = new VBox(10);
        popupContent.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #2196f3; -fx-border-radius: 5;");
        popupContent.setAlignment(Pos.CENTER);

        Label titleLabel = new Label();
        titleLabel.setStyle("-fx-font-size: 16; -fx-font-weight: bold;");

        Label descriptionLabel = new Label();
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(300);

        Button nextButton = new Button("Next");
        nextButton.setOnAction(e -> showNextStep());
        nextButton.getStyleClass().add("primary-button");

        popupContent.getChildren().addAll(titleLabel, descriptionLabel, nextButton);
        guidePopup.getContent().add(popupContent);
    }

    public void startGuide(String guidePath) {
        switch (guidePath) {
            case "campaign":
                startCampaignGuide();
                break;
            case "analytics":
                startAnalyticsGuide();
                break;
            case "ai":
                startAIFeatureGuide();
                break;
        }
    }

    private void startCampaignGuide() {
        guidePath = new String[]{"newCampaign", "campaignList", "optimization"};
        currentStep = 0;
        showCurrentStep();
    }

    private void startAnalyticsGuide() {
        guidePath = new String[]{"dashboard", "metrics", "reports"};
        currentStep = 0;
        showCurrentStep();
    }

    private void startAIFeatureGuide() {
        guidePath = new String[]{"aiSettings", "automation"};
        currentStep = 0;
        showCurrentStep();
    }

    private void showCurrentStep() {
        if (currentStep < guidePath.length) {
            String currentTarget = guidePath[currentStep];
            Node targetNode = guideTargets.get(currentTarget);
            if (targetNode != null) {
                showGuidePopup(targetNode, tooltipContent.get(currentTarget));
                highlightElement(targetNode);
            }
        } else {
            guidePopup.hide();
        }
    }

    private void showNextStep() {
        currentStep++;
        showCurrentStep();
    }

    private void showGuidePopup(Node target, String content) {
        VBox popupContent = (VBox) guidePopup.getContent().get(0);
        Label titleLabel = (Label) popupContent.getChildren().get(0);
        Label descriptionLabel = (Label) popupContent.getChildren().get(1);

        titleLabel.setText("Step " + (currentStep + 1));
        descriptionLabel.setText(content);

        guidePopup.show(target, target.localToScreen(target.getBoundsInLocal()).getMinX(),
                target.localToScreen(target.getBoundsInLocal()).getMaxY());
    }

    private void highlightElement(Node element) {
        StackPane highlight = new StackPane(element);
        highlight.setStyle("-fx-background-color: rgba(33, 150, 243, 0.3); -fx-background-radius: 5;");

        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), highlight);
        fadeTransition.setFromValue(0.3);
        fadeTransition.setToValue(0.1);
        fadeTransition.setCycleCount(2);
        fadeTransition.setAutoReverse(true);
        fadeTransition.play();
    }

    public void registerGuideTarget(String targetId, Node node) {
        guideTargets.put(targetId, node);
    }

    public void showTooltip(String targetId) {
        Node target = guideTargets.get(targetId);
        if (target != null) {
            Tooltip tooltip = new Tooltip(tooltipContent.get(targetId));
            Tooltip.install(target, tooltip);
        }
    }
}