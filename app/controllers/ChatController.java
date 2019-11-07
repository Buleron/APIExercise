package controllers;

import actor.ChatActor;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.stream.Materializer;
import jwt.JwtValidator;
import models.collection.User;
import mongo.MongoDB;
import oauth2.PlatformAttributes;
import play.i18n.MessagesApi;
import play.libs.F;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.streams.ActorFlow;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import play.mvc.WebSocket;
import services.ChatService;
import utils.DatabaseUtils;
import utils.ServiceUtils;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

import static play.mvc.Results.forbidden;

public class ChatController {
    @Inject
    HttpExecutionContext ec;
    @Inject
    Materializer materializer;
    @Inject
    MessagesApi messagesApi;
    @Inject
    ActorSystem system;
    @Inject
    private MongoDB database;
    @Inject
    private JwtValidator jwtValidator;

    public WebSocket socket(String roomId, String token) {
        Cluster cluster = Cluster.get(system);
        ChatService chatService = new ChatService(database, jwtValidator);
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

    public CompletableFuture<Result> sortMessages(Http.RequestHeader reqHeader, String roomId, String search, int limit, int skip, String until) {
        return CompletableFuture.supplyAsync(() -> new ChatService(database, jwtValidator)
                .findByUsersIdRoomIdPagination(ec.current(), roomId, reqHeader.attrs().get(PlatformAttributes.AUTHENTICATED_USER).getId().toString(), search, limit, skip, until))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

}
