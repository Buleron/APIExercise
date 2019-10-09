package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.WriteResult;
import lombok.Data;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static controllers.MongoConfig.datastore;
@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class Content {
    @JsonProperty
    private String dashboardId;
    public List<IContent> content = new ArrayList<>();

    DataContent dataContent = new DataContent();
    public void save() {
        datastore().save(this);
    }

    public Content finds(){
        return datastore().find(Content.class).get();
    }

    public WriteResult delete(){
        WriteResult writeResult = datastore().delete(Content.class);
        writeResult.isUpdateOfExisting();
        return datastore().delete(Content.class);
    }

    public Content query() {
        return datastore().createQuery(Content.class).get();
    }
}
