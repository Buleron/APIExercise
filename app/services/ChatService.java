package services;

import akka.actor.ActorSystem;
import com.google.common.base.Strings;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.typesafe.config.Config;
import data.ChatDataAccess;
import data.ContentDataAccess;
import jwt.JwtValidator;
import models.collection.User;
import models.collection.chat.ChatMessage;
import models.enums.AccessLevelType;
import models.exceptions.RequestException;
import models.responses.PaginatedChatMessages;
import mongo.MongoDB;
import mongolay.MongoRelay;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import java.util.ArrayList;
import javax.inject.Inject;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import static oauth2.AuthenticatedAction.getUser;
import static utils.Constants.*;

public class ChatService {
    @Inject
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

    public CompletableFuture<PaginatedChatMessages> findByUsersIdRoomIdPagination(Executor context, String roomId, String userId, String search, int limit, int skip, String until) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<ChatMessage> collection = mongoDB.getDatabase().getCollection(CHAT, ChatMessage.class);
            FindIterable<ChatMessage> list = collection.find();

            List<Bson> filters = new ArrayList<>();
            String pageToken = Strings.isNullOrEmpty(until) ? new ObjectId().toString() : until;
            filters.add(Filters.lte("_id", new ObjectId(pageToken)));

            if (!Strings.isNullOrEmpty(roomId))
                filters.add(Filters.eq("roomId", new ObjectId(roomId)));

            if (!Strings.isNullOrEmpty(userId))
                filters.add(Filters.eq("userId", new ObjectId(userId)));

            if (!Strings.isNullOrEmpty(search))
                filters.add(new Document("$text", new Document("$search", search)));

            if (filters.size() > 0)
                list = list.filter(Filters.and(filters));

            List<ChatMessage> items = list.limit(limit).skip(skip).sort(Sorts.ascending("_id")).into(new ArrayList<>());
            return new PaginatedChatMessages(until, items);
        }, context);
    }

    public CompletableFuture<ChatMessage> save(ChatMessage chatMessage,Executor context) {
        MongoRelay relay = new MongoRelay(mongoDB.getDatabase()).withACL(ChatMessage.class, AccessLevelType.WRITE);
        return new ChatDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).insert(chatMessage,context);
    }

    public User getAuthUserFromToken(String token) {
        return getUser(token, jwtValidator, mongoDB);
    }

}
