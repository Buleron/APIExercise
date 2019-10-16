package services;

import Interfaces.IContent;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import models.collection.Content;
import models.exceptions.RequestException;
import org.bson.Document;
import org.bson.types.ObjectId;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public class ContentService {
    private MongoDatabase database;
    private static String collectionName = "Content";
    public ContentService(MongoDatabase mongoDatabase) {
        this.database = mongoDatabase;
    }

    public CompletableFuture<List<Document>> all(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> content = database.getCollection(collectionName);
           ArrayList<Document> doc  = content.find().into(new ArrayList<>());
           if(doc.isEmpty())
               throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, "Nothing founded"));
           return doc;
        }, executor);
    }

    public CompletableFuture<Document> findById(String x, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> content = database.getCollection(collectionName);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(x));
            Document doc = content.find(query).first();
            if(doc.isEmpty())
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, "Nothing founded"));
            return doc;
        }, context);
    }

    public CompletableFuture<Content> save(Content resContent, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Content> content = database.getCollection(collectionName, Content.class);
                resContent.setId(new ObjectId());
                content.insertOne(resContent);
            return resContent;
        }, context);
    }

    public CompletableFuture<Content> update(Content resContent, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Content> content = database.getCollection(collectionName, Content.class);
            UpdateResult updateResult = content.updateOne(Filters.eq("_id", resContent.getId()), new BasicDBObject("$set", resContent));
            if (updateResult.isModifiedCountAvailable())
                return resContent;
            throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, updateResult));
        }, context);
    }

    public CompletableFuture<DeleteResult> delete(String contentID, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Content> content = database.getCollection(collectionName, Content.class);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(contentID));
            DeleteResult deleteResult = content.deleteOne(query);
            if (deleteResult.wasAcknowledged())
                return deleteResult;
            throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, deleteResult));
        }, context);
    }
}
