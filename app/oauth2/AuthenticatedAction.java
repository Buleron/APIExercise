package oauth2;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import jwt.JwtValidator;
import jwt.VerifiedJwt;
import models.collection.User;
import models.collection.UserToken;
import models.exceptions.RequestException;
import mongo.MongoDB;
import org.bson.types.ObjectId;
import play.i18n.MessagesApi;
import play.libs.F;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import utils.DatabaseUtils;
import static utils.Constants.*;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;


public class AuthenticatedAction extends Action<Authenticated> {
    @Inject
    MongoDB mongoDB;
    @Inject
    MessagesApi messagesApi;

    @javax.inject.Inject
    HttpExecutionContext context;
    private JwtValidator jwtValidator;

    @javax.inject.Inject
    public AuthenticatedAction(JwtValidator jwtValidator) {
        this.jwtValidator = jwtValidator;
    }


    @Override
    public CompletionStage<Result> call(Http.Request req) {

        return CompletableFuture.supplyAsync(() -> {

            Optional<String> authorization = req.getHeaders().get(AUTHORIZATION);

            if (!authorization.filter(ah -> ah.contains(BEARER)).isPresent())
                throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, NO_BEARER));

            String token = authorization.map(ah -> ah.replace(BEARER, "")).orElse("");
            return getUser(token, jwtValidator, mongoDB);
        }, context.current()).thenCompose((user) -> {
            Http.Request newReq = req.addAttr(PlatformAttributes.AUTHENTICATED_USER, user);
            return delegate.call(newReq);
        }).exceptionally((exception) -> {
            exception.printStackTrace();
            return DatabaseUtils.resultFromThrowable(exception, messagesApi);
        });
    }

    public static User getUser(String token, JwtValidator jwtValidator, MongoDB mongoDB) {
        F.Either<JwtValidator.Error, VerifiedJwt> response = jwtValidator.verify(token);

        if (response.left.isPresent())
            throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, WRONG_TOKEN));

        MongoCollection<UserToken> tokens = mongoDB.getDatabase().getCollection(TOKEN_COLLECTION, UserToken.class);
        UserToken userToken = tokens.find(Filters.eq("token", token)).first();

        if (userToken == null)
            throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, ACCESS_FORBIDDEN));

        try {
            String userID = userToken.getUserId(token);
            MongoCollection<User> usr = mongoDB.getDatabase().getCollection(USERS, User.class);
            BasicDBObject query = new BasicDBObject();
            query.put("_id", new ObjectId(userID));
            return usr.find(query).first();
        } catch (IllegalArgumentException | NullPointerException ex) {
            ex.printStackTrace();
            throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, ACCESS_FORBIDDEN));
        }
    }
}
