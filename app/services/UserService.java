package services;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import models.collection.Dashboard;
import models.collection.User;
import models.exceptions.RequestException;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public class UserService {
    private MongoDatabase database;

    public UserService(MongoDatabase mongoDatabase) {
        this.database = mongoDatabase;
    }

    public CompletableFuture<List<User>> all(Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<User> usr = database.getCollection("users", User.class);
            ArrayList<User> dash = usr.find().into(new ArrayList<>());
            if (dash == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, "Nothing founded"));
            return dash;
        }, context);
    }

}
