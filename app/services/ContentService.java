package services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import data.ContentDataAccess;
import data.DataAccess;
import models.collection.User;
import models.collection.content.DashboardContent;
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

    public CompletableFuture<List<DashboardContent>> all(User authUser) {
		MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(DashboardContent.class, AccessLevelType.READ);
		return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).all(context.current());
    }

    public CompletableFuture<DashboardContent> findById(String x, User authUser) {
		MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser)
				.withACL(DashboardContent.class, AccessLevelType.READ);
		try {
			DataAccess<DashboardContent> access = new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay);
			return access.byIdAsync(new ObjectId(x), context.current());
		} catch (IllegalArgumentException ex) {
			throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, "fasadsga"));
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, "fasadsga"));
		}
    }

    public CompletableFuture<DashboardContent> save(DashboardContent resDashboardContent, User authUser) {
		MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(DashboardContent.class, AccessLevelType.WRITE);

		Set<String> generalAccesses = new HashSet<>();
		//todo set access for user that create this :/
		generalAccesses.add(authUser.getId().toString());
		//todo or set it as public :/
//            generalAccesses.add("*");
		Set<String> read = new HashSet<>(generalAccesses);
		Set<String> write = new HashSet<>(generalAccesses);

		read.addAll(resDashboardContent.getReadACL());
		write.addAll(resDashboardContent.getReadACL());

		resDashboardContent.setReadACL(read);
		resDashboardContent.setWriteACL(write);
		return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).insert(resDashboardContent, context.current());
	}

    public CompletableFuture<DashboardContent> update(DashboardContent resDashboardContent, User authUser) {
		MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(DashboardContent.class, AccessLevelType.WRITE);
		return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).insert(resDashboardContent, context.current());
    }

    public CompletableFuture<DashboardContent> delete(String contentID, User authUser) {
    	return this.findById(contentID, authUser).thenCompose((item) -> {
			MongoRelay relay = new MongoRelay(mongoDB.getDatabase(), authUser).withACL(DashboardContent.class, AccessLevelType.WRITE);
			return new ContentDataAccess(mongoDB.getDatabase()).withMongoRelay(relay).deleteAsync(item, context.current());
		});
    }
}
