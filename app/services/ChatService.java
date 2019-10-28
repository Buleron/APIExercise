package services;

import akka.actor.ActorSystem;
import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.typesafe.config.Config;
import jwt.JwtValidator;
import models.collection.User;
import models.collection.chat.ChatMessage;
import models.exceptions.RequestException;
import mongo.MongoDB;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.mvc.Http;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import static oauth2.AuthenticatedAction.getUser;
import static utils.Constants.*;

public class ChatService {
    private MongoDB mongoDB;
    private JwtValidator jwtValidator;


    public ChatService(MongoDB mongoDB, JwtValidator jwtValidator) {
        this.mongoDB = mongoDB;
        this.jwtValidator = jwtValidator;
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

    public CompletableFuture<List<ChatMessage>> findByUsersIdRoomIdPagination(Executor context, String roomId, String userId, String search, int limit, int skip, String until) {
        return CompletableFuture.supplyAsync(() -> {

            List<ChatMessage> items = new ArrayList<>();
            MongoCollection<ChatMessage> collection = mongoDB.getDatabase().getCollection(CHAT, ChatMessage.class);
            FindIterable<ChatMessage> list = collection.find();

            List<Bson> filters = new ArrayList<>();
            if (!Strings.isNullOrEmpty(until))
                filters.add(Filters.lte("_id", new ObjectId(until)));

            if (!Strings.isNullOrEmpty(roomId))
                filters.add(Filters.eq("roomId", new ObjectId(roomId)));

            if (!Strings.isNullOrEmpty(userId))
                filters.add(Filters.eq("userId", new ObjectId(userId)));

            if (!Strings.isNullOrEmpty(search))
                filters.add(new Document("$text", new Document("$search", search)));

            if (filters.size() > 0)
                list = list.filter(Filters.and(filters));

            list.limit(limit).skip(skip).sort(Sorts.ascending("_id")).into(items);

            return items;
        }, context);
    }

    public CompletableFuture<ChatMessage> save(ChatMessage chatMessage, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                MongoCollection<ChatMessage> message = mongoDB.getDatabase().getCollection(CHAT, ChatMessage.class);
                chatMessage.setId(new ObjectId());
                message.insertOne(chatMessage);
                return chatMessage;
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new CompletionException(new RequestException(Http.Status.NOT_FOUND, NOT_FOUND));
            }
        }, context);
    }

    public User getAuthUserFromToken(String token) {
        return getUser(token, jwtValidator, mongoDB);
    }

}
