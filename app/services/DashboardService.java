package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import models.collection.Dashboard;
import models.exceptions.RequestException;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
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
            if (items.size() > 0) {
                throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, "Wrong, this is bad."));
            }
            return items;
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
            which.setId(new ObjectId());
            dashboards.updateOne(Filters.eq("id",which.getId()),(Bson) dashboards);
            return which;
        });
    }

    public CompletableFuture<Dashboard> delete(Dashboard which) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection("Dashboard", Dashboard.class);
            which.setId(new ObjectId());
            dashboards.deleteOne(Filters.eq("id",which.getId()));
            return which;
        });
    }
}
