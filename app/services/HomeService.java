package services;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.AllArgsConstructor;
import models.collection.User;
import models.collection.UserToken;
import models.exceptions.RequestException;
import org.bson.Document;
import play.Logger;
import play.mvc.Http;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@AllArgsConstructor
public class HomeService {
    private MongoDatabase database;

    public CompletableFuture<Document> auth(User user, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> content = database.getCollection("users");
            BasicDBObject query = new BasicDBObject();
            query.put("username", user.getUsername());
            query.put("password", user.getPassword());
            Document doc = content.find(query).first();
            if (doc == null)
                throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, "Authentication Failed"));
            try {
                String token = getSignedToken(doc.get("_id").toString());
                doc.clear();
                doc.append("token", token);
                // store it to mongo
                UserToken userToken = new UserToken();
                userToken.setToken(token);
                userToken.setClient(user.getUsername());
                userToken.setExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusYears(10).toInstant()));
                MongoCollection<UserToken> userTokenMongoCollection = database.getCollection("tokens", UserToken.class);
                userTokenMongoCollection.insertOne(userToken);
            } catch (UnsupportedEncodingException e) {
                Logger.of(e.getMessage());
                e.printStackTrace();
            }
            return doc;
        }, context);
    }

    private String getSignedToken(String userId) throws UnsupportedEncodingException {
        String secret = "changeme";
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("excerciseApi")
                .withClaim("user_id", userId)
                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusYears(10).toInstant()))
                .sign(algorithm);
    }
}
