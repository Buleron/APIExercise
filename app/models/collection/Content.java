package models.collection;

import Interfaces.IContent;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import mongolay.annotations.Reference;

import java.util.ArrayList;
import java.util.List;
@JsonIgnoreProperties(ignoreUnknown = true)
public @Data class Content {
//    @Id
    private String id;
    @JsonProperty
    @Reference(from = "dashboardId", to = "id")
    private String dashboardId;
    public List<IContent> content = new ArrayList<>();
}
