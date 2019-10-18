package mongo;

import com.google.common.base.Strings;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.typesafe.config.Config;
import play.api.inject.ApplicationLifecycle;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Agon on 10/6/2016.
 */
public final class GlobalMongo extends MongoDB {

    // Global Collection Names
    public static final String DB_PASSWORD_RESETS = "password_resets";
    public static final String DB_FEEDBACK = "feedback";
    public static final String DB_LOGGER = "logger";
    public static final String DB_INVITES = "invites";
    public static final String DB_ORGANISATIONS = "organisations";
    public static final String DB_TRACKER = "tracker";
    public static final String DB_USERS = "users";
    public static final String DB_TOKENS = "tokens";
    public static final String DB_LOGIN_LOGGER = "login_logger";
    private static Integer opened = 0;
    protected static final int MAX_CONNECTION_IDLE_TIME = 5 * 60 * 1000;
    protected static final int MAX_CONNECTION_LIFETIME = 10 * 60 * 1000;
    protected static final int MAX_CONNECTIONS_PER_HOST = 5;

    public GlobalMongo(ApplicationLifecycle lifecycle, Config config) {
        super(lifecycle, config);
    }

//    @Override
//    protected MongoDatabase connect() {
////        String host = config.getString("mongo_host");
////        String[] hosts = new String[0];
////        if(!Strings.isNullOrEmpty(host)) {
////            hosts = host.split(",");
////        }
////        String port = config.getString("mongo_port");
////
////        String[] ports = new String[0];
////        if(!Strings.isNullOrEmpty(port)) {
////            ports = port.split(",");
////        }
////        String userDB = config.getString("mongo_database");
////        String username = config.getString("mongo_user");
////        String password = config.getString("mongo_password");
////
////        String userAuthenticationDatabase = config.getString("mongo_auth_database");
////
////        // connection pooling
////        MongoClientOptions options = MongoClientOptions.builder()
////                .connectionsPerHost(MAX_CONNECTIONS_PER_HOST)
////                .maxConnectionLifeTime(MAX_CONNECTION_LIFETIME)
////                .maxConnectionIdleTime(MAX_CONNECTION_IDLE_TIME)
////                .build();
////
////        MongoCredential credential = null;
////        if (!Strings.isNullOrEmpty(username)) {
////            credential = MongoCredential.createCredential(username,
////                    !Strings.isNullOrEmpty(userAuthenticationDatabase) ? userAuthenticationDatabase : userDB,
////                    password.toCharArray());
////        }
////        List<ServerAddress> addresses = new ArrayList<>();
////        for (int i = 0; i < hosts.length; i++) {
////            addresses.add(new ServerAddress(hosts[i], Integer.parseInt(ports[i])));
////        }
////        if (credential != null) {
////            mongo = new MongoClient(addresses, credential, options);
////        } else {
////            mongo = new MongoClient(addresses, options);
////        }
////        return mongo.getDatabase(userDB);
////    }
////
////    @Override
////    public void disconnect() {
////        if (mongo == null) {
////            return;
////        }
////        mongo.close();
////    }
}