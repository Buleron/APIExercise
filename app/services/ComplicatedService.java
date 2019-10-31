package services;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.client.MongoDatabase;
import data.ContentDataAccess;
import models.collection.User;
import models.collection.content.Content;
import models.enums.AccessLevelType;
import models.exceptions.RequestException;
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
public class ComplicatedService {
	@Inject
	ContentService service;
//	@Inject
//	OtherService other;

}
