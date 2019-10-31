package data;

import com.mongodb.client.MongoDatabase;
import models.collection.content.Content;
import mongolay.MongoRelay;

public class ContentDataAccess extends DataAccess<Content> {

    public ContentDataAccess(MongoDatabase mongo) {
        super(mongo, Content.class);
    }
}
