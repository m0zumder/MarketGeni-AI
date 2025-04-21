package com.aimarketing.services;

import com.aimarketing.models.Client;
import com.aimarketing.services.agents.BaseAIAgent;
import com.aimarketing.services.AIService;
import com.aimarketing.services.CampaignService;
import com.aimarketing.services.ContentWritingService;
import com.aimarketing.services.SEOService;
import com.aimarketing.services.SocialMediaService;
import com.aimarketing.services.VideoEditingService;

import java.util.*;

public class ChatbotService extends BaseAIAgent {
    private final AIService aiService;
    private final CampaignService campaignService;
    private final ContentWritingService contentWritingService;
    private final SEOService seoService;
    private final SocialMediaService socialMediaService;
    private final VideoEditingService videoEditingService;
    
    private Map<String, List<String>> conversationHistory;
    private Map<String, Map<String, Object>> userContext;

    public ChatbotService(
            AIService aiService,
            CampaignService campaignService,
            ContentWritingService contentWritingService,
            SEOService seoService,
            SocialMediaService socialMediaService,
            VideoEditingService videoEditingService) {
        this.aiService = aiService;
        this.campaignService = campaignService;
        this.contentWritingService = contentWritingService;
        this.seoService = seoService;
        this.socialMediaService = socialMediaService;
        this.videoEditingService = videoEditingService;
        
        this.conversationHistory = new HashMap<>();
        this.userContext = new HashMap<>();
    }

    public String processUserInput(String userId, String userInput) {
        // Store conversation history
        conversationHistory.computeIfAbsent(userId, k -> new ArrayList<>()).add(userInput);
        
        // Analyze intent and sentiment
        String intent = analyzeIntent(userInput);
        float sentiment = analyzeSentiment(userInput);
        
        // Update user context
        updateUserContext(userId, intent, sentiment);
        
        // Generate appropriate response
        return generateResponse(userId, intent);
    }

    private String analyzeIntent(String input) {
        // Identify user intent categories:
        // - General inquiry
        // - Service specific questions
        // - Pricing inquiry
        // - Feature inquiry
        // - Case studies request
        if (input.toLowerCase().contains("price") || input.toLowerCase().contains("cost")) {
            return "PRICING_INQUIRY";
        } else if (input.toLowerCase().contains("campaign")) {
            return "CAMPAIGN_INQUIRY";
        } else if (input.toLowerCase().contains("content")) {
            return "CONTENT_INQUIRY";
        } else if (input.toLowerCase().contains("seo")) {
            return "SEO_INQUIRY";
        } else if (input.toLowerCase().contains("social")) {
            return "SOCIAL_MEDIA_INQUIRY";
        } else if (input.toLowerCase().contains("video")) {
            return "VIDEO_INQUIRY";
        }
        return "GENERAL_INQUIRY";
    }

    private float analyzeSentiment(String input) {
        // Use AI service to analyze sentiment
        // Returns value between -1 (negative) to 1 (positive)
        return aiService.analyzeSentiment(input);
    }

    private void updateUserContext(String userId, String intent, float sentiment) {
        Map<String, Object> context = userContext.computeIfAbsent(userId, k -> new HashMap<>());
        context.put("lastIntent", intent);
        context.put("lastSentiment", sentiment);
        context.put("lastInteraction", System.currentTimeMillis());
    }

    private String generateResponse(String userId, String intent) {
        Map<String, Object> context = userContext.get(userId);
        StringBuilder response = new StringBuilder();

        switch (intent) {
            case "PRICING_INQUIRY":
                response.append("Our pricing is customized based on your specific needs and campaign requirements. ");
                response.append("Would you like to schedule a consultation to discuss a personalized package? ");
                response.append("\nYou can view our pricing packages here: /pricing");
                break;

            case "CAMPAIGN_INQUIRY":
                response.append("Our AI-powered campaign management system helps you create and optimize marketing campaigns. ");
                response.append("We offer features like automated targeting, performance tracking, and real-time optimization. ");
                response.append("\nLearn more about our campaign services: /services/campaigns");
                break;

            case "CONTENT_INQUIRY":
                response.append("Our content writing service uses AI to create engaging, SEO-optimized content. ");
                response.append("We can help with blog posts, social media content, and marketing materials. ");
                response.append("\nExplore our content services: /services/content");
                break;

            case "SEO_INQUIRY":
                response.append("Our SEO service combines AI analysis with expert optimization strategies. ");
                response.append("We help improve your search rankings and organic traffic. ");
                response.append("\nLearn about our SEO solutions: /services/seo");
                break;

            case "SOCIAL_MEDIA_INQUIRY":
                response.append("Our social media management service helps you engage with your audience effectively. ");
                response.append("We offer content planning, scheduling, and performance analytics. ");
                response.append("\nDiscover our social media services: /services/social-media");
                break;

            case "VIDEO_INQUIRY":
                response.append("Our video editing service creates professional marketing videos. ");
                response.append("We handle everything from concept to final production. ");
                response.append("\nCheck out our video services: /services/video");
                break;

            default:
                response.append("Welcome to AI Marketing Platform! ");
                response.append("We offer comprehensive marketing solutions including campaign management, ");
                response.append("content creation, SEO optimization, social media management, and video production. ");
                response.append("\nHow can I help you today?");
        }

        // Add personalized recommendations based on context
        addPersonalizedRecommendations(response, context);

        return response.toString();
    }

    private void addPersonalizedRecommendations(StringBuilder response, Map<String, Object> context) {
        String lastIntent = (String) context.get("lastIntent");
        float lastSentiment = (float) context.get("lastSentiment");

        // Add recommendations based on user context and sentiment
        if (lastSentiment < 0) {
            response.append("\n\nI notice you might have some concerns. ");
            response.append("Would you like to speak with one of our marketing specialists? ");
            response.append("They can provide more detailed information and address any specific concerns.");
        } else {
            response.append("\n\nBased on your interests, you might also like: ");
            switch (lastIntent) {
                case "CAMPAIGN_INQUIRY":
                    response.append("\n- Our content writing service to create campaign materials");
                    response.append("\n- Social media management to amplify your campaign");
                    break;
                case "CONTENT_INQUIRY":
                    response.append("\n- Our SEO service to optimize your content");
                    response.append("\n- Campaign management to distribute your content");
                    break;
                case "SEO_INQUIRY":
                    response.append("\n- Content writing service for SEO-optimized content");
                    response.append("\n- Analytics tools to track your SEO performance");
                    break;
            }
        }
    }

    public void clearUserContext(String userId) {
        conversationHistory.remove(userId);
        userContext.remove(userId);
    }
}