package services;

import Interfaces.IContent;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import models.collection.Content;
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

    public CompletableFuture<List<Content>> all(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Content> content = database.getCollection("Content",Content.class);
            content.find().into(new ArrayList<>());
            return null;
        },executor);
    }

    public CompletableFuture<Content> findById(String x,Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Content> content = database.getCollection("Content",Content.class);
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

    public CompletableFuture<Content> delete(Content resContent,Executor context) {
        return CompletableFuture.supplyAsync(()-> {
            MongoCollection<Content> content = database.getCollection("Content",Content.class);
            DeleteResult deleteResult = content.deleteOne(new BasicDBObject("_id",resContent.getId()));
            System.out.println(deleteResult);
            return resContent;
        },context);
    }
}
