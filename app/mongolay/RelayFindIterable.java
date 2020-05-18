package mongolay;

import com.mongodb.CursorType;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Collation;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import org.bson.conversions.Bson;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * Created by agonlohaj on 08 Aug, 2019
 */
public class RelayFindIterable<TDocument, TResult> extends RelayMongoIterable<TDocument, TResult> implements FindIterable<TResult>  {
	@Getter
	private RelayCollection<TDocument> relayCollection;
	@Getter
	private FindIterable<TResult> findIterable;


	public MongoRelay getMongoRelay () {
		return relayCollection.getMongoRelay();
	}

	public MongoDatabase getMongoDatabase () {
		return relayCollection.getMongoDatabase();
	}

	public RelayFindIterable(RelayCollection<TDocument> collection, FindIterable<TResult> findIterable) {
		super(collection, findIterable);
		this.relayCollection = collection;
		this.findIterable = findIterable;

		this.filter(null);
	}

	@Override
	public RelayFindIterable<TDocument, TResult> filter(Bson filter) {
		List<Bson> filters = getMongoRelay().acl(relayCollection.getDocumentClass());
		if (filter != null) {
			filters.add(filter);
		}
		if (filters.size() == 0) {
			return this;
		}
		if (filters.size() == 1) {
			findIterable = findIterable.filter(filters.get(0));
			return this;
		}
		// check if ACL is turned on, and whether the Mongo Relay has something to say abut this filtering!
		findIterable = findIterable.filter(Filters.and(filters));
		return this;
	}

	public CompletableFuture<TResult> firstAsych(Executor context) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return super.first();
			} catch (Exception e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		}, context);
	}

	public <A extends Collection<? super TResult>> CompletableFuture<A> intoAsynch(A target, Executor context) {
		return CompletableFuture.supplyAsync(() -> {
			try {
				return super.into(target);
			} catch (Exception e) {
				e.printStackTrace();
				throw new CompletionException(e);
			}
		}, context);
	}

	@Override
	public RelayFindIterable<TDocument, TResult> limit(int limit) {
		this.findIterable = findIterable.limit(limit);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> skip(int skip) {
		this.findIterable = findIterable.skip(skip);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> maxTime(long maxTime, TimeUnit timeUnit) {
		this.findIterable = findIterable.maxTime(maxTime, timeUnit);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> maxAwaitTime(long maxAwaitTime, TimeUnit timeUnit) {
		this.findIterable = findIterable.maxAwaitTime(maxAwaitTime, timeUnit);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> modifiers(Bson modifiers) {
		this.findIterable = findIterable.modifiers(modifiers);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> projection(Bson projection) {
		this.findIterable = findIterable.projection(projection);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> sort(Bson sort) {
		this.findIterable = findIterable.sort(sort);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> noCursorTimeout(boolean noCursorTimeout) {
		this.findIterable = findIterable.noCursorTimeout(noCursorTimeout);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> oplogReplay(boolean oplogReplay) {
		this.findIterable = findIterable.oplogReplay(oplogReplay);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> partial(boolean partial) {
		this.findIterable = findIterable.partial(partial);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> cursorType(CursorType cursorType) {
		this.findIterable = findIterable.cursorType(cursorType);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> collation(Collation collation) {
		this.findIterable = findIterable.collation(collation);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> comment(String comment) {
		this.findIterable = findIterable.comment(comment);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> hint(Bson hint) {
		this.findIterable = findIterable.hint(hint);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> max(Bson max) {
		this.findIterable = findIterable.max(max);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> min(Bson min) {
		this.findIterable = findIterable.min(min);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> maxScan(long maxScan) {
		this.findIterable = findIterable.maxScan(maxScan);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> returnKey(boolean returnKey) {
		this.findIterable = findIterable.returnKey(returnKey);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> showRecordId(boolean showRecordId) {
		this.findIterable = findIterable.showRecordId(showRecordId);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> snapshot(boolean snapshot) {
		this.findIterable = findIterable.snapshot(snapshot);
		return this;
	}

	@Override
	public RelayFindIterable<TDocument, TResult> batchSize(int batchSize) {
		this.findIterable = findIterable.batchSize(batchSize);
		return this;
	}
}
