package models;

import com.mongodb.WriteResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.annotations.Id;

import java.sql.Timestamp;
import java.util.List;

import static controllers.MongoConfig.datastore;

public @Data
@EqualsAndHashCode(of ="id")
class Dashboard {
    @Id
    private String id;
    private String name;
    private String description;
    private String parentId;
    private Timestamp createdAt;
    private String[] readACL;
    private String[] writeACL;


    public Key<Dashboard> save() {
        return datastore().save(this);
    }
    public List<Dashboard> findAll(){
        return datastore().find(Dashboard.class).asList();
    }

    public Dashboard findById(String id) {
        return datastore().find(Dashboard.class,"id",id).get();
    }

    public WriteResult deleteById(String id) {
        return datastore().delete(Dashboard.class,id);
    }

    public boolean checkIfExists(String id) {
        if(id.isEmpty())
            return false;
        if(datastore().find(Dashboard.class,"id",id).get() != null){
            return true;
        }
        return false;
    }

    public Dashboard query() {
        return datastore().createQuery(Dashboard.class).get();
    }

}
