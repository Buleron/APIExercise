package services;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import models.collection.Content;
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
import static utils.Constants.*;

public class ContentService {
    private MongoDatabase database;
    private AccessService accessService = new AccessService();

    public ContentService(MongoDatabase mongoDatabase) {
        this.database = mongoDatabase;
    }

    public CompletableFuture<List<Document>> all(Executor executor, User authUser) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> content = database.getCollection(CONTENT, Document.class);

            if (content.find().into(new ArrayList<>()) == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));

            FindIterable<Document> doc = content.find();
            List<String> access = accessService.GetAccesses(authUser);
            return doc
                    .filter(Filters.or(Filters.in(READ_ACL, access), Filters.size(READ_ACL, 0)))
                    .into(new ArrayList<>());
        }, executor);
    }

    public CompletableFuture<Document> findById(String x, Executor context, User authUser) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> content = database.getCollection(CONTENT, Document.class);

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(x));

            if (content.find(query).first() == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));

            List<String> access = accessService.GetAccesses(authUser);
            Document doc = content.find(Filters.and(Filters.or(Filters.in(READ_ACL, access), Filters.size(READ_ACL, 0)), Filters.in("_id", new ObjectId(x)))).first();

            if (doc == null)
                throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, PERMISSION_DENIED));
            return doc;
        }, context);
    }

    public CompletableFuture<Content> save(Content resContent, Executor context, User authUser) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Content> content = database.getCollection(CONTENT, Content.class);

            Set<String> generalAccesses = new HashSet<>();
            //todo set access for user that create this :/
            generalAccesses.add(authUser.getId().toString());
            //todo or set it as public :/
//            generalAccesses.add("*");
            Set<String> read = new HashSet<>(generalAccesses);
            Set<String> write = new HashSet<>(generalAccesses);

            read.addAll(resContent.getReadACL());
            write.addAll(resContent.getReadACL());

            resContent.setReadACL(read);
            resContent.setWriteACL(write);

            resContent.setId(new ObjectId());
            content.insertOne(resContent);
            return resContent;
        }, context);
    }

    public CompletableFuture<Content> update(Content resContent, Executor context, User authUser) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> content = database.getCollection(CONTENT);

            BasicDBObject query = new BasicDBObject();
            query.put("_id", resContent.getId());

            if (content.find(query).first() == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));

            List<String> access = accessService.GetAccesses(authUser);
            Document cont = content.find(Filters.and(Filters.or(Filters.in(WRITE_ACL, access), Filters.size(WRITE_ACL, 0)), Filters.in("_id", resContent.getId()))).first();

            if (cont == null)
                throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, PERMISSION_DENIED));

            //todo overwrite ReadACL and WriteAcl

            UpdateResult updateResult = content.updateOne(Filters.eq("_id", resContent.getId()), new BasicDBObject("$set", resContent));
            if (updateResult.getModifiedCount() > 0)
                return resContent;
            throw new CompletionException(new RequestException(Http.Status.NOT_MODIFIED, updateResult));
        }, context);
    }

    public CompletableFuture<DeleteResult> delete(String contentID, Executor context, User authUser) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> content = database.getCollection(CONTENT);

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(contentID));

            if (content.find(query).first() == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));

            List<String> access = accessService.GetAccesses(authUser);
            Document cont =content.find(Filters.and(Filters.or(Filters.in(WRITE_ACL, access), Filters.size(WRITE_ACL, 0)), Filters.in("_id", new ObjectId(contentID)))).first();

            if (cont == null)
                throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, PERMISSION_DENIED));

            DeleteResult deleteResult = content.deleteOne(query);
            if (deleteResult.getDeletedCount() > 0)
                return deleteResult;
            throw new CompletionException(new RequestException(Http.Status.NOT_MODIFIED, deleteResult));
        }, context);
    }
}
