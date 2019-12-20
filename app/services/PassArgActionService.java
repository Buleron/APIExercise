package services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import data.DashboardsDataAccess;
import data.DataAccess;
import models.collection.Dashboard;
import models.collection.User;
import models.enums.AccessLevelType;
import models.exceptions.RequestException;
import mongo.MongoDB;
import mongolay.MongoRelay;
import oauth2.PlatformAttributes;
import org.bson.types.ObjectId;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import utils.DatabaseUtils;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

import static utils.Constants.NOT_FOUND;


public class PassArgActionService extends play.mvc.Action.Simple {

    @Inject
    private MongoDB mongoDB;
    @Inject
    HttpExecutionContext context;

    public CompletionStage<Result> call(Http.Request req) {

        Dashboard dashboard = new Dashboard();
        ObjectMapper mapper = new ObjectMapper();

        try {
            dashboard = DatabaseUtils.jsonToJavaClass(req.body().asJson(), Dashboard.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (req.method().equals("GET")) {
            return delegate.call(req.addAttr(PlatformAttributes.DASHBOARDACTION, mapper.convertValue(all(req.attrs().get(PlatformAttributes.AUTHENTICATED_USER)), JsonNode.class)));
        }
        if (req.method().equals("POST")) {
            return delegate.call(req.addAttr(PlatformAttributes.DASHBOARDACTION, mapper.convertValue(save(dashboard, req.attrs().get(PlatformAttributes.AUTHENTICATED_USER)), JsonNode.class)));
        }
        if (req.method().equals("PUT")) {
            return delegate.call(req.addAttr(PlatformAttributes.DASHBOARDACTION, mapper.convertValue(update(dashboard, req.attrs().get(PlatformAttributes.AUTHENTICATED_USER)), JsonNode.class)));
        }
        if (req.method().equals("DELETE")) {
            return delegate.call(req.addAttr(PlatformAttributes.DASHBOARDACTION, mapper.convertValue(delete(dashboard.getId().toString(), req.attrs().get(PlatformAttributes.AUTHENTICATED_USER)), JsonNode.class)));

        } else {
            return delegate.call(req.addAttr(PlatformAttributes.DASHBOARDACTION, mapper.convertValue(dashboard, JsonNode.class)));
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
