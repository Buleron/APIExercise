package controllers;

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
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import org.webjars.play.WebJarsUtil;
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

    public CompletableFuture<Result> authenticate(Http.Request request) {
        return ServiceUtils.parseBodyOfType(context.current(), User.class)
                .thenCompose((item) -> new HomeService(mongoDB.getDatabase()).auth(item, context.current()))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    @Inject
    public HomeController(ActorSystem actorSystem, Materializer mat, WebJarsUtil webJarsUtil) {

    }
}
