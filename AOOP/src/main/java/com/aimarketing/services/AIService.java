package com.aimarketing.services;

import com.aimarketing.models.Campaign;
import com.aimarketing.services.agents.AgentManager;
import com.aimarketing.repositories.ContentRepository;
import com.aimarketing.repositories.AnalyticsRepository;
import com.aimarketing.repositories.SEORepository;
import org.bson.Document;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

public class AIService {
    private final AgentManager agentManager;
    private final ContentRepository contentRepository;
    private final AnalyticsRepository analyticsRepository;
    private final SEORepository seoRepository;
    private final ExecutorService executorService;
    
    public AIService(ContentRepository contentRepository, AnalyticsRepository analyticsRepository, SEORepository seoRepository) {
        this.agentManager = new AgentManager();
        this.contentRepository = contentRepository;
        this.analyticsRepository = analyticsRepository;
        this.seoRepository = seoRepository;
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    }
    
    public void optimizeCampaign(Campaign campaign, String targetMetric, double learningRate) {
        if (campaign == null) {
            throw new IllegalArgumentException("Campaign cannot be null");
        }
        
        // Update campaign settings
        campaign.setAiSetting("lastOptimization", System.currentTimeMillis());
        campaign.setAiSetting("learningRate", learningRate);
        campaign.setAiSetting("targetMetric", targetMetric);
        
        // Optimize campaign using all available agents
        agentManager.optimizeCampaign(campaign, targetMetric);
    }
    
    public Map<String, Double> predictMetrics(Campaign campaign, List<String> metrics) {
        if (campaign == null) {
            throw new IllegalArgumentException("Campaign cannot be null");
        }
        return agentManager.predictMetrics(campaign, metrics);
    }
    
    public double predictMetric(Campaign campaign, String metric) {
        Map<String, Double> predictions = predictMetrics(campaign, Arrays.asList(metric));
        return predictions.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }
    
    public void updateModel(Campaign campaign) {
        if (campaign == null) {
            throw new IllegalArgumentException("Campaign cannot be null");
        }
        agentManager.updateAgentModels(campaign);
    }
    
    public CompletableFuture<Document> generateOptimizedContent(String campaignId, String contentType, Map<String, Object> parameters) {
        return CompletableFuture.supplyAsync(() -> {
            List<Document> existingContent = contentRepository.findByCampaignId(campaignId);
            List<Document> seoData = seoRepository.findKeywordRankings(campaignId);
            
            Map<String, Object> optimizedContent = new HashMap<>();
            optimizedContent.put("title", parameters.get("title"));
            optimizedContent.put("content", parameters.get("content"));
            optimizedContent.put("metadata", generateMetadata(parameters, seoData));

            contentRepository.saveContent(campaignId, contentType, optimizedContent);
            return contentRepository.getLatestContent(campaignId, contentType);
        }, executorService);
    }

    public CompletableFuture<List<Document>> analyzeContentPerformance(String campaignId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Document> contentMetrics = analyticsRepository.findByCampaignId(campaignId);
            List<Document> seoMetrics = seoRepository.findKeywordRankings(campaignId);
            
            Map<String, Object> analysisData = new HashMap<>();
            analysisData.put("timestamp", LocalDateTime.now());
            analysisData.put("metrics", aggregateMetrics(contentMetrics, seoMetrics));
            
            analyticsRepository.saveMetric(campaignId, "content_performance", analysisData);
            return analyticsRepository.findByCampaignId(campaignId);
        }, executorService);
    }

    public CompletableFuture<Document> optimizeSEOStrategy(String campaignId, List<String> targetKeywords) {
        return CompletableFuture.supplyAsync(() -> {
            List<Document> currentRankings = seoRepository.findKeywordRankings(campaignId);
            List<Document> contentData = contentRepository.findByCampaignId(campaignId);
            
            Map<String, Object> seoStrategy = new HashMap<>();
            seoStrategy.put("keywords", targetKeywords);
            seoStrategy.put("recommendations", generateSEORecommendations(currentRankings, contentData));
            
            seoRepository.saveKeywordRanking(campaignId, "strategy", seoStrategy);
            return seoRepository.getLatestKeywordRanking(campaignId, "strategy");
        }, executorService);
    }

    private Map<String, Object> generateMetadata(Map<String, Object> parameters, List<Document> seoData) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("keywords", parameters.getOrDefault("keywords", new ArrayList<>()));
        metadata.put("seo_score", calculateSEOScore(parameters, seoData));
        metadata.put("generated_at", LocalDateTime.now());
        return metadata;
    }

    private Map<String, Object> aggregateMetrics(List<Document> contentMetrics, List<Document> seoMetrics) {
        Map<String, Object> aggregatedMetrics = new HashMap<>();
        aggregatedMetrics.put("engagement_score", calculateEngagementScore(contentMetrics));
        aggregatedMetrics.put("seo_performance", calculateSEOPerformance(seoMetrics));
        return aggregatedMetrics;
    }

    private List<Map<String, Object>> generateSEORecommendations(List<Document> rankings, List<Document> content) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        // AI-based SEO recommendations logic
        return recommendations;
    }

    private double calculateSEOScore(Map<String, Object> parameters, List<Document> seoData) {
        // AI-based SEO scoring logic
        return 0.0;
    }

    private double calculateEngagementScore(List<Document> metrics) {
        // AI-based engagement scoring logic
        return 0.0;
    }

    private Map<String, Object> calculateSEOPerformance(List<Document> metrics) {
        Map<String, Object> performance = new HashMap<>();
        // AI-based SEO performance calculation logic
        return performance;
    }
    
    public List<String> getAvailableAgentTypes() {
        return agentManager.getAvailableAgentTypes();
    }

    public void shutdown() {
        executorService.shutdown();
    }
}