package services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import data.ContentDataAccess;
import models.collection.User;
import models.collection.content.Content;
import models.enums.AccessLevelType;
import models.exceptions.RequestException;
import modules.SingleThreadedExecutionContext;
import mongo.MongoDB;
import mongolay.MongoRelay;
import org.bson.types.ObjectId;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Singleton
public class ContentService {
	@Inject
    private MongoDB mongoDB;
	@Inject
	HttpExecutionContext context;
	@Inject
	SingleThreadedExecutionContext singleThreadedExecutionContext;

    public CompletableFuture<List<Content>> all(User authUser) {
		MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(Content.class, AccessLevelType.READ);
		return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).all(context.current());
    }

    public CompletableFuture<Content> findById(String x, User authUser) {
		MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser)
				.withACL(Content.class, AccessLevelType.READ);
		try {
			return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).byId(new ObjectId(x), singleThreadedExecutionContext);
		} catch (IllegalArgumentException ex) {
			throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, "fasadsga"));
		}
    }

    public CompletableFuture<Content> save(Content resContent, User authUser) {
		MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(Content.class, AccessLevelType.WRITE);

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
		return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).insert(resContent, context.current());
	}

    public CompletableFuture<Content> update(Content resContent, User authUser) {
		MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(Content.class, AccessLevelType.WRITE);
		return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).insert(resContent, context.current());
    }

    public CompletableFuture<Content> delete(String contentID, User authUser) {
    	return this.findById(contentID, authUser).thenCompose((item) -> {
			MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(Content.class, AccessLevelType.WRITE);
			return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).deleteAsynch(item, context.current());
		});
    }
}
