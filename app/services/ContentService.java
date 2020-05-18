package services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.client.model.Filters;
import data.ContentDataAccess;
import data.DataAccess;
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

import java.util.HashSet;
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

    public CompletableFuture<DashboardContent> findByDashboardId(String did, User authUser) {
        RelayCollection<DashboardContent> dashboardContentRelayCollection =
                new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(new MongoRelay(mongoDB.getDatabase(), authUser)
                        .withACL(DashboardContent.class, AccessLevelType.READ)).getRelay().getCollection();

        DashboardContent found = dashboardContentRelayCollection.find(DashboardContent.class).filter(Filters.eq("dashboardId", new ObjectId(did))).first();
        if (found == null)
            throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));
        return CompletableFuture.completedFuture(found);
    }

    public CompletableFuture<DashboardContent> findById(String did, String x, User authUser) {
        try {
            MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(DashboardContent.class, AccessLevelType.READ);
            if (did == null)
                return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).byIdAsync(new ObjectId(x), context.current());
            RelayCollection<DashboardContent> dashboardContentRelayCollection = new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).getRelay().getCollection();
            RelayFindIterable findIterable = dashboardContentRelayCollection.find()
                    .filter(Filters.and(Filters.eq("_id", new ObjectId(x)), Filters.eq("dashboardId", new ObjectId(did))));
            if (findIterable.first() == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));
            return CompletableFuture.completedFuture((DashboardContent) findIterable.first());
        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
            throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, ex.getMessage()));
        }
    }

    public CompletableFuture<DashboardContent> save(DashboardContent resDashboardContent, User authUser) {
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
        return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(new MongoRelay(mongoDB.getDatabase(), authUser)
                .withACL(DashboardContent.class, AccessLevelType.WRITE)).insert(resDashboardContent, context.current());
    }

    public CompletableFuture<DashboardContent> update(DashboardContent resDashboardContent, User authUser) {
        return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(new MongoRelay(mongoDB.getDatabase(), authUser)
                .withACL(DashboardContent.class, AccessLevelType.WRITE)).insert(resDashboardContent, context.current());
    }

    public CompletableFuture<DashboardContent> delete(String contentID, User authUser) {
        return this.findById(null, contentID, authUser).thenCompose((item) -> {
            if (item == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));
            return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(new MongoRelay(mongoDB.getDatabase(), authUser)
                    .withACL(DashboardContent.class, AccessLevelType.WRITE)).deleteAsync(item, context.current());
        });
    }
}
