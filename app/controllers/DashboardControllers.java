package controllers;

import models.collection.Dashboard;
import oauth2.PlatformAttributes;
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
    MessagesApi messagesApi;
    @Inject
    HttpExecutionContext context;
    @Inject
    DashboardService service;

    public CompletableFuture<Result> all(Http.RequestHeader reqHeader) {
        return CompletableFuture.supplyAsync(() -> service.all(reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER)))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    public CompletableFuture<Result> findById(String id, Http.RequestHeader reqHeader) {
        return CompletableFuture.supplyAsync(() -> service.findById(ServiceUtils.escapeXSS(id), reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER)))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> save(Http.Request request) {
        return ServiceUtils.parseBodyOfType(request.body(), context.current(), Dashboard.class)
                .thenCompose((item) -> service.save(item, request.attrs().get(PlatformAttributes.AUTHENTICATED_USER)))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    public CompletableFuture<Result> update(Http.Request request) {
        return ServiceUtils.parseBodyOfType(request.body(), context.current(), Dashboard.class)
                .thenCompose((item) -> service.update(item, request.attrs().get(PlatformAttributes.AUTHENTICATED_USER)))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    public CompletableFuture<Result> delete(String id, Http.RequestHeader reqHeader) {
        return CompletableFuture.supplyAsync(() -> service.delete(ServiceUtils.escapeXSS(id), reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER)))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

}
