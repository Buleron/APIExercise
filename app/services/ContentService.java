package services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import data.ContentDataAccess;
import data.DashboardsDataAccess;
import data.DataAccess;
import models.collection.Dashboard;
import models.collection.User;
import models.collection.content.DashboardContent;
import models.enums.AccessLevelType;
import models.exceptions.RequestException;
import modules.SingleThreadedExecutionContext;
import mongo.MongoDB;
import mongolay.MongoRelay;
import mongolay.RelayCollection;
import mongolay.RelayFindIterable;
import org.bson.types.ObjectId;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static utils.Constants.NOT_FOUND;

@Singleton
public class ContentService {
    @Inject
    private MongoDB mongoDB;
    @Inject
    HttpExecutionContext context;
    @Inject
    SingleThreadedExecutionContext singleThreadedExecutionContext;

    public CompletableFuture<List<DashboardContent>> all(String did, User authUser) {
        MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(DashboardContent.class, AccessLevelType.READ);
        RelayCollection<DashboardContent> dashboardContentRelayCollection = new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).getRelay().getCollection();
        RelayFindIterable findIterable = dashboardContentRelayCollection.find().filter(Filters.eq("dashboardId", new ObjectId(did)));
        return CompletableFuture.completedFuture((List<DashboardContent>) findIterable.into(new ArrayList()));
    }

    public CompletableFuture<DashboardContent> findById(String did, String x, User authUser) {
        try {
            MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(DashboardContent.class, AccessLevelType.READ);
            if (did == null) {
                DataAccess<DashboardContent> access = new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay);
                return access.byIdAsync(new ObjectId(x), context.current());
            }
            RelayCollection<DashboardContent> dashboardContentRelayCollection = new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).getRelay().getCollection();
            RelayFindIterable findIterable = dashboardContentRelayCollection.find()
                    .filter(Filters.and(Filters.eq("_id", new ObjectId(x)), Filters.eq("dashboardId", new ObjectId(did))));
            if (findIterable == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));
            return CompletableFuture.completedFuture((DashboardContent) findIterable.first());
        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
            throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, ex.getMessage()));
        }
    }

    public CompletableFuture<DashboardContent> save(DashboardContent resDashboardContent, User authUser) {
        MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(DashboardContent.class, AccessLevelType.WRITE);

        Set<String> generalAccesses = new HashSet<>();
        //todo set access for user that create this :/
        generalAccesses.add(authUser.getId().toString());
        //todo or set it as public :/
//            generalAccesses.add("*");
        Set<String> read = new HashSet<>(generalAccesses);
        Set<String> write = new HashSet<>(generalAccesses);

        read.addAll(resDashboardContent.getReadACL());
        write.addAll(resDashboardContent.getReadACL());

        resDashboardContent.setReadACL(read);
        resDashboardContent.setWriteACL(write);
        return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).insert(resDashboardContent, context.current());
    }

    public CompletableFuture<DashboardContent> update(DashboardContent resDashboardContent, User authUser) {
        MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(DashboardContent.class, AccessLevelType.WRITE);
        return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).insert(resDashboardContent, context.current());
    }

    public CompletableFuture<DashboardContent> delete(String contentID, User authUser) {
        return this.findById(null, contentID, authUser).thenCompose((item) -> {
            if (item == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));
            MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(DashboardContent.class, AccessLevelType.WRITE);
            return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).deleteAsync(item, context.current());
        });
    }
}
