package com.aimarketing.controllers;

import com.aimarketing.models.Campaign;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.time.LocalDateTime;

public class NewCampaignDialog extends Dialog<Campaign> {
    @FXML private TextField nameField;
    @FXML private TextArea descriptionArea;
    @FXML private TextField budgetField;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;

    public NewCampaignDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/NewCampaignDialog.fxml"));
            loader.setController(this);
            
            DialogPane dialogPane = getDialogPane();
            dialogPane.setContent(loader.load());
            
            setTitle("Create New Campaign");
            setHeaderText("Enter Campaign Details");
            
            ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
            dialogPane.getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
            
            setResultConverter(buttonType -> {
                if (buttonType == createButtonType) {
                    return createCampaign();
                }
                return null;
            });
            
            // Initialize UI components
            Node createButton = dialogPane.lookupButton(createButtonType);
            createButton.setDisable(true);
            
            // Add validation
            nameField.textProperty().addListener((observable, oldValue, newValue) -> {
                validateForm();
            });
            
            budgetField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*(\\.\\d*)?")) {
                    budgetField.setText(oldValue);
                }
                validateForm();
            });
            
            targetAudienceCombo.valueProperty().addListener((observable, oldValue, newValue) -> {
                validateForm();
            });
            
            startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                validateForm();
            });
            
            endDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
                validateForm();
            });
            
            private void validateForm() {
                boolean isValid = nameField.getText() != null && !nameField.getText().trim().isEmpty() &&
                                budgetField.getText() != null && !budgetField.getText().trim().isEmpty() &&
                                targetAudienceCombo.getValue() != null &&
                                startDatePicker.getValue() != null &&
                                endDatePicker.getValue() != null;
                
                if (isValid && startDatePicker.getValue() != null && endDatePicker.getValue() != null) {
                    isValid = !startDatePicker.getValue().isAfter(endDatePicker.getValue());
                }
                
                createButton.setDisable(!isValid);
            }
            });
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Campaign createCampaign() {
        try {
            String name = nameField.getText().trim();
            String description = descriptionArea.getText().trim();
            double budget = Double.parseDouble(budgetField.getText());
            
            Campaign campaign = new Campaign(name, description, budget);
            
            if (startDatePicker.getValue() != null) {
                campaign.setStartDate(startDatePicker.getValue().atStartOfDay());
            }
            if (endDatePicker.getValue() != null) {
                campaign.setEndDate(endDatePicker.getValue().atStartOfDay());
            }
            
            // Set target audience and AI optimization settings
            campaign.setTargetAudience(targetAudienceCombo.getValue());
            campaign.setAiOptimizationEnabled(aiOptimizationCheckbox.isSelected());
            
            // Configure A/B testing if enabled
            if (abTestingCheckbox.isSelected()) {
                campaign.setAbTestSetting("enabled", true);
                campaign.setAbTestSetting("createdAt", LocalDateTime.now());
                campaign.setAbTestSetting("status", "pending");
            }
            
            return campaign;
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a valid budget amount.");
            alert.showAndWait();
            return null;
        }
    }
}