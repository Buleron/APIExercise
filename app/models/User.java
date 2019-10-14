package models;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

//@Entity(value = "users", noClassnameStored = true)
public @Data
class User {
    private List<Roles> roles = new ArrayList<>();
    private String username;
    private String password;
//    @Id
    private String id;

//    public boolean auth(String username, String password) {
//        if (username.isEmpty() || password.isEmpty())
//            return false;
//        if (datastore().find(User.class).field("username").equal(username).field("password").equal(password).get() == null) {
//            return false;
//        }
//        return true;
//    }
}
