package controllers;

import models.collection.User;
import mongo.MongoDB;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.HomeService;
import services.UserService;
import utils.DatabaseUtils;
import utils.ServiceUtils;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class UserController {

    @Inject
    MongoDB mongoDB;
    @Inject
    MessagesApi messagesApi;
    @Inject
    HttpExecutionContext context;

    public CompletableFuture<Result> allUsers(Http.Request request) {
        return ServiceUtils.parseBodyOfType(context.current(), User.class)
                .thenCompose((item) -> new UserService(mongoDB.getDatabase()).all(context.current()))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }


}
