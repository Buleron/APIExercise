package services;

import akka.actor.ActorSystem;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.typesafe.config.Config;
import models.collection.Content;
import models.collection.User;
import models.collection.UserToken;
import models.collection.chat.ChatMessage;
import models.exceptions.RequestException;
import mongo.MongoDB;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.Logger;
import play.i18n.MessagesApi;
import play.mvc.Http;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static utils.Constants.*;
import static utils.Constants.CONTENT;


public class ChatService {
    private MongoDB mongoDB;
    private Config config;
    private ActorSystem actorSystem;
    private MessagesApi messagesApi;

    public ChatService(MongoDB mongoDB, Config config, ActorSystem actorSystem, MessagesApi messagesApi) {
        this.mongoDB = mongoDB;
        this.config = config;
        this.actorSystem = actorSystem;
        this.messagesApi = messagesApi;
    }

    public CompletableFuture<List<ChatMessage>> findByUsersIdRoomId(String roomId, String userId, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<ChatMessage> content = mongoDB.getDatabase().getCollection(CHAT, ChatMessage.class);

            BasicDBObject query = new BasicDBObject();
            if (!roomId.equals("all"))
                query.put("roomId", new ObjectId(roomId));
            query.put("userId", new ObjectId(userId));

            if (content.find(query).first() == null)
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));

            //List<String> access = accessService.GetAccesses(authUser);
            return content.find(query).into(new ArrayList<>());
        }, context);
    }

    public CompletableFuture<ChatMessage> save(ChatMessage chatMessage, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<ChatMessage> message = mongoDB.getDatabase().getCollection(CHAT, ChatMessage.class);
                chatMessage.setId(new ObjectId());
                Logger.debug("Chat message: {}", chatMessage.toString());
                message.insertOne(chatMessage);
                return chatMessage;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));
            }
        }, context);
    }

    public User getAuthUserFromToken(String token) {

        if (token.contains("Bearer "))
            token = token.substring(7);

        MongoCollection<UserToken> tokens = mongoDB.getDatabase().getCollection("tokens", UserToken.class);
        UserToken userToken = tokens.find(Filters.eq("token", token)).first();

        // if token is missing at mongo, throw unathorized
        if (userToken == null) {
            throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, "access_forbidden"));
        }
        try {
            String userID = userToken.getUserId(token);
            MongoCollection<User> user = mongoDB.getDatabase().getCollection("users", User.class);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(userID));
            return user.find(query).first();

        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
            // if user not found, throw unauthorized
            throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, "access_forbidden"));
        }
    }

}
