package models.collection.content;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import models.collection.CollectionModel;
import models.collection.Dashboard;
import models.utils.ObjectIdDeSerializer;
import models.utils.ObjectIdStringSerializer;
import mongolay.annotations.Entity;
import mongolay.annotations.Index;
import mongolay.annotations.Reference;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;

@Entity(collection = "content")
public @Data class DashboardContent extends CollectionModel {
    @Index
    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    private ObjectId dashboardId;

    @Reference(from = "dashboardId", to = "_id")
    private Dashboard dashboard;

    public List<BaseContent> content = new ArrayList<>();
}
