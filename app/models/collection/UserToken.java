package models.collection;

import Interfaces.IContent;
import lombok.Data;
import mongolay.annotations.Reference;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.ArrayList;
import java.util.List;


public @Data class UserToken extends CollectionModel {
    private String token;
    private long expiresAt;
    private String client;

    @BsonIgnore
    public String getUserId() {
        // TODO: implement this
        // extract userId out of token
        return token;
    }
}
