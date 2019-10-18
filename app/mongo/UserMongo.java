package mongo;

import com.google.common.base.Strings;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;
import com.typesafe.config.Config;
import models.collection.organisation.MongoConnection;
import models.collection.organisation.Organisation;
import play.api.inject.ApplicationLifecycle;

import java.util.ArrayList;
import java.util.List;

public final class UserMongo extends MongoDB {
    protected Organisation organisation;
    protected MongoClient mongo;
    private MongoDatabase database;

    public UserMongo(ApplicationLifecycle lifecycle, Config config) {
        super(lifecycle, config);
    }

    public UserMongo withOrganisation(Organisation organisation) {
        this.organisation = organisation;
        return this;
    }

//    @Override
    public MongoDatabase connect() {
        if (organisation == null) {
            return null;
        }

        MongoConnection connection = organisation.getMongo();
        String userDB = connection.getDatabase();
        String username = connection.getUsername();
        String password = connection.getPassword();

        String host = connection.getHost();
        String[] hosts = new String[0];
        if(!Strings.isNullOrEmpty(host)) {
            hosts = host.split(",");
        }
        String port = connection.getPort();

        String[] ports = new String[0];
        if(!Strings.isNullOrEmpty(port)) {
            ports = port.split(",");
        }

        // connection pooling
        MongoClientOptions options = MongoClientOptions.builder()
                .connectionsPerHost(MAX_CONNECTIONS_PER_HOST)
                .maxConnectionLifeTime(MAX_CONNECTION_LIFETIME)
                .maxConnectionIdleTime(MAX_CONNECTION_IDLE_TIME)
                .build();

        MongoCredential credential = null;
        if (!Strings.isNullOrEmpty(username)) {
            credential = MongoCredential.createCredential(username,
                    userDB,
                    password.toCharArray());
        }
        List<ServerAddress> addresses = new ArrayList<>();
        for (int i = 0; i < hosts.length; i++) {
            addresses.add(new ServerAddress(hosts[i], Integer.parseInt(ports[i])));
        }
        if (credential != null) {
            mongo = new MongoClient(addresses, credential, options);
        } else {
            mongo = new MongoClient(addresses, options);
        }
        return mongo.getDatabase(userDB);
    }

//    @Override
    public void disconnect() {
        if (mongo == null) {
            return;
        }
        mongo.close();
    }
}