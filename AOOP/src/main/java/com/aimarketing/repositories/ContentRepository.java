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

public class ContentRepository {
    private final MongoCollection<Document> collection;

    public ContentRepository(MongoDatabase database) {
        this.collection = database.getCollection("content_data");
        // Create indexes for frequently queried fields
        collection.createIndex(Indexes.ascending("campaignId"));
        collection.createIndex(Indexes.ascending("contentType"));
        collection.createIndex(Indexes.ascending("createdAt"));
        collection.createIndex(Indexes.text("title"));
        collection.createIndex(Indexes.text("content"));
        collection.createIndex(Indexes.compoundIndex(
            Indexes.ascending("campaignId"),
            Indexes.ascending("contentType"),
            Indexes.ascending("createdAt")
        ));
    }

    public void saveContent(String campaignId, String contentType, Map<String, Object> contentData) {
        Document doc = new Document()
                .append("_id", new ObjectId())
                .append("campaignId", campaignId)
                .append("contentType", contentType)
                .append("title", contentData.get("title"))
                .append("content", contentData.get("content"))
                .append("metadata", contentData.get("metadata"))
                .append("version", 1)
                .append("createdAt", LocalDateTime.now())
                .append("updatedAt", LocalDateTime.now());

        collection.insertOne(doc);
    }

    public List<Document> findByCampaignId(String campaignId) {
        List<Document> contents = new ArrayList<>();
        collection.find(Filters.eq("campaignId", campaignId))
                .forEach(contents::add);
        return contents;
    }

    public List<Document> findByContentType(String contentType) {
        List<Document> contents = new ArrayList<>();
        collection.find(Filters.eq("contentType", contentType))
                .forEach(contents::add);
        return contents;
    }

    public List<Document> findByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Document> contents = new ArrayList<>();
        collection.find(Filters.and(
                Filters.gte("createdAt", startDate),
                Filters.lte("createdAt", endDate)))
                .forEach(contents::add);
        return contents;
    }

    public List<Document> searchContent(String searchText) {
        List<Document> contents = new ArrayList<>();
        collection.find(Filters.text(searchText))
                .forEach(contents::add);
        return contents;
    }

    public boolean updateContent(ObjectId id, Map<String, Object> newData) {
        Document currentDoc = collection.find(Filters.eq("_id", id)).first();
        if (currentDoc == null) return false;

        int currentVersion = currentDoc.getInteger("version");
        
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", id),
                Updates.combine(
                    Updates.set("title", newData.get("title")),
                    Updates.set("content", newData.get("content")),
                    Updates.set("metadata", newData.get("metadata")),
                    Updates.set("version", currentVersion + 1),
                    Updates.set("updatedAt", LocalDateTime.now())
                )
        );
        return result.getModifiedCount() > 0;
    }

    public boolean deleteContent(ObjectId id) {
        DeleteResult result = collection.deleteOne(Filters.eq("_id", id));
        return result.getDeletedCount() > 0;
    }

    public void deleteContentByCampaign(String campaignId) {
        collection.deleteMany(Filters.eq("campaignId", campaignId));
    }

    public Document getLatestContent(String campaignId, String contentType) {
        return collection.find(Filters.and(
                Filters.eq("campaignId", campaignId),
                Filters.eq("contentType", contentType)))
                .sort(Indexes.descending("createdAt"))
                .first();
    }

    public List<Document> getContentVersions(ObjectId id) {
        List<Document> versions = new ArrayList<>();
        collection.find(Filters.eq("_id", id))
                .sort(Indexes.ascending("version"))
                .forEach(versions::add);
        return versions;
    }
}