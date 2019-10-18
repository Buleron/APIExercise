package models.collection;

import lombok.Data;
import models.Roles;
import models.UserTypeModel;
import models.collection.organisation.Organisation;
import mongolay.annotations.Entity;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.ArrayList;
import java.util.List;
@Entity(collection = "users")
public @Data class User  extends CollectionModel{
    private String username;
    private String password;
    private List<Roles> roles = new ArrayList<Roles>();
    private Boolean totpEnabled = false;
    @BsonIgnore
    private Organisation organisation;
    private String organisationId;
    private UserTypeModel type;
}
