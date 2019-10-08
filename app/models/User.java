package models;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.query.Query;

import static controllers.MongoConfig.datastore;

@Entity(value = "users", noClassnameStored = true)
public @Data
class User {
    private String username;
    private String password;
    @Id
    private int id;

    public boolean auth(String username, String password) {
        if (username.isEmpty() || password.isEmpty())
            return false;
        if (datastore().find(User.class).field("username").equal(username).field("password").equal(password).get() == null) {
            return false;
        }
        return true;
    }
}
