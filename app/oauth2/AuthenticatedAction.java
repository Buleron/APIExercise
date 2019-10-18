package oauth2;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import models.collection.User;
import models.collection.UserToken;
import models.exceptions.RequestException;
import mongo.MongoDB;
import org.bson.Document;
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
			if(!token.contains("Bearer ")){
				throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, "No bearer token type"));
			}
			//remove Bearer; and check if exists into mongodb
			MongoCollection<UserToken> tokens = mongoDB.getDatabase().getCollection("tokens", UserToken.class);
			UserToken userToken = tokens.find(Filters.eq("token", token.substring(7))).first();

			// if token is missing at mongo, throw unathorized
			if (userToken == null) {
				throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, "access_forbidden"));
			}
			try {
				// if token exists, find the user for that token
				// if user found, put it into attributes at request
				String userID = userToken.getUserId(token.substring(7));
				MongoCollection<User> usr = mongoDB.getDatabase().getCollection("users",User.class);
				BasicDBObject query = new BasicDBObject();
				query.put("_id", new ObjectId(userID));
				User res = usr.find(query).first();
				return res;
			} catch (IllegalArgumentException|NullPointerException ex) {
				ex.printStackTrace();
				// if user not found, throw unathorized
				throw new CompletionException(new RequestException(Http.Status.UNAUTHORIZED, "access_forbidden"));
			}
		}, context.current()).thenComposeAsync((user) -> {
			req.addAttr(PlatformAttributes.AUTHENTICATED_USER, user);
			return delegate.call(req);
		}).exceptionally((exception) -> {
			exception.printStackTrace();
			return DatabaseUtils.resultFromThrowable(exception, messagesApi);
		});
	}
}
