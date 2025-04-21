package com.aimarketing.services.agents;

import com.aimarketing.models.Campaign;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class AgentManager {
    private Map<String, MarketingAgent> agents;
    
    public AgentManager() {
        agents = new HashMap<>();
        initializeAgents();
    }
    
    private void initializeAgents() {
        // Initialize all marketing agents
        agents.put("social_media", new SocialMediaAgent());
        agents.put("ad_optimizer", new AdOptimizerAgent());
        agents.put("content_writing", new ContentWritingAgent());
        
        // Initialize all agents
        for (MarketingAgent agent : agents.values()) {
            agent.initialize();
        }
    }
    
    public void optimizeCampaign(Campaign campaign, String targetMetric) {
        if (campaign == null) {
            throw new IllegalArgumentException("Campaign cannot be null");
        }
        
        // Run optimization for each agent
        for (MarketingAgent agent : agents.values()) {
            agent.optimize(campaign);
        }
        
        // Update campaign metrics after optimization
        analyzeCampaign(campaign);
    }
    
    public void analyzeCampaign(Campaign campaign) {
        if (campaign == null) {
            throw new IllegalArgumentException("Campaign cannot be null");
        }
        
        // Run analysis for each agent
        for (MarketingAgent agent : agents.values()) {
            agent.analyze(campaign);
        }
    }
    
    public Map<String, Double> predictMetrics(Campaign campaign, List<String> metrics) {
        if (campaign == null) {
            throw new IllegalArgumentException("Campaign cannot be null");
        }
        
        Map<String, Double> predictions = new HashMap<>();
        
        // Get predictions from each agent for requested metrics
        for (String metric : metrics) {
            for (MarketingAgent agent : agents.values()) {
                try {
                    double prediction = agent.predictMetric(campaign, metric);
                    predictions.put(agent.getAgentType() + "_" + metric, prediction);
                } catch (IllegalArgumentException e) {
                    // Skip if agent doesn't support this metric
                    continue;
                }
            }
        }
        
        return predictions;
    }
    
    public void updateAgentModels(Campaign campaign) {
        if (campaign == null) {
            throw new IllegalArgumentException("Campaign cannot be null");
        }
        
        // Update models for each agent
        for (MarketingAgent agent : agents.values()) {
            agent.updateModel(campaign);
        }
    }
    
    public MarketingAgent getAgent(String agentType) {
        return agents.get(agentType);
    }
    
    public List<String> getAvailableAgentTypes() {
        return new ArrayList<>(agents.keySet());
    }
}