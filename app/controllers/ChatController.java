package controllers;

import actor.ChatActor;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.stream.Materializer;
import models.collection.User;
import mongo.MongoDB;
import play.i18n.MessagesApi;
import play.libs.F;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.streams.ActorFlow;
import play.mvc.WebSocket;
import services.ChatService;
import com.typesafe.config.Config;
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

    public WebSocket socket(String roomId, String token) {
        Cluster cluster = Cluster.get(system);
        ChatService chatService = new ChatService(database, config, actorSystem, messagesApi);
        User authUser = chatService.getAuthUserFromToken(token);
        return WebSocket.Text.acceptOrResult((requests) -> {
            if (authUser == null)
                return CompletableFuture.completedFuture(F.Either.Left(forbidden()));
            return CompletableFuture.completedFuture(
                    F.Either.Right(
                            ActorFlow.actorRef((out) -> ChatActor.props(out, roomId, authUser, chatService, ec.current()),
                                    cluster.system(), materializer)
                    )
            );
        });
    }

}
