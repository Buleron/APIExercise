package services;

import data.DashboardsDataAccess;
import data.DataAccess;
import models.collection.Dashboard;
import models.collection.User;
import models.enums.AccessLevelType;
import models.exceptions.RequestException;
import mongo.MongoDB;
import mongolay.MongoRelay;
import oauth2.Authenticated;
import oauth2.PlatformAttributes;
import org.bson.types.ObjectId;
import play.filters.csrf.CSRF;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import utils.DatabaseUtils;
import utils.ServiceUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

import static utils.Constants.*;


@Authenticated()
public class PassArgActionService extends play.mvc.Action.Simple {
    @Inject
    private MongoDB mongoDB;
    @Inject
    HttpExecutionContext context;

    public CompletionStage<Result> call(Http.Request req) {
        Dashboard dashboard;
        try {
            dashboard = DatabaseUtils.jsonToJavaClass(req.body().asJson(), Dashboard.class);
            return results(req, dashboard).thenCompose(test -> delegate.call(test));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return delegate.call(req);
    }

    public CompletableFuture<Http.Request> results(Http.Request req, Dashboard dashboard) {
        switch (req.method() + req.path()) {
            case DASHBOARD_GETALL:
                Optional<CSRF.Token> CSRFToken = CSRF.getToken(req);
                System.out.println(CSRFToken);
                return CompletableFuture.supplyAsync(() -> all(req.attrs().get(PlatformAttributes.AUTHENTICATED_USER)))
                        .thenCompose(ServiceUtils::toJsonNode)
                        .thenApply(result -> req.addAttr(PlatformAttributes.DASHBOARDS, result));
            case DASHBOARD_SAVE:
                return CompletableFuture.supplyAsync(() -> save(dashboard, req.attrs().get(PlatformAttributes.AUTHENTICATED_USER)))
                        .thenCompose(ServiceUtils::toJsonNode)
                        .thenApply(result -> req.addAttr(PlatformAttributes.DASHBOARDS, result));
            case DASHBOARD_UPDATE:
                return CompletableFuture.supplyAsync(() -> update(dashboard, req.attrs().get(PlatformAttributes.AUTHENTICATED_USER)))
                        .thenCompose(ServiceUtils::toJsonNode)
                        .thenApply(result -> req.addAttr(PlatformAttributes.DASHBOARDS, result));
            case DASHBOARD_DELETE:
                return CompletableFuture.supplyAsync(() -> delete(dashboard.getId().toString(), req.attrs().get(PlatformAttributes.AUTHENTICATED_USER)))
                        .thenCompose(ServiceUtils::toJsonNode)
                        .thenApply(result -> req.addAttr(PlatformAttributes.DASHBOARDS, result));
            case DASHBOARD_GETBYID:
                return CompletableFuture.supplyAsync(() -> findById(dashboard.getId().toString(), req.attrs().get(PlatformAttributes.AUTHENTICATED_USER)))
                        .thenCompose(ServiceUtils::toJsonNode)
                        .thenApply(result -> req.addAttr(PlatformAttributes.DASHBOARDS, result));
            default:
                return null;
        }
    }


    public CompletableFuture<List<Dashboard>> all(User authUser) {
        MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(Dashboard.class, AccessLevelType.READ);
        return new DashboardsDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).all(context.current());
    }

    public CompletableFuture<Dashboard> findById(String t, User authUser) {
        MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser)
                .withACL(Dashboard.class, AccessLevelType.READ);
        try {
            DataAccess<Dashboard> dashboardDataAccess = new DashboardsDataAccess(mongoDB.getDatabase()).withMongoRelay(relay);
            if (dashboardDataAccess.byId(new ObjectId(t)) == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));
            return dashboardDataAccess.byIdAsync(new ObjectId(t), context.current());
        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
            throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, ex.getMessage()));
        }
    }

    public CompletableFuture<Dashboard> save(Dashboard which, User authUser) {
        MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(Dashboard.class, AccessLevelType.WRITE);

        Set<String> generalAccesses = new HashSet<>();
        //todo set access for user that create this :/
        generalAccesses.add(authUser.getId().toString());
        //todo or set it as public :/
//            generalAccesses.add("*");
        Set<String> read = new HashSet<>(generalAccesses);
        Set<String> write = new HashSet<>(generalAccesses);

        read.addAll(which.getReadACL());
        write.addAll(which.getReadACL());

        which.setReadACL(read);
        which.setWriteACL(write);

        return new DashboardsDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).insert(which, context.current());
    }

    public CompletableFuture<Dashboard> update(Dashboard which, User authUser) {
        MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(Dashboard.class, AccessLevelType.WRITE);
        return new DashboardsDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).update(which, context.current());
    }

    public CompletableFuture<Dashboard> delete(String id, User authUser) {
        return this.findById(id, authUser).thenCompose((item) -> {
            if (item == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));
            MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(Dashboard.class, AccessLevelType.WRITE);
            return new DashboardsDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).deleteAsync(item, context.current());
        });
    }


}
