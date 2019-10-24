package controllers;

import actor.ChatActor;
import actor.ChatUnreadActor;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.stream.Materializer;
import models.collection.User;
import mongo.MongoDB;
import oauth2.Authenticated;
import oauth2.PlatformAttributes;
import org.bson.types.ObjectId;
import play.i18n.MessagesApi;
import play.libs.F;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.streams.ActorFlow;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.WebSocket;
import services.ChatService;
import com.typesafe.config.Config;
import utils.DatabaseUtils;
import utils.ServiceUtils;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import static play.mvc.Results.forbidden;


public class ChatController {
    @Inject
    HttpExecutionContext ec;
    @Inject
    ActorSystem actorSystem;
    @Inject
    Config config;
    @Inject
    Materializer materializer;
    @Inject
    MessagesApi messagesApi;
    @Inject
    ActorSystem system;
    @Inject
    private MongoDB database;


    public WebSocket socket(String roomId,String token) {
        Cluster cluster = Cluster.get(system);
        ChatService chatService = new ChatService(database,config,actorSystem,messagesApi);

        User authUser = chatService.getAuthUserFromToken(token);

        return WebSocket.Text.acceptOrResult((requests) -> {
            if(authUser == null)
                return CompletableFuture.completedFuture(F.Either.Left(forbidden()));

        return CompletableFuture.completedFuture(
             F.Either.Right(
                     ActorFlow.actorRef((out) -> ChatActor.props(out, roomId, authUser, chatService,ec.current()),
                             cluster.system(),materializer)
                      )
                 );
        });
    }


    public WebSocket tracker(String token) {

        return this.socket(new ObjectId().toString(), token);
//        Cluster cluster = Cluster.get(system);
//        ChatService chatService = new ChatService(database,config,actorSystem,messagesApi);
//        User user = chatService.getAuthUserFromToken(token);
//
//        return WebSocket.Text.acceptOrResult((request) -> {
//            if(user == null)
//                return CompletableFuture.completedFuture(F.Either.Left(forbidden()));
//            return CompletableFuture.completedFuture(F.Either.Right(
//                    ActorFlow.actorRef((out) -> ChatUnreadActor.props(out, user, chatService, ec.current()),
//                            cluster.system(), materializer)
//            ));
//        });
    }

    public CompletableFuture<Result> all(Http.Request request) {
        User authUser = request.attrs().get(PlatformAttributes.AUTHENTICATED_USER);
        return CompletableFuture.supplyAsync(() -> new ChatService(database,config,actorSystem,messagesApi).findMyMessages(ec.current(),authUser))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

}
