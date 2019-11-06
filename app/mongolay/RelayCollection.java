package mongolay;

import com.mongodb.*;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.session.ClientSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import models.collection.CollectionModel;
import models.exceptions.RequestException;
import org.bson.Document;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import play.mvc.Http;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

/**
 * Created by agonlohaj on 08 Aug, 2019
 */
@AllArgsConstructor
public class RelayCollection<TDocument> implements MongoCollection<TDocument> {
	@Getter
	private RelayDatabase database;
	@Getter
	private MongoCollection<TDocument> collection;

	public MongoRelay getMongoRelay () {
		return database.getMongoRelay();
	}

	public MongoDatabase getMongoDatabase () {
		return database.getMongoDatabase();
	}

	/**
	 * Validates a value of a class, by checking Hibernate Validation
	 * And also Access control based on definitions
	 * @param value
	 * @return
	 * @throws RequestException
	 */
	@SuppressWarnings("unchecked")
	public <TDocument> TDocument validate (TDocument value) throws RequestException {
		return getMongoRelay().validate(value, (RelayCollection<TDocument>) this);
	}

	/**
	 * Validates a value of a class, by checking Hibernate Validation
	 * And also Access control based on definitions
	 * @param value
	 * @return
	 * @throws RequestException
	 */
	@SuppressWarnings("unchecked")
	public <TDocument> TDocument validate (TDocument value, Bson filter) throws RequestException {
		return getMongoRelay().validate(value, (RelayCollection<TDocument>) this, filter);
	}

	/**
	 * Returns if an object is valid
	 * And also Access control based on definitions
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <TDocument> boolean isValid (TDocument value) {
		return getMongoRelay().isValid(value, (RelayCollection<TDocument>) this);
	}

	/**
	 * Validates the value using an asynch promise
	 * @param value
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <TDocument> CompletableFuture<TDocument> validateAsynch (TDocument value, Executor context) {
		return getMongoRelay().validateAsynch(value, (RelayCollection<TDocument>) this, context);
	}

	@Override
	public MongoNamespace getNamespace() {
		return collection.getNamespace();
	}

	@Override
	public Class<TDocument> getDocumentClass() {
		return collection.getDocumentClass();
	}

	@Override
	public CodecRegistry getCodecRegistry() {
		return collection.getCodecRegistry();
	}

	@Override
	public ReadPreference getReadPreference() {
		return collection.getReadPreference();
	}

	@Override
	public WriteConcern getWriteConcern() {
		return collection.getWriteConcern();
	}

	@Override
	public ReadConcern getReadConcern() {
		return collection.getReadConcern();
	}

	@Override
	public <NewTDocument> MongoCollection<NewTDocument> withDocumentClass(Class<NewTDocument> clazz) {
		return collection.withDocumentClass(clazz);
	}

	@Override
	public MongoCollection<TDocument> withCodecRegistry(CodecRegistry codecRegistry) {
		return collection.withCodecRegistry(codecRegistry);
	}

	@Override
	public MongoCollection<TDocument> withReadPreference(ReadPreference readPreference) {
		return collection.withReadPreference(readPreference);
	}

	@Override
	public MongoCollection<TDocument> withWriteConcern(WriteConcern writeConcern) {
		return collection.withWriteConcern(writeConcern);
	}

	@Override
	public MongoCollection<TDocument> withReadConcern(ReadConcern readConcern) {
		return collection.withReadConcern(readConcern);
	}

	@Override
	public long count() {
		return collection.count();
	}

	@Override
	public long count(Bson filter) {
		return collection.count(filter);
	}

	@Override
	public long count(Bson filter, CountOptions options) {
		return collection.count(filter, options);
	}

	@Override
	public long count(ClientSession clientSession) {
		return collection.count(clientSession);
	}

	@Override
	public long count(ClientSession clientSession, Bson filter) {
		return collection.count(clientSession, filter);
	}

	@Override
	public long count(ClientSession clientSession, Bson filter, CountOptions options) {
		return collection.count(clientSession, filter, options);
	}

	@Override
	public <TResult> DistinctIterable<TResult> distinct(String fieldName, Class<TResult> tResultClass) {
		return collection.distinct(fieldName, tResultClass);
	}

	@Override
	public <TResult> DistinctIterable<TResult> distinct(String fieldName, Bson filter, Class<TResult> tResultClass) {
		return collection.distinct(fieldName, filter, tResultClass);
	}

	@Override
	public <TResult> DistinctIterable<TResult> distinct(ClientSession clientSession, String fieldName, Class<TResult> tResultClass) {
		return collection.distinct(clientSession, fieldName, tResultClass);
	}

	@Override
	public <TResult> DistinctIterable<TResult> distinct(ClientSession clientSession, String fieldName, Bson filter, Class<TResult> tResultClass) {
		return collection.distinct(clientSession, fieldName, filter, tResultClass);
	}

	@Override
	public RelayFindIterable find() {
		return new RelayFindIterable<>(this, collection.find());
	}

	@Override
	public <TResult> RelayFindIterable<TDocument, TResult> find(Class<TResult> tResultClass) {
		return new RelayFindIterable<>(this, collection.find(tResultClass));
	}

	@Override
	public RelayFindIterable find(Bson filter) {
		return this.find().filter(filter);
	}

	@Override
	public <TResult> RelayFindIterable<TDocument, TResult> find(Bson filter, Class<TResult> tResultClass) {
		return this.find(tResultClass).filter(filter);
	}

	@Override
	public RelayFindIterable find(ClientSession clientSession) {
		return new RelayFindIterable<>(this, collection.find(clientSession));
	}

	@Override
	public <TResult> RelayFindIterable find(ClientSession clientSession, Class<TResult> tResultClass) {
		return new RelayFindIterable<>(this, collection.find(clientSession, tResultClass));
	}

	@Override
	public RelayFindIterable find(ClientSession clientSession, Bson filter) {
		return this.find(clientSession).filter(filter);
	}

	@Override
	public <TResult> RelayFindIterable find(ClientSession clientSession, Bson filter, Class<TResult> tResultClass) {
		return this.find(clientSession, tResultClass).filter(filter);
	}

	@Override
	public AggregateIterable<TDocument> aggregate(List<? extends Bson> pipeline) {
		return new RelayAggregation<>(this, collection.aggregate(pipeline));
	}

	@Override
	public <TResult> AggregateIterable<TResult> aggregate(List<? extends Bson> pipeline, Class<TResult> tResultClass) {
		return new RelayAggregation<>(this, collection.aggregate(pipeline, tResultClass));
	}

	@Override
	public AggregateIterable<TDocument> aggregate(ClientSession clientSession, List<? extends Bson> pipeline) {
		return new RelayAggregation<>(this, collection.aggregate(clientSession, pipeline));
	}

	@Override
	public <TResult> AggregateIterable<TResult> aggregate(ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
		return new RelayAggregation<>(this, collection.aggregate(clientSession, pipeline, tResultClass));
	}

	@Override
	public ChangeStreamIterable<TDocument> watch() {
		return collection.watch();
	}

	@Override
	public <TResult> ChangeStreamIterable<TResult> watch(Class<TResult> tResultClass) {
		return collection.watch(tResultClass);
	}

	@Override
	public ChangeStreamIterable<TDocument> watch(List<? extends Bson> pipeline) {
		return collection.watch(pipeline);
	}

	@Override
	public <TResult> ChangeStreamIterable<TResult> watch(List<? extends Bson> pipeline, Class<TResult> tResultClass) {
		return collection.watch(pipeline, tResultClass);
	}

	@Override
	public ChangeStreamIterable<TDocument> watch(ClientSession clientSession) {
		return collection.watch(clientSession);
	}

	@Override
	public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, Class<TResult> tResultClass) {
		return collection.watch(clientSession, tResultClass);
	}

	@Override
	public ChangeStreamIterable<TDocument> watch(ClientSession clientSession, List<? extends Bson> pipeline) {
		return collection.watch(clientSession, pipeline);
	}

	@Override
	public <TResult> ChangeStreamIterable<TResult> watch(ClientSession clientSession, List<? extends Bson> pipeline, Class<TResult> tResultClass) {
		return collection.watch(clientSession, pipeline, tResultClass);
	}

	@Override
	public MapReduceIterable<TDocument> mapReduce(String mapFunction, String reduceFunction) {
		return collection.mapReduce(mapFunction, reduceFunction);
	}

	@Override
	public <TResult> MapReduceIterable<TResult> mapReduce(String mapFunction, String reduceFunction, Class<TResult> tResultClass) {
		return collection.mapReduce(mapFunction, reduceFunction, tResultClass);
	}

	@Override
	public MapReduceIterable<TDocument> mapReduce(ClientSession clientSession, String mapFunction, String reduceFunction) {
		return collection.mapReduce(clientSession, mapFunction, reduceFunction);
	}

	@Override
	public <TResult> MapReduceIterable<TResult> mapReduce(ClientSession clientSession, String mapFunction, String reduceFunction, Class<TResult> tResultClass) {
		return collection.mapReduce(clientSession, mapFunction, reduceFunction, tResultClass);
	}

	@Override
	public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends TDocument>> requests) {
		return collection.bulkWrite(requests);
	}

	@Override
	public BulkWriteResult bulkWrite(List<? extends WriteModel<? extends TDocument>> requests, BulkWriteOptions options) {
		return collection.bulkWrite(requests, options);
	}

	@Override
	public BulkWriteResult bulkWrite(ClientSession clientSession, List<? extends WriteModel<? extends TDocument>> requests) {
		return collection.bulkWrite(clientSession, requests);
	}

	@Override
	public BulkWriteResult bulkWrite(ClientSession clientSession, List<? extends WriteModel<? extends TDocument>> requests, BulkWriteOptions options) {
		return collection.bulkWrite(clientSession, requests, options);
	}

	@Override
	public void insertOne(TDocument tDocument) {
		collection.insertOne(tDocument);
	}

	@Override
	public void insertOne(TDocument tDocument, InsertOneOptions options) {
		collection.insertOne(tDocument, options);
	}

	@Override
	public void insertOne(ClientSession clientSession, TDocument tDocument) {
		collection.insertOne(clientSession, tDocument);
	}

	@Override
	public void insertOne(ClientSession clientSession, TDocument tDocument, InsertOneOptions options) {
		collection.insertOne(clientSession, tDocument, options);
	}

	@Override
	public void insertMany(List<? extends TDocument> tDocuments) {
		collection.insertMany(tDocuments);
	}

	@Override
	public void insertMany(List<? extends TDocument> tDocuments, InsertManyOptions options) {
		collection.insertMany(tDocuments, options);
	}

	@Override
	public void insertMany(ClientSession clientSession, List<? extends TDocument> tDocuments) {
		collection.insertMany(clientSession, tDocuments);
	}

	@Override
	public void insertMany(ClientSession clientSession, List<? extends TDocument> tDocuments, InsertManyOptions options) {
		collection.insertMany(clientSession, tDocuments, options);
	}

	@Override
	public DeleteResult deleteOne(Bson filter) {
		return collection.deleteOne(filter);
	}

	@Override
	public DeleteResult deleteOne(Bson filter, DeleteOptions options) {
		return collection.deleteOne(filter, options);
	}

	@Override
	public DeleteResult deleteOne(ClientSession clientSession, Bson filter) {
		return collection.deleteOne(clientSession, filter);
	}

	@Override
	public DeleteResult deleteOne(ClientSession clientSession, Bson filter, DeleteOptions options) {
		return collection.deleteOne(clientSession, filter, options);
	}

	@Override
	public DeleteResult deleteMany(Bson filter) {
		return collection.deleteMany(filter);
	}

	@Override
	public DeleteResult deleteMany(Bson filter, DeleteOptions options) {
		return collection.deleteMany(filter, options);
	}

	@Override
	public DeleteResult deleteMany(ClientSession clientSession, Bson filter) {
		return collection.deleteMany(clientSession, filter);
	}

	@Override
	public DeleteResult deleteMany(ClientSession clientSession, Bson filter, DeleteOptions options) {
		return collection.deleteMany(clientSession, filter, options);
	}

	@Override
	public UpdateResult replaceOne(Bson filter, TDocument replacement) {
		return collection.replaceOne(filter, replacement);
	}

	@Override
	public UpdateResult replaceOne(Bson filter, TDocument replacement, UpdateOptions updateOptions) {
		return collection.replaceOne(filter, replacement, updateOptions);
	}

	@Override
	public UpdateResult replaceOne(ClientSession clientSession, Bson filter, TDocument replacement) {
		return collection.replaceOne(clientSession, filter, replacement);
	}

	@Override
	public UpdateResult replaceOne(ClientSession clientSession, Bson filter, TDocument replacement, UpdateOptions updateOptions) {
		return collection.replaceOne(clientSession, filter, replacement, updateOptions);
	}

	@Override
	public UpdateResult updateOne(Bson filter, Bson update) {
		return collection.updateOne(filter, update);
	}

	@Override
	public UpdateResult updateOne(Bson filter, Bson update, UpdateOptions updateOptions) {
		return collection.updateOne(filter, update, updateOptions);
	}

	@Override
	public UpdateResult updateOne(ClientSession clientSession, Bson filter, Bson update) {
		return collection.updateOne(clientSession, filter, update);
	}

	@Override
	public UpdateResult updateOne(ClientSession clientSession, Bson filter, Bson update, UpdateOptions updateOptions) {
		return collection.updateOne(clientSession, filter, update, updateOptions);
	}

	@Override
	public UpdateResult updateMany(Bson filter, Bson update) {
		return collection.updateMany(filter, update);
	}

	@Override
	public UpdateResult updateMany(Bson filter, Bson update, UpdateOptions updateOptions) {
		return collection.updateMany(filter, update, updateOptions);
	}

	@Override
	public UpdateResult updateMany(ClientSession clientSession, Bson filter, Bson update) {
		return collection.updateMany(clientSession, filter, update);
	}

	@Override
	public UpdateResult updateMany(ClientSession clientSession, Bson filter, Bson update, UpdateOptions updateOptions) {
		return collection.updateMany(clientSession, filter, update, updateOptions);
	}

	@Override
	public TDocument findOneAndDelete(Bson filter) {
		return collection.findOneAndDelete(filter);
	}

	@Override
	public TDocument findOneAndDelete(Bson filter, FindOneAndDeleteOptions options) {
		return collection.findOneAndDelete(filter, options);
	}

	@Override
	public TDocument findOneAndDelete(ClientSession clientSession, Bson filter) {
		return collection.findOneAndDelete(clientSession, filter);
	}

	@Override
	public TDocument findOneAndDelete(ClientSession clientSession, Bson filter, FindOneAndDeleteOptions options) {
		return collection.findOneAndDelete(clientSession, filter, options);
	}

	@Override
	public TDocument findOneAndReplace(Bson filter, TDocument replacement) {
		return collection.findOneAndReplace(filter, replacement);
	}

	@Override
	public TDocument findOneAndReplace(Bson filter, TDocument replacement, FindOneAndReplaceOptions options) {
		return collection.findOneAndReplace(filter, replacement, options);
	}

	@Override
	public TDocument findOneAndReplace(ClientSession clientSession, Bson filter, TDocument replacement) {
		return collection.findOneAndReplace(clientSession, filter, replacement);
	}

	@Override
	public TDocument findOneAndReplace(ClientSession clientSession, Bson filter, TDocument replacement, FindOneAndReplaceOptions options) {
		return collection.findOneAndReplace(clientSession, filter, replacement, options);
	}

	@Override
	public TDocument findOneAndUpdate(Bson filter, Bson update) {
		return collection.findOneAndUpdate(filter, update);
	}

	@Override
	public TDocument findOneAndUpdate(Bson filter, Bson update, FindOneAndUpdateOptions options) {
		return collection.findOneAndUpdate(filter, update, options);
	}

	@Override
	public TDocument findOneAndUpdate(ClientSession clientSession, Bson filter, Bson update) {
		return collection.findOneAndUpdate(clientSession, filter, update);
	}

	@Override
	public TDocument findOneAndUpdate(ClientSession clientSession, Bson filter, Bson update, FindOneAndUpdateOptions options) {
		return collection.findOneAndUpdate(clientSession, filter, update, options);
	}

	@Override
	public void drop() {
		collection.drop();
	}

	@Override
	public void drop(ClientSession clientSession) {
		collection.drop(clientSession);
	}

	@Override
	public String createIndex(Bson keys) {
		return collection.createIndex(keys);
	}

	@Override
	public String createIndex(Bson keys, IndexOptions indexOptions) {
		return collection.createIndex(keys, indexOptions);
	}

	@Override
	public String createIndex(ClientSession clientSession, Bson keys) {
		return collection.createIndex(clientSession, keys);
	}

	@Override
	public String createIndex(ClientSession clientSession, Bson keys, IndexOptions indexOptions) {
		return collection.createIndex(clientSession, keys, indexOptions);
	}

	@Override
	public List<String> createIndexes(List<IndexModel> indexes) {
		return collection.createIndexes(indexes);
	}

	@Override
	public List<String> createIndexes(List<IndexModel> indexes, CreateIndexOptions createIndexOptions) {
		return collection.createIndexes(indexes, createIndexOptions);
	}

	@Override
	public List<String> createIndexes(ClientSession clientSession, List<IndexModel> indexes) {
		return collection.createIndexes(clientSession, indexes);
	}

	@Override
	public List<String> createIndexes(ClientSession clientSession, List<IndexModel> indexes, CreateIndexOptions createIndexOptions) {
		return collection.createIndexes(clientSession, indexes, createIndexOptions);
	}

	@Override
	public ListIndexesIterable<Document> listIndexes() {
		return collection.listIndexes();
	}

	@Override
	public <TResult> ListIndexesIterable<TResult> listIndexes(Class<TResult> tResultClass) {
		return collection.listIndexes(tResultClass);
	}

	@Override
	public ListIndexesIterable<Document> listIndexes(ClientSession clientSession) {
		return collection.listIndexes(clientSession);
	}

	@Override
	public <TResult> ListIndexesIterable<TResult> listIndexes(ClientSession clientSession, Class<TResult> tResultClass) {
		return collection.listIndexes(clientSession, tResultClass);
	}

	@Override
	public void dropIndex(String indexName) {
		collection.dropIndex(indexName);
	}

	@Override
	public void dropIndex(String indexName, DropIndexOptions dropIndexOptions) {
		collection.dropIndex(indexName, dropIndexOptions);
	}

	@Override
	public void dropIndex(Bson keys) {
		collection.dropIndex(keys);
	}

	@Override
	public void dropIndex(Bson keys, DropIndexOptions dropIndexOptions) {
		collection.dropIndex(keys, dropIndexOptions);
	}

	@Override
	public void dropIndex(ClientSession clientSession, String indexName) {
		collection.dropIndex(clientSession, indexName);
	}

	@Override
	public void dropIndex(ClientSession clientSession, Bson keys) {
		collection.dropIndex(clientSession, keys);
	}

	@Override
	public void dropIndex(ClientSession clientSession, String indexName, DropIndexOptions dropIndexOptions) {
		collection.dropIndex(clientSession, indexName, dropIndexOptions);
	}

	@Override
	public void dropIndex(ClientSession clientSession, Bson keys, DropIndexOptions dropIndexOptions) {
		collection.dropIndex(clientSession, keys, dropIndexOptions);
	}

	@Override
	public void dropIndexes() {
		collection.dropIndexes();
	}

	@Override
	public void dropIndexes(ClientSession clientSession) {
		collection.dropIndexes(clientSession);
	}

	@Override
	public void dropIndexes(DropIndexOptions dropIndexOptions) {
		collection.dropIndexes(dropIndexOptions);
	}

	@Override
	public void dropIndexes(ClientSession clientSession, DropIndexOptions dropIndexOptions) {
		collection.dropIndexes(clientSession, dropIndexOptions);
	}

	@Override
	public void renameCollection(MongoNamespace newCollectionNamespace) {
		collection.renameCollection(newCollectionNamespace);
	}

	@Override
	public void renameCollection(MongoNamespace newCollectionNamespace, RenameCollectionOptions renameCollectionOptions) {
		collection.renameCollection(newCollectionNamespace, renameCollectionOptions);
	}

	@Override
	public void renameCollection(ClientSession clientSession, MongoNamespace newCollectionNamespace) {
		collection.renameCollection(clientSession, newCollectionNamespace);
	}

	@Override
	public void renameCollection(ClientSession clientSession, MongoNamespace newCollectionNamespace, RenameCollectionOptions renameCollectionOptions) {
		collection.renameCollection(clientSession, newCollectionNamespace, renameCollectionOptions);
	}

	/**
	 * Inserts or update items based on defined access control
	 *
	 * @param item
	 * @return
	 */
	public CompletableFuture<TDocument> insertOrUpdateAsynch(TDocument item, Executor executor) {
		return this.insertOrUpdateAsynch(item, null, executor);
	}

	/**
	 * Inserts or update items based on defined access control
	 *
	 * @param item
	 * @return
	 */
	public CompletableFuture<TDocument> insertOrUpdateAsynch(TDocument item, Bson filter, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return this.insertOrUpdate(item, filter);
			} catch (RequestException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		}, executor);
	}

	/**
	 * Inserts or update items based on defined access control
	 *
	 * @param item
	 * @return
	 */
	public TDocument insertOrUpdate(TDocument item) throws RequestException {
		return this.insertOrUpdate(item, null);
	}

	/**
	 * Inserts or update items based on defined access control
	 *
	 * @param item
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public TDocument insertOrUpdate(TDocument item, Bson filter) throws RequestException {
		this.validate(item, filter);

		if (item instanceof Document) {
			return (TDocument) this.insertOrUpdate((Document) item);
		}

		if (item instanceof CollectionModel) {
			return (TDocument) this.insertOrUpdate((CollectionModel) item);
		}

		throw new RequestException(Http.Status.BAD_REQUEST, "invalid_parameters");
	}

	@SuppressWarnings("unchecked")
	private Document insertOrUpdate(Document item) throws RequestException {
		if (item.getObjectId("_id") != null) {
			item.append("updatedAt", new Date().getTime());
			UpdateResult result = collection.replaceOne(new BasicDBObject("_id", item.getObjectId("_id")), (TDocument) item);
			if (result.wasAcknowledged() && result.getModifiedCount() > 0) {
				return item;
			}
			throw new RequestException(Http.Status.NOT_FOUND, "not_found");
		}
		collection.insertOne((TDocument) item);
		return item;
	}

	@SuppressWarnings("unchecked")
	private CollectionModel insertOrUpdate(CollectionModel item) throws RequestException {
		if (item.getId() != null) {
			item.setUpdatedAt(new Date().getTime());
			UpdateResult result = this.replaceOne(new BasicDBObject("_id", item.getId()), (TDocument) item);
			if (result.wasAcknowledged() && result.getModifiedCount() > 0) {
				return item;
			}
			throw new RequestException(Http.Status.NOT_FOUND, "not_found");
		}
		ObjectId id = new ObjectId();
		item.setId(id);
		this.insertOne((TDocument) item);
		return item;
	}



	/**
	 * Inserts or update items based on defined access control
	 *
	 * @param item
	 * @return
	 */
	public CompletableFuture<TDocument> deleteAsynch(TDocument item, Executor executor) {
		return this.deleteAsynch(item, null, executor);
	}

	/**
	 * Inserts or update items based on defined access control
	 *
	 * @param item
	 * @return
	 */
	public CompletableFuture<TDocument> deleteAsynch(TDocument item, Bson filter, Executor executor) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return this.deleteItem(item, filter);
			} catch (RequestException e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		}, executor);
	}

	/**
	 * Deletes an item based on access control
	 *
	 * @param item
	 * @return
	 */
	public <T> T deleteItem(T item) throws RequestException {
		return this.deleteItem(item, null);
	}

	/**
	 * Deletes an item based on access control
	 * @param item
	 * @return
	 */
	public <T> T deleteItem(T item, Bson filter) throws RequestException {
		this.validate(item, filter);

		ObjectId id = null;
		if (item instanceof Document) {
			id = ((Document) item).getObjectId("_id");
		}

		if (item instanceof CollectionModel) {
			id = ((CollectionModel) item).getId();
		}
		DeleteResult result = this.deleteOne(new BasicDBObject("_id", id));
		if (result.wasAcknowledged() && result.getDeletedCount() > 0) {
			return item;
		}
		// if no objcetid return bad request else not found
		if (id == null) {
			throw new RequestException(Http.Status.BAD_REQUEST, "invalid_parameters");
		}
		throw new RequestException(Http.Status.NOT_FOUND, "not_found");
	}
}
