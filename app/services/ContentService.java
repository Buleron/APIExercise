package services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.sun.javaws.exceptions.InvalidArgumentException;
import data.ContentDataAccess;
import data.DataAccess;
import models.collection.Dashboard;
import models.collection.content.Content;
import models.collection.User;
import models.enums.AccessLevelType;
import models.exceptions.RequestException;
import modules.SingleThreadedExecutionContext;
import mongolay.MongoRelay;
import org.bson.Document;
import org.bson.types.ObjectId;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import static utils.Constants.*;

@Singleton
public class ContentService {
	@Inject
    private MongoDatabase database;

	@Inject
	HttpExecutionContext context;
	@Inject
	SingleThreadedExecutionContext singleThreadedExecutionContext;

    public CompletableFuture<List<Content>> all(User authUser) {
		MongoRelay relay = new MongoRelay(database, authUser).withACL(Content.class, AccessLevelType.READ);
		return new ContentDataAccess(database).withMongoRelay(relay).all(context.current());
    }

    public CompletableFuture<Content> findById(String x, User authUser) {
		MongoRelay relay = new MongoRelay(database, authUser)
				.withACL(Content.class, AccessLevelType.READ);
		try {
			return new ContentDataAccess(database).withMongoRelay(relay).byId(new ObjectId(x), singleThreadedExecutionContext);
		} catch (IllegalArgumentException ex) {
			throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, "fasadsga"));
		}
    }

    public CompletableFuture<Content> save(Content resContent, User authUser) {
		MongoRelay relay = new MongoRelay(database, authUser).withACL(Content.class, AccessLevelType.WRITE);

		Set<String> generalAccesses = new HashSet<>();
		//todo set access for user that create this :/
		generalAccesses.add(authUser.getId().toString());
		//todo or set it as public :/
//            generalAccesses.add("*");
		Set<String> read = new HashSet<>(generalAccesses);
		Set<String> write = new HashSet<>(generalAccesses);

		read.addAll(resContent.getReadACL());
		write.addAll(resContent.getReadACL());

		resContent.setReadACL(read);
		resContent.setWriteACL(write);
		return new ContentDataAccess(database).withMongoRelay(relay).insert(resContent, context.current());
	}

    public CompletableFuture<Content> update(Content resContent, User authUser) {
		MongoRelay relay = new MongoRelay(database, authUser).withACL(Content.class, AccessLevelType.WRITE);
		return new ContentDataAccess(database).withMongoRelay(relay).insert(resContent, context.current());
    }

    public CompletableFuture<Content> delete(String contentID, User authUser) {
    	return this.findById(contentID, authUser).thenCompose((item) -> {
			MongoRelay relay = new MongoRelay(database, authUser).withACL(Content.class, AccessLevelType.WRITE);
			return new ContentDataAccess(database).withMongoRelay(relay).deleteAsynch(item, context.current());
		});
    }
}
