package mongo;

import com.typesafe.config.Config;
import models.collection.User;
import models.collection.organisation.Organisation;
import play.api.inject.ApplicationLifecycle;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Agon on 10/6/2016.
 */
@Singleton
public class MongoFactory {

    private ApplicationLifecycle lifecycle;
    private Config configuration;
    private static Map<String, UserMongo> userMongoMap = new HashMap<>();
    private static GlobalMongo global;
//    private static Map<String, UserMongo> userMongoMap = new HashMap<>();
//    private static TestMongo test;


    @Inject
    public MongoFactory(ApplicationLifecycle lifecycle, Config config) {
        this.configuration = config;
        this.lifecycle = lifecycle;
    }

    public MongoDB mongoOn(MongoConnectionType collection) {
        return this.mongoOnOrganisation(collection, null);
    }

    public MongoDB mongoOn(MongoConnectionType collection, User user) {
        return this.mongoOnOrganisation(collection, user != null ? user.getOrganisation() : null);
    }

    public MongoDB mongoOn(MongoConnectionType collection, Organisation organisation) {
        return this.mongoOnOrganisation(collection, organisation);
    }


    public MongoDB mongoOnOrganisation(MongoConnectionType collection, Organisation organisation) {
        String mode = configuration.getString("mode");
//        if (mode.equalsIgnoreCase("test")) {
//            return this.getTestMongo();
//        }
        switch (collection) {
            case GLOBAL:
                return getGlobalMongo();
            default:
                if (organisation == null) {
                    throw new IllegalArgumentException("Organisation should be specified");
                }
                return getOrganisationMongo(organisation);
        }
    }

    private GlobalMongo getGlobalMongo() {
        if (global == null) {
            global = new GlobalMongo(lifecycle, configuration);
        }
        return global;
    }

//    public TestMongo getTestMongo() {
//        if (test == null) {
//            test = new TestMongo(lifecycle, configuration);
//        }
//        return test;
//    }

    private UserMongo getOrganisationMongo(Organisation organisation) {
        synchronized ((Integer) userMongoMap.size()) {
            if (!userMongoMap.containsKey(organisation.getId().toString())) {
                UserMongo mongo = new UserMongo(lifecycle, configuration).withOrganisation(organisation);
                userMongoMap.put(organisation.getId().toString(), mongo);
                return mongo;
            }
            return userMongoMap.get(organisation.getId().toString());
        }
    }
}
