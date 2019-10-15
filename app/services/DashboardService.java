package services;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import models.collection.Dashboard;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class DashboardService {
    MongoDatabase database;

    public DashboardService (MongoDatabase database) {
        this.database = database;
    }

    public CompletableFuture<List<Dashboard>> all(Executor context) {
         return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection("Dashboard", Dashboard.class);
            List<Dashboard> items = dashboards.find().into(new ArrayList<>());
            return items;
        }, context);
    }

    public CompletableFuture<Dashboard> findById(String t,Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection("Dashboard", Dashboard.class);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(t));
            return dashboards.find(query).first();
        }, context);
    }

    public CompletableFuture<Dashboard> save(Dashboard which, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection("Dashboard", Dashboard.class);
            which.setId(new ObjectId());
            dashboards.insertOne(which);
            return which;
        }, context);
    }

    public CompletableFuture<Dashboard> update(Dashboard which) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection("Dashboard", Dashboard.class);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(which.getId().toString()));
            dashboards.updateOne(Filters.eq("_id", which.getId()),
                    new BasicDBObject("$set",which)
            );
            return which;
        });
    }

    public CompletableFuture<Dashboard> delete(Dashboard which) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection("Dashboard", Dashboard.class);
            dashboards.deleteOne(new BasicDBObject("_id",  which.getId()));
            return which;
        });
    }
}
