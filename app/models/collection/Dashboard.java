package models.collection;

import lombok.Data;

public @Data  class Dashboard extends CollectionModel {
    private String name;
    private String description;
    private String parentId;
}
