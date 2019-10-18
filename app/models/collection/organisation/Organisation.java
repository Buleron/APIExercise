package models.collection.organisation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;
import models.collection.CollectionModel;

@ToString(callSuper = true)
public @Data
class Organisation  extends CollectionModel {
    private String name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private MongoConnection mongo;
}
