package models.collection;

import Interfaces.IContent;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;


public @Data class Content extends CollectionModel {
    //todo - Does it work as reference ??;
//    @Reference(from = "dashboardId", to = "_id")
    private String dashboardId;
    public List<IContent> content = new ArrayList<>();
}
