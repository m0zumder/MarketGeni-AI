package com.aimarketing.services.agents;

import com.aimarketing.models.Campaign;

public interface MarketingAgent {
    void initialize();
    void optimize(Campaign campaign);
    void analyze(Campaign campaign);
    void updateModel(Campaign campaign);
    double predictMetric(Campaign campaign, String metric);
    String getAgentType();
    
    default void validateCampaign(Campaign campaign) {
        if (campaign == null) {
            throw new IllegalArgumentException("Campaign cannot be null");
        }
    }
}