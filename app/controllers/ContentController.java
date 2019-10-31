package controllers;

import models.collection.content.Content;
import models.collection.User;
import oauth2.PlatformAttributes;
import play.mvc.BodyParser;
import play.mvc.Result;
import mongo.MongoDB;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Results;
import services.ContentService;
import utils.DatabaseUtils;
import utils.ServiceUtils;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class ContentController {
    @Inject
    MessagesApi messagesApi;
    @Inject
    HttpExecutionContext context;
    @Inject
    ContentService service;

    public CompletableFuture<Result> all(Http.RequestHeader reqHeader) {
        User authUser = reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER);
        return CompletableFuture.supplyAsync(() -> service.all(authUser))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    public CompletableFuture<Result> findById(String id, Http.RequestHeader reqHeader) {
        User authUser = reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER);
        return CompletableFuture.supplyAsync(() -> service.findById(id, authUser))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> save(Http.RequestHeader reqHeader) {
        User authUser = reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER);
        return ServiceUtils.parseBodyOfType(context.current(), Content.class)
                .thenCompose((item) -> service.save(item, authUser))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> update(Http.RequestHeader reqHeader) {
        User authUser = reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER);
        return ServiceUtils.parseBodyOfType(context.current(), Content.class)
                .thenCompose((item) -> service.update(item, authUser))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    public CompletableFuture<Result> delete(String id,Http.RequestHeader reqHeader) {
        User authUser = reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER);
        return CompletableFuture.supplyAsync(() -> service.delete(id, authUser))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }
}
