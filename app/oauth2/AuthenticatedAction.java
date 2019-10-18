package oauth2;

import com.google.inject.Inject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import models.collection.User;
import models.collection.UserToken;
import models.exceptions.RequestException;
import mongo.MongoDB;
import org.bson.types.ObjectId;
import play.Logger;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.Result;
import utils.Constants;
import utils.DatabaseUtils;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CompletionStage;

/**
 * Created by Agon on 11/29/2016.
 */
public class AuthenticatedAction extends Action<Authenticated>  {
	@Inject
	MongoDB mongoDB;
	@Inject
    MessagesApi messagesApi;

	@javax.inject.Inject
	HttpExecutionContext context;

	@Override
	public CompletionStage<Result> call(Http.Request req) {

		return CompletableFuture.supplyAsync(() -> {
			// request extract headers on Authentitcation
			Optional<String> authorization = req.getHeaders().get("Authorization");

			// there you should find a token
			// if token is missing at headers, throw unathorized
			if (!authorization.isPresent()) {
				throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, "access_forbidden"));
			}
			// Bearer asfasdgasdgahg
			String token = authorization.get();

			MongoCollection<UserToken> tokens = mongoDB.getDatabase().getCollection("tokens", UserToken.class);
			UserToken userToken = tokens.find(Filters.eq("token", token)).first();

			// if token is missing at mongo, throw unathorized
			if (userToken == null) {
				throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, "access_forbidden"));
			}
			String userId = userToken.getUserId();
			MongoCollection<User> users = mongoDB.getDatabase().getCollection("tokens", User.class);
			try {
				// if token exists, find the user for that token
				// if user found, put it into attributes at request
				return users.find(Filters.eq("_id", new ObjectId(userId))).first();
			} catch (IllegalArgumentException|NullPointerException ex) {
				ex.printStackTrace();
				// if user not found, throw unathorized
				throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, "access_forbidden"));
			}
		}, context.current()).thenCompose((user) -> {
			req.addAttr(PlatformAttributes.AUTHENTICATED_USER, user);
			return delegate.call(req);
		}).exceptionally((exception) -> {
			exception.printStackTrace();
			return DatabaseUtils.resultFromThrowable(exception, messagesApi);
		});
	}
}
