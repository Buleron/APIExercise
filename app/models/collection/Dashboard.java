package models.collection;

import lombok.Data;
import mongolay.annotations.Entity;
import javax.validation.constraints.NotNull;

@Entity(collection = "Dashboard")
public @Data  class Dashboard extends CollectionModel {
    @NotNull
    private String name;
    private String description;
    private String parentId;
}
