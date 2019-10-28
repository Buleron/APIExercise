package models.collection;

import lombok.Data;
import models.Roles;
import mongolay.annotations.Entity;

import java.util.ArrayList;
import java.util.List;
public @Data class User  extends CollectionModel{
    private String username;
    private String password;
    private List<Roles> roles = new ArrayList<>();
}
