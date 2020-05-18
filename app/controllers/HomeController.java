package controllers;

import com.typesafe.config.Config;
import models.collection.User;
import mongo.MongoDB;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.HomeService;
import utils.DatabaseUtils;
import utils.ServiceUtils;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    @Inject
    MongoDB mongoDB;
    @Inject
    MessagesApi messagesApi;
    @Inject
    HttpExecutionContext context;
    @Inject
    private Config config;

    public CompletableFuture<Result> authenticate(Http.Request request) {
        return ServiceUtils.parseBodyOfType(request.body(), context.current(), User.class)
                .thenCompose((item) -> new HomeService(mongoDB.getDatabase(), config).auth(item, context.current()))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

}
