package models.collection;

import lombok.Data;
import models.Roles;
import mongolay.annotations.Entity;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
public @Data class User  extends CollectionModel {
    private String username;
    private String password;
    @BsonProperty("id")
    private List<ObjectId> roles = new ArrayList<>();
}
