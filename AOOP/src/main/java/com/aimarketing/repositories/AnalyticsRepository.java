package com.aimarketing.repositories;

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

public class AnalyticsRepository {
    private final MongoCollection<Document> collection;

    public AnalyticsRepository(MongoDatabase database) {
        this.collection = database.getCollection("analytics");
        // Create indexes for frequently queried fields
        collection.createIndex(Indexes.ascending("campaignId"));
        collection.createIndex(Indexes.ascending("metricType"));
        collection.createIndex(Indexes.ascending("timestamp"));
        collection.createIndex(Indexes.compoundIndex(
            Indexes.ascending("campaignId"),
            Indexes.ascending("metricType"),
            Indexes.ascending("timestamp")
        ));
    }

    public void saveMetric(String campaignId, String metricType, Map<String, Object> data) {
        Document doc = new Document()
                .append("_id", new ObjectId())
                .append("campaignId", campaignId)
                .append("metricType", metricType)
                .append("timestamp", LocalDateTime.now())
                .append("data", data);

        collection.insertOne(doc);
    }

    public List<Document> findByCampaignId(String campaignId) {
        List<Document> metrics = new ArrayList<>();
        collection.find(Filters.eq("campaignId", campaignId))
                .forEach(metrics::add);
        return metrics;
    }

    public List<Document> findByMetricType(String metricType) {
        List<Document> metrics = new ArrayList<>();
        collection.find(Filters.eq("metricType", metricType))
                .forEach(metrics::add);
        return metrics;
    }

    public List<Document> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Document> metrics = new ArrayList<>();
        collection.find(Filters.and(
                Filters.gte("timestamp", startDate),
                Filters.lte("timestamp", endDate)))
                .forEach(metrics::add);
        return metrics;
    }

    public List<Document> findByCampaignAndMetricType(String campaignId, String metricType) {
        List<Document> metrics = new ArrayList<>();
        collection.find(Filters.and(
                Filters.eq("campaignId", campaignId),
                Filters.eq("metricType", metricType)))
                .forEach(metrics::add);
        return metrics;
    }

    public boolean updateMetric(ObjectId id, Map<String, Object> newData) {
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.set("data", newData)
        );
        return result.getModifiedCount() > 0;
    }

    public boolean deleteMetric(ObjectId id) {
        DeleteResult result = collection.deleteOne(Filters.eq("_id", id));
        return result.getDeletedCount() > 0;
    }

    public void deleteMetricsByCampaign(String campaignId) {
        collection.deleteMany(Filters.eq("campaignId", campaignId));
    }

    public long getMetricsCount(String campaignId) {
        return collection.countDocuments(Filters.eq("campaignId", campaignId));
    }

    public Document getLatestMetric(String campaignId, String metricType) {
        return collection.find(Filters.and(
                Filters.eq("campaignId", campaignId),
                Filters.eq("metricType", metricType)))
                .sort(Indexes.descending("timestamp"))
                .first();
    }
}