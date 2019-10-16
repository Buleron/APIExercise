package models.collection;

import lombok.Data;
import models.Roles;

import java.util.ArrayList;
import java.util.List;

public @Data
class User {
    private String username;
    private String password;
    private List<Roles> roles = new ArrayList<>();

}
