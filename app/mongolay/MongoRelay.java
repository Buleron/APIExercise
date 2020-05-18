package mongolay;

import com.google.common.base.Strings;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Wither;
import models.collection.CollectionModel;
import models.collection.User;
import models.enums.AccessLevelType;
import models.exceptions.RequestException;
import mongo.AccessControl;
import mongolay.annotations.Entity;
import mongolay.annotations.Reference;
import mongolay.utils.HibernateValidator;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bson.Document;
import org.bson.conversions.Bson;
import play.Logger;
import play.mvc.Http;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

/**
 * Created by agonlohaj on 07 Aug, 2019
 */
@AllArgsConstructor
public class MongoRelay {
	/**
	 * Mongo Relay on User
	 */
	@Getter
	private User user;

	/**
	 * Simple Mongo Database
	 */
	@Getter
	MongoDatabase database;

	/**
	 * Depth
	 */
	@Getter
	@Wither
	private int depth = 0;

	/**
	 * Max Reference Depth
	 */
	@Getter
	private int maxDepth = 1;

	Map<Class, AccessLevelType> collectionClassAcl = new HashMap<>();
	Map<String, AccessLevelType> collectionNameAcl = new HashMap<>();

	/**
	 * Constructs a new Mongo Relay, given only the factory, useful on Global Collections without ACL control
	 * @param database
	 */
	public MongoRelay (MongoDatabase database) {
		this.database = database;
	}

	/**
	 * Constructs a new Mongo Relay, given the user, has access to user database, and can enforce ACL
	 * @param database
	 * @param user
	 */
	public MongoRelay (MongoDatabase database, User user) {
		this(database);
		this.user = user;
	}

	/**
	 * Constructs a new Mongo Relay, given the user, has access to user database, and can enforce ACL
	 * @param copy
	 */
	public MongoRelay (MongoRelay copy) {
		this.user = copy.user;
		this.database = copy.database;
		this.depth = copy.depth;
		this.maxDepth = copy.maxDepth;
		this.collectionClassAcl = copy.collectionClassAcl;
		this.collectionNameAcl = copy.collectionNameAcl;

	}

	public MongoRelay withMaxDepth (int maxDepth) {
		this.maxDepth = maxDepth;
		return this;
	}

	public MongoRelay withACL (Class clazz, AccessLevelType type) {
		collectionClassAcl.put(clazz, type);
		return this;
	}

	public MongoRelay withACL (String collectionName, AccessLevelType type) {
		collectionNameAcl.put(collectionName, type);
		return this;
	}

	protected  <T> boolean isValid (T value, MongoCollection<T> collection) {
		return this.isValid(value, collection, null);
	}

	protected <T> boolean isValid (T value, MongoCollection<T> collection, Bson filter) {
		return HibernateValidator.isValid(value);
	}

	protected <T> CompletableFuture<T> validateAsynch (T value, MongoCollection<T> collection, Executor context) {
		return this.validateAsynch(value, collection, null, context);
	}

	protected <T> CompletableFuture<T> validateAsynch (T value, MongoCollection<T> collection, Bson filter, Executor context) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return this.validate(value, collection, filter);
			} catch (RequestException exception) {
				throw new CompletionException(exception);
			}
		}, context);
	}

	protected <T> T validate (T value, MongoCollection<T> collection) throws RequestException {
		return this.validate(value, collection, null);
	}

	@SuppressWarnings("unchecked")
	protected <T> T validate (T value, MongoCollection<T> collection, Bson filter) throws RequestException {
		// perform a Hibernate Check
		HibernateValidator.validate(value);
		// check if its with read or write ACL
		AccessControl accessControl = this.accessControl(value.getClass());

		if (accessControl == null) {
			return value;
		}
		// I can only check access for Mongo Collection Model
		if (value instanceof CollectionModel) {
			CollectionModel model = (CollectionModel) value;
			if (!accessControl.isAccessibleOnCollection((MongoCollection<CollectionModel>) collection, filter, model.getId())) {
				throw new RequestException(Http.Status.FORBIDDEN, "access_forbidden");
			}
		}
		// or Document model
		if (value instanceof Document) {
			Document model = (Document) value;
			if (!accessControl.isAccessible((MongoCollection<Document>) collection, filter, model.getObjectId("_id"))) {
				throw new RequestException(Http.Status.FORBIDDEN, "access_forbidden");
			}
		}

		return value;
	}

	/**
	 * If the class has entity defined, or its simple class name is the same as the mongo collection name, than use this!
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public <T> RelayDatabase<T> on(Class<T> clazz) {
		return new RelayDatabase<T>(this, this.getDatabase()).withClass(clazz).withCollectionName(this.entityName(clazz));
	}

	/**
	 * Using this method, you won't benefit out of internal mappings, instead this would be you're normal
	 * Mongo Document class for which this library cannot add mappings or ACL
	 * @param collectionName
	 * @return
	 */
	@Deprecated
	public RelayDatabase<Document> on(String collectionName) {
		return new RelayDatabase<Document>(this, this.getDatabase()).withClass(Document.class).withCollectionName(collectionName);
	}

	/**
	 * Given the initial collection type, get the mongo-database with a different result class,
	 * Comes in handy when you start your queries in a class and end up in a different class from the source
	 * Aggregations will use this often
	 * @param collectionClass
	 * @param resultClass
	 * @param <T>
	 * @param <C>
	 * @return
	 */
	public <T, C> RelayDatabase<T> on(Class<C> collectionClass, Class<T> resultClass) {
		return new RelayDatabase<T>(this, this.getDatabase()).withClass(resultClass).withCollectionName(this.entityName(collectionClass));
	}

	/**
	 * Given the initial collection name, get the mongo-database with a different result class,
	 * Comes in handy when you start your queries in a class and end up in a different class from the source
	 * Aggregations will use this often
	 * @param collectionName
	 * @param clazz
	 * @param <T>
	 * @return
	 */
	public <T> RelayDatabase<T> on(String collectionName, Class<T> clazz) {
		return new RelayDatabase<T>(this, this.getDatabase()).withClass(clazz).withCollectionName(collectionName);
	}

	public <T> List<Bson> acl (Class<T> clazz) {
		AccessControl control = this.accessControl(clazz);
		if (control == null) {
			return new ArrayList<>();
		}
		return control.applyAccessFilter();
	}

	/**
	 * The core of the Mongo Relay Library, does the mapping based on annotations at targeted value
	 * @param item
	 * @param <TResult>
	 * @return
	 */
	public <A extends Collection<? super TResult>, TResult, TDocument> A map(A item, Class<TDocument> clazz) {
		return this.map(item, this.discover(clazz));
	}

	/**
	 * The core of the Mongo Relay Library, does the mapping based on annotations at targeted value
	 * @param item
	 * @return
	 */
	public <TResult, TDocument> TResult map(TResult item, Class<TDocument> clazz) {
		return this.map(Arrays.asList(item), this.discover(clazz)).get(0);
	}

	private <T> AccessControl accessControl (Class<T> clazz) {
		AccessLevelType type = this.aclTypeOn(clazz);
		if (type == AccessLevelType.NONE) {
			return null;
		}
		if (user == null) {
			Logger.warn("ACL was defined, but user is missing {}", clazz.getSimpleName());
			return null;
		}
		return new AccessControl(user, type);
	}

	private <T> AccessLevelType aclTypeOn (Class<T> clazz) {
		AccessLevelType type = collectionClassAcl.get(clazz);
		if (type == null) {
			return this.aclTypeOn(this.entityName(clazz));
		}
		return type;
	}

	private AccessLevelType aclTypeOn (String collectionName) {
		if (Strings.isNullOrEmpty(collectionName)) {
			return AccessLevelType.NONE;
		}
		AccessLevelType type = collectionNameAcl.get(collectionName);
		return type == null ? AccessLevelType.NONE : type;
	}

	private <T> String entityName (Class<T> clazz) {
		Entity[] next = clazz.getAnnotationsByType(Entity.class);
		if (next.length == 0) {
			return clazz.getSimpleName().toLowerCase();
		}

		Entity entity = next[0];
		return entity.collection();
	}

	/**
	 * The core of the Mongo Relay Library, does the mapping based on annotations at targeted value
	 * @param item
	 * @param references
	 * @return mapped collection
	 */
	private <A extends Collection<? super TResult>, TResult> A map(A item, List<ReferencedField> references) {
		if (depth >= maxDepth) {
			return item;
		}
		if (references.size() == 0) {
			return item;
		}

		MongoRelay relay = new MongoRelay(this);
		// increase the depth by one, such that on next mapping it knows how deep it went
		relay.depth += 1;

		// Now that we have the reference mapping, lets form the filter which will get the references from Mongo and assign the values back to the target
		// That's apply the referencing
		for (ReferencedField reference: references) {
			reference.map(item, relay);
		}

		return item;
	}

	private <T> List<ReferencedField> discover (Class<T> clazz) {
		List<Field> referencedFields = FieldUtils.getFieldsListWithAnnotation(clazz, Reference.class);

		return referencedFields.stream().map(next -> {
			ReferencedField referencedField = new ReferencedField(clazz, next, new ArrayList<>());
			Reference annotation = next.getAnnotation(Reference.class);

			String[] from = annotation.from();
			String[] to = annotation.to();

			if (from.length != to.length) {
				return referencedField;
			}
			if (from.length == 0) {
				return referencedField;
			}
			for (int i = 0; i < from.length; i++) {
				String source = from[i];
				String target = to[i];
				referencedField.addReference(new FieldReference(source, target));
			}

			return referencedField;
		})
		.filter(next -> next.getReferencesList().size() > 0)
		.collect(Collectors.toList());
	}
}
