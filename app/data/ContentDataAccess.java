package data;

import com.mongodb.client.MongoDatabase;
import models.collection.content.DashboardContent;

public class ContentDataAccess extends DataAccess<DashboardContent> {

    public ContentDataAccess(MongoDatabase mongo) {
        super(mongo, DashboardContent.class);
    }
}
