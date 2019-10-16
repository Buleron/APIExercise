package services;

import Interfaces.IContent;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import models.collection.Content;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ContentService {
    private MongoDatabase database;

    public ContentService(MongoDatabase mongoDatabase){
        this.database = mongoDatabase;
    }

    public CompletableFuture<List<Document>> all(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> content = database.getCollection("Content");
            return content.find().into(new ArrayList<>());

        },executor);
    }

    public CompletableFuture<Document> findById(String x,Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> content = database.getCollection("Content");
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(x));
            return content.find(query).first();
        },context);
    }

    public CompletableFuture<Content>  save(Content resContent, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Content> content = database.getCollection("Content",Content.class);
            resContent.setId(new ObjectId());
            content.insertOne(resContent);
            return resContent;
        },context);
    }
    public CompletableFuture<Content> update(Content resContent,Executor context) {
        return CompletableFuture.supplyAsync(()-> {
            MongoCollection<Content> content = database.getCollection("Content",Content.class);
            UpdateResult updateResult = content.updateOne(Filters.eq("_id",resContent.getId()),new BasicDBObject("$set",resContent));
            System.out.println(updateResult);
            return resContent;
        },context);
    }

    public CompletableFuture<DeleteResult> delete(String contentID,Executor context) {
        return CompletableFuture.supplyAsync(()-> {
            MongoCollection<Content> content = database.getCollection("Content",Content.class);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(contentID));
            DeleteResult deleteResult = content.deleteOne(query);
            System.out.println(deleteResult);
            return  deleteResult;
        },context);
    }
}
