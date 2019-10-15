package models.collection;

import Interfaces.IContent;
import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.ArrayList;
import java.util.List;

@BsonDiscriminator
@AllArgsConstructor
@NoArgsConstructor
public @Data class Content extends CollectionModel {
//
//    private String id;
//    @Reference(from = "dashboardId", to = "_id")
    private String dashboardId;
    public List<IContent> content = new ArrayList<>();
}
