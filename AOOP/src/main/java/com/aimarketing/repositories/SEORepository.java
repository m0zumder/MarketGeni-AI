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

public class SEORepository {
    private final MongoCollection<Document> collection;

    public SEORepository(MongoDatabase database) {
        this.collection = database.getCollection("seo_data");
        // Create indexes for frequently queried fields
        collection.createIndex(Indexes.ascending("campaignId"));
        collection.createIndex(Indexes.ascending("keyword"));
        collection.createIndex(Indexes.ascending("domain"));
        collection.createIndex(Indexes.ascending("timestamp"));
        collection.createIndex(Indexes.compoundIndex(
            Indexes.ascending("campaignId"),
            Indexes.ascending("keyword"),
            Indexes.ascending("timestamp")
        ));
    }

    public void saveKeywordRanking(String campaignId, String keyword, Map<String, Object> rankingData) {
        Document doc = new Document()
                .append("_id", new ObjectId())
                .append("campaignId", campaignId)
                .append("type", "keyword_ranking")
                .append("keyword", keyword)
                .append("timestamp", LocalDateTime.now())
                .append("data", rankingData);

        collection.insertOne(doc);
    }

    public void saveBacklink(String campaignId, String domain, Map<String, Object> backlinkData) {
        Document doc = new Document()
                .append("_id", new ObjectId())
                .append("campaignId", campaignId)
                .append("type", "backlink")
                .append("domain", domain)
                .append("timestamp", LocalDateTime.now())
                .append("data", backlinkData);

        collection.insertOne(doc);
    }

    public List<Document> findKeywordRankings(String campaignId) {
        List<Document> rankings = new ArrayList<>();
        collection.find(Filters.and(
                Filters.eq("campaignId", campaignId),
                Filters.eq("type", "keyword_ranking")))
                .forEach(rankings::add);
        return rankings;
    }

    public List<Document> findBacklinks(String campaignId) {
        List<Document> backlinks = new ArrayList<>();
        collection.find(Filters.and(
                Filters.eq("campaignId", campaignId),
                Filters.eq("type", "backlink")))
                .forEach(backlinks::add);
        return backlinks;
    }

    public List<Document> findByKeyword(String keyword) {
        List<Document> rankings = new ArrayList<>();
        collection.find(Filters.and(
                Filters.eq("keyword", keyword),
                Filters.eq("type", "keyword_ranking")))
                .forEach(rankings::add);
        return rankings;
    }

    public List<Document> findByDomain(String domain) {
        List<Document> backlinks = new ArrayList<>();
        collection.find(Filters.and(
                Filters.eq("domain", domain),
                Filters.eq("type", "backlink")))
                .forEach(backlinks::add);
        return backlinks;
    }

    public List<Document> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Document> seoData = new ArrayList<>();
        collection.find(Filters.and(
                Filters.gte("timestamp", startDate),
                Filters.lte("timestamp", endDate)))
                .forEach(seoData::add);
        return seoData;
    }

    public boolean updateKeywordRanking(ObjectId id, Map<String, Object> newData) {
        UpdateResult result = collection.updateOne(
                Filters.and(
                    Filters.eq("_id", id),
                    Filters.eq("type", "keyword_ranking")),
                Updates.set("data", newData)
        );
        return result.getModifiedCount() > 0;
    }

    public boolean updateBacklink(ObjectId id, Map<String, Object> newData) {
        UpdateResult result = collection.updateOne(
                Filters.and(
                    Filters.eq("_id", id),
                    Filters.eq("type", "backlink")),
                Updates.set("data", newData)
        );
        return result.getModifiedCount() > 0;
    }

    public boolean deleteKeywordRanking(ObjectId id) {
        DeleteResult result = collection.deleteOne(Filters.and(
                Filters.eq("_id", id),
                Filters.eq("type", "keyword_ranking")));
        return result.getDeletedCount() > 0;
    }

    public boolean deleteBacklink(ObjectId id) {
        DeleteResult result = collection.deleteOne(Filters.and(
                Filters.eq("_id", id),
                Filters.eq("type", "backlink")));
        return result.getDeletedCount() > 0;
    }

    public void deleteSEODataByCampaign(String campaignId) {
        collection.deleteMany(Filters.eq("campaignId", campaignId));
    }

    public Document getLatestKeywordRanking(String campaignId, String keyword) {
        return collection.find(Filters.and(
                Filters.eq("campaignId", campaignId),
                Filters.eq("keyword", keyword),
                Filters.eq("type", "keyword_ranking")))
                .sort(Indexes.descending("timestamp"))
                .first();
    }

    public Document getLatestBacklink(String campaignId, String domain) {
        return collection.find(Filters.and(
                Filters.eq("campaignId", campaignId),
                Filters.eq("domain", domain),
                Filters.eq("type", "backlink")))
                .sort(Indexes.descending("timestamp"))
                .first();
    }
}