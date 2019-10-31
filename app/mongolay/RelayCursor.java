package mongolay;

import com.mongodb.Block;
import com.mongodb.Function;
import com.mongodb.ServerAddress;
import com.mongodb.ServerCursor;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Collation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.conversions.Bson;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

/**
 * Created by agonlohaj on 08 Aug, 2019
 */
@AllArgsConstructor
public class RelayCursor<TDocument, TResult> implements MongoCursor<TResult> {
	@Getter
	private RelayMongoIterable<TDocument, TResult> relayMongoIterable;
	@Getter
	private MongoCursor<TResult> cursor;

	@Override
	public void close() {
		cursor.close();
	}

	@Override
	public boolean hasNext() {
		return cursor.hasNext();
	}

	@Override
	public TResult next() {
		return relayMongoIterable.getMongoRelay().map(cursor.next(), relayMongoIterable.getDocumentClass());
	}

	@Override
	public TResult tryNext() {
		TResult result = cursor.tryNext();
		if (result == null) {
			return null;
		}
		return relayMongoIterable.getMongoRelay().map(result, relayMongoIterable.getDocumentClass());
	}

	@Override
	public ServerCursor getServerCursor() {
		return cursor.getServerCursor();
	}

	@Override
	public ServerAddress getServerAddress() {
		return cursor.getServerAddress();
	}
}
