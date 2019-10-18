package models.collection.organisation;

import lombok.Data;

public @Data
class MongoConnection {
    private String database;
    private String username;
    private String password;
    private String host;
    private String port;
}
