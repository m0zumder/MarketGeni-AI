package com.aimarketing.repositories;

import com.aimarketing.models.Campaign;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CampaignRepository {
    private final MongoCollection<Document> collection;

    public CampaignRepository(MongoDatabase database) {
        this.collection = database.getCollection("campaigns");
        // Create indexes for frequently queried fields
        collection.createIndex(Indexes.ascending("name"));
        collection.createIndex(Indexes.ascending("status"));
        collection.createIndex(Indexes.ascending("startDate"));
        collection.createIndex(Indexes.descending("budget"));
    }

    public void save(Campaign campaign) {
        Document doc = new Document()
                .append("_id", new ObjectId(campaign.getId()))
                .append("name", campaign.getName())
                .append("description", campaign.getDescription())
                .append("startDate", campaign.getStartDate())
                .append("endDate", campaign.getEndDate())
                .append("budget", campaign.getBudget())
                .append("status", campaign.getStatus())
                .append("metrics", campaign.getMetrics())
                .append("aiSettings", campaign.getAiSettings())
                .append("targetAudience", campaign.getTargetAudience())
                .append("audienceSettings", campaign.getAudienceSettings())
                .append("aiOptimizationEnabled", campaign.isAiOptimizationEnabled())
                .append("abTestSettings", campaign.getAbTestSettings());

        collection.insertOne(doc);
    }

    public Campaign findById(String id) {
        Document doc = collection.find(Filters.eq("_id", new ObjectId(id))).first();
        return doc != null ? documentToCampaign(doc) : null;
    }

    public List<Campaign> findByStatus(String status) {
        List<Campaign> campaigns = new ArrayList<>();
        collection.find(Filters.eq("status", status))
                .forEach(doc -> campaigns.add(documentToCampaign(doc)));
        return campaigns;
    }

    public List<Campaign> findActiveCampaigns() {
        List<Campaign> campaigns = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        collection.find(Filters.and(
                Filters.eq("status", "ACTIVE"),
                Filters.lte("startDate", now),
                Filters.or(Filters.gte("endDate", now), Filters.exists("endDate", false))))
                .forEach(doc -> campaigns.add(documentToCampaign(doc)));
        return campaigns;
    }

    public List<Campaign> findByBudgetRange(double minBudget, double maxBudget) {
        List<Campaign> campaigns = new ArrayList<>();
        collection.find(Filters.and(
                Filters.gte("budget", minBudget),
                Filters.lte("budget", maxBudget)))
                .forEach(doc -> campaigns.add(documentToCampaign(doc)));
        return campaigns;
    }

    public boolean update(Campaign campaign) {
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", new ObjectId(campaign.getId())),
                Updates.combine(
                        Updates.set("name", campaign.getName()),
                        Updates.set("description", campaign.getDescription()),
                        Updates.set("startDate", campaign.getStartDate()),
                        Updates.set("endDate", campaign.getEndDate()),
                        Updates.set("budget", campaign.getBudget()),
                        Updates.set("status", campaign.getStatus()),
                        Updates.set("metrics", campaign.getMetrics()),
                        Updates.set("aiSettings", campaign.getAiSettings()),
                        Updates.set("targetAudience", campaign.getTargetAudience()),
                        Updates.set("audienceSettings", campaign.getAudienceSettings()),
                        Updates.set("aiOptimizationEnabled", campaign.isAiOptimizationEnabled()),
                        Updates.set("abTestSettings", campaign.getAbTestSettings())
                )
        );
        return result.getModifiedCount() > 0;
    }

    public boolean delete(String id) {
        DeleteResult result = collection.deleteOne(Filters.eq("_id", new ObjectId(id)));
        return result.getDeletedCount() > 0;
    }

    private Campaign documentToCampaign(Document doc) {
        Campaign campaign = new Campaign();
        campaign.setName(doc.getString("name"));
        campaign.setDescription(doc.getString("description"));
        campaign.setStartDate(doc.get("startDate", LocalDateTime.class));
        campaign.setEndDate(doc.get("endDate", LocalDateTime.class));
        campaign.setBudget(doc.getDouble("budget"));
        campaign.setStatus(doc.getString("status"));
        
        @SuppressWarnings("unchecked")
        Map<String, Double> metrics = (Map<String, Double>) doc.get("metrics");
        if (metrics != null) {
            metrics.forEach(campaign::setMetric);
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> aiSettings = (Map<String, Object>) doc.get("aiSettings");
        if (aiSettings != null) {
            aiSettings.forEach(campaign::setAiSetting);
        }

        campaign.setTargetAudience(doc.getString("targetAudience"));

        @SuppressWarnings("unchecked")
        Map<String, Object> audienceSettings = (Map<String, Object>) doc.get("audienceSettings");
        if (audienceSettings != null) {
            audienceSettings.forEach(campaign::setAudienceSetting);
        }

        campaign.setAiOptimizationEnabled(doc.getBoolean("aiOptimizationEnabled", false));

        @SuppressWarnings("unchecked")
        Map<String, Object> abTestSettings = (Map<String, Object>) doc.get("abTestSettings");
        if (abTestSettings != null) {
            abTestSettings.forEach(campaign::setAbTestSetting);
        }

        return campaign;
    }
}