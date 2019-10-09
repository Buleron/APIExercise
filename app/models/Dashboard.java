package models;

import com.mongodb.WriteResult;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.mongodb.morphia.annotations.Id;

import java.sql.Timestamp;

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


    public void save() {
        datastore().save(this);
    }

    public Dashboard finds(){
        return datastore().find(Dashboard.class).get();
    }

    public WriteResult delete(){
        WriteResult writeResult = datastore().delete(Dashboard.class);
        writeResult.isUpdateOfExisting();
        return datastore().delete(Dashboard.class);
    }

    public Dashboard query() {
        return datastore().createQuery(Dashboard.class).get();
    }

}
