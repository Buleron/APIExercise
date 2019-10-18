package mongo;

import akka.cluster.Cluster;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import lombok.Data;
import lombok.Getter;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;
import play.api.inject.ApplicationLifecycle;
import scala.Function0;
import scala.concurrent.Future;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.bson.codecs.pojo.Conventions.ANNOTATION_CONVENTION;


@Singleton
public class MongoDB {
    @Getter
    private MongoDatabase database;
    private MongoClient client;
//    private static Datastore datastore;
    protected static final int MAX_CONNECTION_IDLE_TIME = 5 * 60 * 1000;
    protected static final int MAX_CONNECTION_LIFETIME = 10 * 60 * 1000;
    protected static final int MAX_CONNECTIONS_PER_HOST = 5;
    public static final String DB_ACCESS_CONTROL = "access";
    public static final String DB_COLUMNS = "columns";

    @Inject
    public MongoDB(ApplicationLifecycle lifecycle, Config config) {
        lifecycle.addStopHook(() -> {
            if (client != null) {
                client.close();
                client = null;
            }
            database = null;
            return CompletableFuture.completedFuture(null);
        });

        String host = config.getString("mongodb.host");
        int port = config.getInt("mongodb.port");

        this.client = new MongoClient(host, port);
        String db = config.getString("mongodb.database");

        com.mongodb.client.MongoDatabase database = client.getDatabase(db);
        CodecProvider pojoCodecProvider =
                PojoCodecProvider.builder()
                        .conventions(Arrays.asList(ANNOTATION_CONVENTION))
                        .register("models")
                        .automatic(true).build();


        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        this.database = database.withCodecRegistry(pojoCodecRegistry);
    }
    public CompletableFuture<MongoDatabase> getMongoDatabaseAsync() {
        CompletableFuture<MongoDatabase> promise = new CompletableFuture<>();
        try {
            MongoDatabase connection = this.getDatabase();
            promise.complete(connection);
        } catch (NullPointerException ex) {
            promise.completeExceptionally(ex);
        }
        return promise;
    }
}
