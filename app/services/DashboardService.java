package services;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import models.collection.Dashboard;
import models.collection.User;
import models.exceptions.RequestException;
import org.bson.Document;
import org.bson.types.ObjectId;
import play.mvc.Http;
import scalaoauth2.provider.AuthInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static play.mvc.Controller.ctx;
import static utils.Constants.READ_ACL;

public class DashboardService {
    private MongoDatabase database;
    private static String collectionName = "Dashboard";
    public DashboardService(MongoDatabase database) {
        this.database = database;
    }

    public DashboardService withReadAccess() {
        AuthInfo<User> authInfo = (AuthInfo<User>) ctx().args.get("authInfo");
        return withReadAccess(authInfo.user());
    }

    public DashboardService withReadAccess(User user) {
//        setReadAccess(user);
        return this;
    }

    public DashboardService withWriteAccess() {
        AuthInfo<User> authInfo = (AuthInfo<User>) ctx().args.get("authInfo");
        return withWriteAccess(authInfo.user());
    }

    public DashboardService withWriteAccess(User user) {
//        setWriteAccess(user);
        return this;
    }


    public CompletableFuture<List<Dashboard>> all(Executor context, User user) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection(collectionName, Dashboard.class);
            FindIterable<Dashboard> find = dashboards.find();

            List<String> access = new ArrayList<>();
            List<String> roles = user.getRoles().stream().map(next -> next.getId()).collect(Collectors.toList());
            access.addAll(roles);
            access.add(user.getId().toString());
            access.add("*");
            // add filters, based on user access
            ArrayList<Dashboard> dash = find
                    .filter(Filters.or(Filters.in(READ_ACL, access), Filters.size(READ_ACL, 0)))
                    .into(new ArrayList<>());

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
