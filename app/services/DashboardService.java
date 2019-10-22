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

public class DashboardService {
    private MongoDatabase database;
    private AccessService accessService = new AccessService();

    public DashboardService(MongoDatabase database) {
        this.database = database;
    }

    public CompletableFuture<List<Dashboard>> all(Executor context, User user) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection(DASHBOARD, Dashboard.class);

            if (dashboards.find().into(new ArrayList<>()) == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));

            FindIterable<Dashboard> find = dashboards.find();
            List<String> access = accessService.GetAccesses(user);
            // add filters, based on user access
            return find
                    .filter(Filters.or(Filters.in(READ_ACL, access), Filters.size(READ_ACL, 0)))
                    .into(new ArrayList<>());
        }, context);
    }

    public CompletableFuture<Dashboard> findById(String t, Executor context, User user) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection(DASHBOARD, Dashboard.class);

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(t));

            if (dashboards.find(query).first() == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));

            List<String> access = accessService.GetAccesses(user);
            Dashboard dash = dashboards.find(Filters.and(Filters.or(Filters.in(READ_ACL, access), Filters.size(READ_ACL, 0)), Filters.in("_id", new ObjectId(t)))).first();
            if (dash == null)
                throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, PERMISSION_DENIED));
            return dash;
        }, context);
    }

    public CompletableFuture<Dashboard> save(Dashboard which, Executor context, User user) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection(DASHBOARD, Dashboard.class);
            Set<String> generalAccesses = new HashSet<>();
            //todo set access for user that create this :/
            generalAccesses.add(user.getId().toString());
            //todo or set it as public :/
//            generalAccesses.add("*");
            Set<String> read = new HashSet<>(generalAccesses);
            Set<String> write = new HashSet<>(generalAccesses);

            read.addAll(which.getReadACL());
            write.addAll(which.getReadACL());

            which.setReadACL(read);
            which.setWriteACL(write);

            which.setId(new ObjectId());
            dashboards.insertOne(which);
            return which;
        }, context);
    }

    public CompletableFuture<Dashboard> update(Dashboard which, Executor context, User user) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection(DASHBOARD, Dashboard.class);

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(which.getId().toString()));

            Dashboard request = dashboards.find(query).first();
            if (request == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));

            List<String> access = accessService.GetAccesses(user);
            Dashboard dash = dashboards.find(Filters.and(Filters.or(Filters.in(WRITE_ACL, access), Filters.size(WRITE_ACL, 0)), Filters.in("_id", which.getId()))).first();
            if (dash == null)
                throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, PERMISSION_DENIED));

            // overwrite ReadACL & WriteACL;
            which.setReadACL(request.getReadACL());
            which.setWriteACL(request.getWriteACL());

            UpdateResult updateResult = dashboards.updateOne(Filters.eq("_id", which.getId()), new BasicDBObject("$set", which));
            if (updateResult.getModifiedCount() > 0)
                return which;
            throw new CompletionException(new RequestException(Http.Status.NOT_MODIFIED, updateResult));
        }, context);
    }

    public CompletableFuture<DeleteResult> delete(String id, Executor context, User user) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Dashboard> dashboards = database.getCollection(DASHBOARD, Dashboard.class);

            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(id));

            if (dashboards.find(query).first() == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));

            List<String> access = accessService.GetAccesses(user);
            Dashboard dash = dashboards.find(Filters.and(Filters.or(Filters.in(WRITE_ACL, access), Filters.size(WRITE_ACL, 0)), Filters.in("_id", new ObjectId(id)))).first();

            if (dash == null)
                throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, PERMISSION_DENIED));

            DeleteResult deleteResult = dashboards.deleteOne(query);
            if (deleteResult.getDeletedCount() > 0)
                return deleteResult;
            throw new CompletionException(new RequestException(Http.Status.NOT_MODIFIED, deleteResult));
        }, context);
    }
}
