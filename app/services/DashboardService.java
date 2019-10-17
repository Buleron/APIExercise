package services;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import models.collection.Dashboard;
import models.exceptions.RequestException;
import org.bson.Document;
import org.bson.types.ObjectId;
import play.mvc.Http;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public class DashboardService {
    private MongoDatabase database;
    private static String collectionName = "Dashboard";
    public DashboardService(MongoDatabase database) {
        this.database = database;
    }

    public CompletableFuture<List<Dashboard>> all(Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection(collectionName, Dashboard.class);
            ArrayList<Dashboard> dash = dashboards.find().into(new ArrayList<>());
            if (dash == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, "Nothing founded"));
            return dash;
        }, context);
    }

    public CompletableFuture<Dashboard> findById(String t, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection(collectionName, Dashboard.class);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(t));
            Dashboard dash = dashboards.find(query).first();
            if (dash == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, "Nothing founded"));
            return dash;
        }, context);
    }

    public CompletableFuture<Dashboard> save(Dashboard which, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection(collectionName, Dashboard.class);
            which.setId(new ObjectId());
            dashboards.insertOne(which);
            return which;
        }, context);
    }

    public CompletableFuture<Dashboard> update(Dashboard which, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection(collectionName, Dashboard.class);
            UpdateResult updateResult = dashboards.updateOne(Filters.eq("_id", which.getId()), new BasicDBObject("$set", which));
            if (updateResult.getModifiedCount() > 0)
                return which;
            throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, updateResult));
        }, context);
    }

    public CompletableFuture<DeleteResult> delete(String id, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection(collectionName, Dashboard.class);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            DeleteResult deleteResult = dashboards.deleteOne(query);
            if (deleteResult.getDeletedCount() > 0)
                return deleteResult;
            throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, deleteResult));
        }, context);
    }
}
