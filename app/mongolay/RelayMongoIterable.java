package mongolay;

import com.mongodb.Block;
import com.mongodb.Function;
import com.mongodb.Mongo;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import lombok.Getter;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by agonlohaj on 08 Aug, 2019
 */
public class RelayMongoIterable<TDocument, TResult> implements MongoIterable<TResult> {
	@Getter
	private RelayCollection<TDocument> relayCollection;
	@Getter
	private MongoIterable<TResult> mongoIterable;

	public MongoRelay getMongoRelay () {
		return relayCollection.getMongoRelay();
	}

	public Class<TDocument> getDocumentClass () {
		return relayCollection.getDocumentClass();
	}

	public RelayMongoIterable(RelayCollection<TDocument> collection, MongoIterable<TResult> mongoIterable) {
		this.relayCollection = collection;
		this.mongoIterable = mongoIterable;
	}

	@Override
	public MongoCursor<TResult> iterator() {
		return new RelayCursor<>(this, mongoIterable.iterator());
	}

	@Override
	public TResult first() {
		return getMongoRelay().map(mongoIterable.first(), relayCollection.getDocumentClass());
	}

	@Override
	public <U> MongoIterable<U> map(Function<TResult, U> mapper) {
		return new RelayMongoIterable(relayCollection, mongoIterable.map(mapper));
	}

	@Override
	public void forEach(Block<? super TResult> block) {
		mongoIterable.forEach(block);
	}

	@Override
	public <A extends Collection<? super TResult>> A into(A target) {
		mongoIterable.into(target);
		return getMongoRelay().map(target, relayCollection.getDocumentClass());
	}

	@Override
	public MongoIterable<TResult> batchSize(int batchSize) {
		return mongoIterable.batchSize(batchSize);
	}
}
