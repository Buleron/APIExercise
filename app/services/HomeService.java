package services;

import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.typesafe.config.Config;
import jwt.JwtControllerHelper;
import models.collection.User;
import models.exceptions.RequestException;
import org.bson.Document;
import play.Logger;
import play.mvc.Http;
import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
public class HomeService {
    private MongoDatabase database;

    public HomeService(MongoDatabase mongoDatabase) {
        this.database = mongoDatabase;
    }

    public CompletableFuture<Document> auth(User user, Executor context) {
        return CompletableFuture.supplyAsync(() -> {
            MongoCollection<Document> content = database.getCollection("users");
            BasicDBObject query = new BasicDBObject();
            query.put("username", user.getUsername());
            query.put("password", user.getPassword());
            Document doc = content.find(query).first();
            if(doc == null)
                throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, "Authentication Failed"));
            try {
                String token = getSignedToken(user.getUsername());
                doc.clear();
                doc.append("token",token);
            } catch (UnsupportedEncodingException e) {
                Logger.of(e.getMessage());
                e.printStackTrace();
            }
            return doc;
        }, context);
    }

        private String getSignedToken(String username) throws UnsupportedEncodingException {
        String secret = "changeme";
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("excerciseApi")
                .withClaim("user_id", username)
                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusYears(10).toInstant()))
                .sign(algorithm);
    }
}
