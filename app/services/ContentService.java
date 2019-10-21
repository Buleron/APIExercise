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
import models.collection.Dashboard;
import models.collection.User;
import models.exceptions.RequestException;
import org.bson.Document;
import org.bson.types.ObjectId;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static utils.Constants.READ_ACL;
import static utils.Constants.WRITE_ACL;

public class ContentService {
    private MongoDatabase database;
    private static String collectionName = "Content";

    public ContentService(MongoDatabase mongoDatabase) {
        this.database = mongoDatabase;
    }

    public CompletableFuture<List<Content>> all(Executor executor, User authUser) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Content> content = database.getCollection(collectionName,Content.class);
            FindIterable<Content> doc = content.find();

            List<String> access = new ArrayList<>();
            List<String> roles = authUser.getRoles().stream().map( next -> next.getId()).collect(Collectors.toList());
            System.out.println(roles);
            access.addAll(roles);
            access.add(authUser.getId().toString());
            access.add("*");

            ArrayList<Content> res  = doc
                    .filter(Filters.or(Filters.in(READ_ACL,access),Filters.size(READ_ACL,0)))
                    .into(new ArrayList<>());

            if (res == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, "Nothing founded"));
            return res;
        }, executor);
    }

    public CompletableFuture<Content> findById(String x, Executor context, User authUser) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Content> content = database.getCollection(collectionName,Content.class);

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(x));

            List<String> access = new ArrayList<>();
            List<String> roles = authUser.getRoles().stream().map(next -> next.getId()).collect(Collectors.toList());
            System.out.println(roles);
            access.addAll(roles);
            access.add(authUser.getId().toString());
            access.add("*");

            Content doc = content.find(query).filter(Filters.or(Filters.in(READ_ACL,access),Filters.size(READ_ACL,0))).first();

            if (doc == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, "Nothing founded"));
            return doc;
        }, context);
    }

    public CompletableFuture<Content> save(Content resContent, Executor context, User AuthUser) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Content> content = database.getCollection(collectionName, Content.class);

            Set<String> access = new HashSet<>();
            //todo add accesses for its own creator;
            access.add(AuthUser.getId().toString());
            //todo or set it as public :/
            access.add("*");

            resContent.setReadACL(access);
            resContent.setWriteACL(access);

            resContent.setId(new ObjectId());
            content.insertOne(resContent);
            return resContent;
        }, context);
    }

    public CompletableFuture<Content> update(Content resContent, Executor context, User AuthUser) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Content> content = database.getCollection(collectionName, Content.class);

            List<String> access = new ArrayList<>();
            List<String> roles = AuthUser.getRoles().stream().map(next -> next.getId()).collect(Collectors.toList());
            System.out.println(roles);
            access.addAll(roles);
            access.add(AuthUser.getId().toString());
            access.add("*");

            BasicDBObject query = new BasicDBObject();
            query.put("_id", resContent.getId());

            Content cont = content.find(query).filter(Filters.or(Filters.in(WRITE_ACL, access), Filters.size(WRITE_ACL, 0))).first();
            if(cont == null){
                throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED,"Permission denied"));
            }

            UpdateResult updateResult = content.updateOne(Filters.eq(query), new BasicDBObject("$set", resContent));
            if (updateResult.getModifiedCount() > 0)
                return resContent;
            throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, updateResult));
        }, context);
    }

    public CompletableFuture<DeleteResult> delete(String contentID, Executor context, User AuthUser) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Content> content = database.getCollection(collectionName, Content.class);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(contentID));

            List<String> access = new ArrayList<>();
            List<String> roles = AuthUser.getRoles().stream().map(next -> next.getId()).collect(Collectors.toList());
            System.out.println(roles);
            access.addAll(roles);
            access.add(AuthUser.getId().toString());
            access.add("*");

            Content cont = content.find(query).filter(Filters.or(Filters.in(WRITE_ACL, access), Filters.size(WRITE_ACL, 0))).first();
            if(cont == null){
                throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED,"Permission denied"));
            }

            DeleteResult deleteResult = content.deleteOne(query);
            if (deleteResult.getDeletedCount() > 0)
                return deleteResult;
            throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, deleteResult));
        }, context);
    }
}
