package controllers;

import jwt.VerifiedJwt;
import models.collection.Dashboard;
import models.collection.User;
import mongo.MongoDB;
import oauth2.Authenticated;
import oauth2.PlatformAttributes;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.typedmap.TypedKey;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.DashboardService;
import utils.DatabaseUtils;
import utils.ServiceUtils;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

//@Authenticated()
public class DashboardControllers {

    @Inject
    MongoDB mongoDB;
    @Inject
    MessagesApi messagesApi;

    @Inject
    HttpExecutionContext
            context;

    public CompletableFuture<Result> all(Http.RequestHeader reqHeader) {
         //To get token use VerifiedJwt token = reqHeader.attrs().get(PlatformAttributes.VERIFIED_JWT);
        User authUser = reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER);
        return CompletableFuture.supplyAsync(() -> new DashboardService(mongoDB.getDatabase()).all(context.current(), authUser))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    public CompletableFuture<Result> findById(String id,Http.RequestHeader reqHeader) {
        User authUser = reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER);
        return CompletableFuture.supplyAsync(() -> new DashboardService(mongoDB.getDatabase()).findById(id, context.current(),authUser))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> save(Http.RequestHeader reqHeader) {
        User authUser = reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER);
        return ServiceUtils.parseBodyOfType(context.current(), Dashboard.class)
                .thenCompose((item) -> new DashboardService(mongoDB.getDatabase()).save(item, context.current(),authUser))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    public CompletableFuture<Result> update(Http.RequestHeader reqHeader) {
        User authUser = reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER);
        return ServiceUtils.parseBodyOfType(context.current(), Dashboard.class)
                .thenCompose((item) -> new DashboardService(mongoDB.getDatabase()).update(item, context.current(),authUser))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    public CompletableFuture<Result> delete(String id,Http.RequestHeader reqHeader) {
        User authUser = reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER);
        return CompletableFuture.supplyAsync(() -> new DashboardService(mongoDB.getDatabase()).delete(id, context.current(),authUser))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

}
