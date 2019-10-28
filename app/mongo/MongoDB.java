package mongo;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.typesafe.config.Config;
import lombok.Getter;
import models.collection.chat.ChatMessage;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.ClassModel;
import org.bson.codecs.pojo.PojoCodecProvider;
import play.api.inject.ApplicationLifecycle;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;
import static org.bson.codecs.pojo.Conventions.ANNOTATION_CONVENTION;


@Singleton
public class MongoDB {
    @Getter
    MongoDatabase database;
    MongoClient client;
//    private static Datastore datastore;

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
        // add class model if codec error;
        ClassModel<ChatMessage> chatMessage = ClassModel.builder(ChatMessage.class).enableDiscriminator(true).build();
        CodecProvider pojoCodecProvider =
                PojoCodecProvider.builder()
                        .conventions(Arrays.asList(ANNOTATION_CONVENTION))
                        .register("models")
                        // and also register class model if codec error;
                        .register(chatMessage)
                        .automatic(true).build();


        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(pojoCodecProvider));
        this.database = database.withCodecRegistry(pojoCodecRegistry);
    }
}
