package models.collection;

import lombok.Data;
import lombok.ToString;
import models.Roles;

import java.util.ArrayList;
import java.util.List;

@ToString
public @Data class Role extends CollectionModel{
    private String name;
}
