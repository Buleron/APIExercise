package data;

import com.mongodb.client.MongoDatabase;
import models.collection.Dashboard;

public class DashboardsDataAccess extends DataAccess<Dashboard> {

    public DashboardsDataAccess(MongoDatabase mongo) {
        super(mongo, Dashboard.class);
    }
}
