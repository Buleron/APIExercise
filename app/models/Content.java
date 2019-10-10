package models;

import Interfaces.IContent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mongodb.WriteResult;
import lombok.Data;
import org.mongodb.morphia.annotations.Id;

import java.util.ArrayList;
import java.util.List;

import static controllers.MongoConfig.datastore;
@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class Content {
    @Id
    private String id;
    @JsonProperty
    private String dashboardId;
    public List<IContent> content = new ArrayList<>();

    public void save() {
        datastore().save(this);
    }

    public Content findById(String id){
        return datastore().find(Content.class,"id",id).get();
    }

    public WriteResult deleteById(String id){
        WriteResult writeResult = datastore().delete(Content.class,id);
        return  writeResult;
    }

    public Content query() {
        return datastore().createQuery(Content.class).get();
    }
}
