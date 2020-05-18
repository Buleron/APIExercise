package data;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import models.exceptions.RequestException;
import mongolay.MongoRelay;
import mongolay.RelayDatabase;
import org.bson.types.ObjectId;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RequiredArgsConstructor
public class DataAccess<T> {
    @NotEmpty
    private final MongoDatabase database;
    @NotEmpty
    private final Class<T> clazz;

//    @RequiredArgsConstructor
//    public DataAccess (MongoDatabase database) {
//        this.database = database;
//    }

    @Getter
    private MongoRelay mongoRelay;


    public DataAccess<T> withMongoRelay (MongoRelay relay) {
        this.mongoRelay = relay;
        return this;
    }

    public RelayDatabase<T> getRelay () {
        if (this.mongoRelay == null) {
            return new MongoRelay(database).on(clazz);
        }
        return mongoRelay.on(clazz);
    }

    public List<T> all() {
        return getRelay().getCollection().find(clazz).into(new ArrayList<>());
    }

    public CompletableFuture<List<T>> all(Executor context) {
        return getRelay().getCollection().find(clazz).intoAsynch(new ArrayList<>(), context);
    }

    public T byId(ObjectId id) {
        return getRelay().getCollection().find(Filters.eq("_id", id), clazz).first();
    }

    public CompletableFuture<T> byIdAsync(ObjectId id, Executor context) {
        return getRelay().getCollection().find(Filters.eq("_id", id), clazz).firstAsych(context);
    }

    public T insert (T item) throws RequestException {
        return getRelay().getCollection().insertOrUpdate(item);
    }

    public CompletableFuture<T> insert (T item, Executor context) {
        return getRelay().getCollection().insertOrUpdateAsynch(item, context);
    }

    public T update (T item) throws RequestException {
        return getRelay().getCollection().insertOrUpdate(item);
    }

    public CompletableFuture<T> update (T item, Executor context) {
        return getRelay().getCollection().insertOrUpdateAsynch(item, context);
    }

    public T delete (T item) throws RequestException {
        return getRelay().getCollection().deleteItem(item);
    }

    public CompletableFuture<T> delete (T item, Executor context) {
        return getRelay().getCollection().deleteAsynch(item, context);
    }

    public CompletableFuture<T> deleteAsync (T item, Executor context) {
        return getRelay().getCollection().deleteAsynch(item, context);
    }
}
