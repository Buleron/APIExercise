package models.collection;

import lombok.Data;
import mongolay.annotations.Entity;

@Entity(collection = "Dashboard")
public @Data  class Dashboard extends CollectionModel {
    private String name;
    private String description;
    private String parentId;
}
