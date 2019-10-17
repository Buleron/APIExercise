package controllers;

import models.collection.Dashboard;
import mongo.MongoDB;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.DashboardService;
import utils.DatabaseUtils;
import utils.ServiceUtils;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class DashboardControllers {

    @Inject
    MongoDB mongoDB;
    @Inject
    MessagesApi messagesApi;

    @Inject
    HttpExecutionContext
    context;

    public CompletableFuture<Result> all (Http.Request request) {
        return CompletableFuture.supplyAsync(() -> new DashboardService(mongoDB.getDatabase()).all(context.current()))
            .thenCompose(ServiceUtils::toJsonNode)
            .thenApply(Results::ok)
            .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }
    public CompletableFuture<Result> findById(String id){
        return CompletableFuture.supplyAsync(() -> new DashboardService(mongoDB.getDatabase()).findById(id,context.current()))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception,messagesApi));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> save(Http.Request request) {
        return ServiceUtils.parseBodyOfType(context.current(), Dashboard.class)
                .thenCompose((item) -> new DashboardService(mongoDB.getDatabase()).save(item, context.current()))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> update(Http.Request request) {
        return ServiceUtils.parseBodyOfType(context.current(), Dashboard.class)
                .thenCompose((item) -> new DashboardService(mongoDB.getDatabase()).update(item, context.current()))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    public CompletableFuture<Result> delete(String id) {
        return CompletableFuture.supplyAsync(() -> new DashboardService(mongoDB.getDatabase()).delete(id, context.current()))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

}
