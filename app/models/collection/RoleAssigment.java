package models.collection;

import com.mongodb.client.MongoCollection;
import lombok.Data;

import java.util.Set;

public @Data
class RoleAssigment extends CollectionModel {
    private String name;
    private Set<String> read;
    private Set<String> write;

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
