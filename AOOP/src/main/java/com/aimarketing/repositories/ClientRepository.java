package com.aimarketing.repositories;

import com.aimarketing.models.Client;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

public class ClientRepository {
    private final MongoCollection<Document> collection;

    public ClientRepository(MongoDatabase database) {
        this.collection = database.getCollection("clients");
        // Create indexes for frequently queried fields
        collection.createIndex(Indexes.ascending("email"));
        collection.createIndex(Indexes.ascending("username"));
        collection.createIndex(Indexes.ascending("companyName"));
    }

    public void save(Client client) {
        Document doc = new Document()
                .append("_id", client.getId())
                .append("username", client.getUsername())
                .append("email", client.getEmail())
                .append("hashedPassword", client.getHashedPassword())
                .append("companyName", client.getCompanyName())
                .append("industry", client.getIndustry())
                .append("createdAt", client.getCreatedAt())
                .append("lastLogin", client.getLastLogin())
                .append("preferences", client.getPreferences())
                .append("isActive", client.isActive());

        collection.insertOne(doc);
    }

    public Client findById(ObjectId id) {
        Document doc = collection.find(Filters.eq("_id", id)).first();
        return doc != null ? documentToClient(doc) : null;
    }

    public Client findByEmail(String email) {
        Document doc = collection.find(Filters.eq("email", email)).first();
        return doc != null ? documentToClient(doc) : null;
    }

    public List<Client> findAll() {
        List<Client> clients = new ArrayList<>();
        collection.find().forEach(doc -> clients.add(documentToClient(doc)));
        return clients;
    }

    public List<Client> findByIndustry(String industry) {
        List<Client> clients = new ArrayList<>();
        collection.find(Filters.eq("industry", industry))
                .forEach(doc -> clients.add(documentToClient(doc)));
        return clients;
    }

    public boolean update(Client client) {
        UpdateResult result = collection.updateOne(
                Filters.eq("_id", client.getId()),
                Updates.combine(
                        Updates.set("username", client.getUsername()),
                        Updates.set("email", client.getEmail()),
                        Updates.set("hashedPassword", client.getHashedPassword()),
                        Updates.set("companyName", client.getCompanyName()),
                        Updates.set("industry", client.getIndustry()),
                        Updates.set("lastLogin", client.getLastLogin()),
                        Updates.set("preferences", client.getPreferences()),
                        Updates.set("isActive", client.isActive())
                )
        );
        return result.getModifiedCount() > 0;
    }

    public boolean delete(ObjectId id) {
        DeleteResult result = collection.deleteOne(Filters.eq("_id", id));
        return result.getDeletedCount() > 0;
    }

    private Client documentToClient(Document doc) {
        Client client = new Client();
        client.setUsername(doc.getString("username"));
        client.setEmail(doc.getString("email"));
        client.setHashedPassword(doc.getString("hashedPassword"));
        client.setCompanyName(doc.getString("companyName"));
        client.setIndustry(doc.getString("industry"));
        client.setLastLogin(doc.get("lastLogin", java.time.LocalDateTime.class));
        client.setActive(doc.getBoolean("isActive", true));
        
        @SuppressWarnings("unchecked")
        Map<String, Object> preferences = (Map<String, Object>) doc.get("preferences");
        if (preferences != null) {
            preferences.forEach(client::setPreference);
        }
        
        return client;
    }
}