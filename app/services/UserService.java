package services;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import models.collection.User;
import models.exceptions.RequestException;
import org.bson.types.ObjectId;
import play.mvc.Http;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public class UserService {
    private MongoDatabase database;
    private static String collectionName = "users";

    public UserService(MongoDatabase mongoDatabase) {
        this.database = mongoDatabase;
    }

    public CompletableFuture<List<User>> all(Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<User> usr = database.getCollection(collectionName, User.class);
            ArrayList<User> dash = usr.find().into(new ArrayList<>());
            if (dash == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, "Nothing founded"));
            return dash;
        }, context);
    }

    public CompletableFuture<User> findById(String t, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<User> usr = database.getCollection(collectionName, User.class);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(t));
            User res = usr.find(query).first();
            if (res == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, "Nothing founded"));
            return res;
        }, context);
    }

    public CompletableFuture<User> save(User which, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<User> usr = database.getCollection(collectionName, User.class);
            which.setId(new ObjectId());
            usr.insertOne(which);
            return which;
        }, context);
    }

    public CompletableFuture<User> update(User which, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<User> users = database.getCollection(collectionName, User.class);
            UpdateResult updateResult = users.updateOne(Filters.eq("id", which.getId()), new BasicDBObject("$set", which));
            if (updateResult.getModifiedCount() > 0)
                return which;
            throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, updateResult));
        }, context);
    }

    public CompletableFuture<DeleteResult> delete(String id, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<User> users = database.getCollection(collectionName, User.class);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));
            DeleteResult deleteResult = users.deleteOne(query);
            if (deleteResult.getDeletedCount() > 0)
                return deleteResult;
            throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, deleteResult));
        }, context);
    }

}
