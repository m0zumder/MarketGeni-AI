package com.aimarketing.services;

import com.aimarketing.models.Campaign;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import java.util.ArrayList;
import java.util.List;

public class CampaignService {
    private final MongoClient mongoClient;
    private final MongoDatabase database;
    private final MongoCollection<Document> campaignCollection;

    public CampaignService() {
        // Initialize MongoDB connection
        mongoClient = MongoClients.create("mongodb://localhost:27017");
        database = mongoClient.getDatabase("aimarketing");
        campaignCollection = database.getCollection("campaigns");
    }

    public void saveCampaign(Campaign campaign) {
        Document doc = new Document()
            .append("_id", campaign.getId())
            .append("name", campaign.getName())
            .append("description", campaign.getDescription())
            .append("budget", campaign.getBudget())
            .append("status", campaign.getStatus())
            .append("metrics", campaign.getMetrics())
            .append("aiSettings", campaign.getAiSettings());

        campaignCollection.insertOne(doc);
    }

    public void updateCampaign(Campaign campaign) {
        Document update = new Document()
            .append("name", campaign.getName())
            .append("description", campaign.getDescription())
            .append("budget", campaign.getBudget())
            .append("status", campaign.getStatus())
            .append("metrics", campaign.getMetrics())
            .append("aiSettings", campaign.getAiSettings());

        campaignCollection.updateOne(
            Filters.eq("_id", campaign.getId()),
            Updates.combine(Updates.set("$set", update))
        );
    }

    public Campaign getCampaign(String id) {
        Document doc = campaignCollection.find(Filters.eq("_id", id)).first();
        return documentToCampaign(doc);
    }

    public List<Campaign> getAllCampaigns() {
        List<Campaign> campaigns = new ArrayList<>();
        campaignCollection.find().forEach(doc -> campaigns.add(documentToCampaign(doc)));
        return campaigns;
    }

    public void deleteCampaign(String id) {
        campaignCollection.deleteOne(Filters.eq("_id", id));
    }

    private Campaign documentToCampaign(Document doc) {
        if (doc == null) return null;

        Campaign campaign = new Campaign();
        campaign.setName(doc.getString("name"));
        campaign.setDescription(doc.getString("description"));
        campaign.setBudget(doc.getDouble("budget"));
        campaign.setStatus(doc.getString("status"));

        // Convert Document to Map for metrics and aiSettings
        Document metricsDoc = (Document) doc.get("metrics");
        if (metricsDoc != null) {
            metricsDoc.forEach((key, value) -> campaign.setMetric(key, (Double) value));
        }

        Document aiSettingsDoc = (Document) doc.get("aiSettings");
        if (aiSettingsDoc != null) {
            aiSettingsDoc.forEach((key, value) -> campaign.setAiSetting(key, value));
        }

        return campaign;
    }

    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}