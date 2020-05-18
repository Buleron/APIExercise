package services;

import models.Roles;
import models.collection.User;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccessService {

    public List<String> GetAccesses(User AuthUser) {
        List<String> access = new ArrayList<>();
        List<String> roles = AuthUser.getRoles().stream().map(ObjectId::toString).collect(Collectors.toList());
        System.out.println(roles);
        access.addAll(roles);
        access.add(AuthUser.getId().toString());
//        access.add("*");
        return access;
    }

}
