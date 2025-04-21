package com.aimarketing.controllers;

import com.aimarketing.services.ChatbotService;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.paint.Color;
import javafx.application.Platform;

public class ChatbotController {
    @FXML
    private ScrollPane chatScrollPane;
    
    @FXML
    private VBox chatBox;
    
    @FXML
    private TextField userInput;
    
    private final ChatbotService chatbotService;
    private String currentUserId;
    
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
        this.currentUserId = "guest"; // This should be updated when user logs in
    }
    
    @FXML
    public void initialize() {
        // Show welcome message
        addBotMessage("Welcome to AI Marketing Platform! How can I assist you today?");
        
        // Configure user input handling
        userInput.setOnAction(event -> handleUserInput());
        
        // Make chat scroll to bottom when new messages are added
        chatBox.heightProperty().addListener((observable, oldValue, newValue) -> 
            chatScrollPane.setVvalue(1.0));
    }
    
    @FXML
    private void handleUserInput() {
        String message = userInput.getText().trim();
        if (!message.isEmpty()) {
            // Add user message to chat
            addUserMessage(message);
            
            // Clear input field
            userInput.clear();
            
            // Process user input and get response
            new Thread(() -> {
                String response = chatbotService.processUserInput(currentUserId, message);
                
                // Add bot response to chat on UI thread
                Platform.runLater(() -> addBotMessage(response));
            }).start();
        }
    }
    
    private void addUserMessage(String message) {
        TextFlow messageFlow = new TextFlow();
        Text text = new Text(message);
        text.setFill(Color.WHITE);
        messageFlow.getChildren().add(text);
        messageFlow.getStyleClass().add("user-message");
        
        Platform.runLater(() -> chatBox.getChildren().add(messageFlow));
    }
    
    private void addBotMessage(String message) {
        TextFlow messageFlow = new TextFlow();
        Text text = new Text(message);
        text.setFill(Color.BLACK);
        messageFlow.getChildren().add(text);
        messageFlow.getStyleClass().add("bot-message");
        
        Platform.runLater(() -> chatBox.getChildren().add(messageFlow));
    }
    
    public void setUserId(String userId) {
        this.currentUserId = userId;
        chatbotService.clearUserContext(currentUserId);
    }
}