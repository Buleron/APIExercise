package mongo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import models.Roles;
import models.collection.CollectionModel;
import models.collection.User;
import models.enums.AccessLevelType;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccessControl {
    // access level per row
    public static final String READ_ACL = "readACL";
    public static final String WRITE_ACL = "writeACL";

    private AccessLevelType type;
    private List<String> userRoles;

    /**
     * constructor
     */
    public AccessControl(User user, AccessLevelType type){
        this.type = type;
        userRoles = user.getRoles().stream().map(ObjectId::toString).collect(Collectors.toList());
    }

    /**
     * applies the access filter to the query
     * @return
     */
    public List<Bson> applyAccessFilter() {
        List<Bson> query = new ArrayList<>();
        boolean isRead = type == AccessLevelType.READ;
        boolean isWrite = type == AccessLevelType.WRITE;
        if (isRead) {
            // check if both don't exists, that means its a public thing
            Bson notSet = Filters.and(Filters.exists(READ_ACL, false), Filters.exists(WRITE_ACL, false));
            // check if both their sizes are empty
            Bson bothEmpty = Filters.and(Filters.size(READ_ACL, 0), Filters.size(WRITE_ACL, 0));
            // if one of the above hits, its all public, we assume

            // otherwise check if role is contained on one of them
            Bson roleCheck = Filters.or(Filters.in(READ_ACL, userRoles), Filters.in(WRITE_ACL, userRoles));
            query.add(Filters.or(
                    notSet,
                    bothEmpty,
                    roleCheck
            ));
        }
        if (isWrite) {
            // check if both don't exists, that means its a public thing
            Bson notSet = Filters.and(Filters.exists(READ_ACL, false), Filters.exists(WRITE_ACL, false));
            // check if both their sizes are empty
            Bson bothEmpty = Filters.and(Filters.size(READ_ACL, 0), Filters.size(WRITE_ACL, 0));

            // otherwise check if role is within Write access
            Bson roleCheck = Filters.in(WRITE_ACL, userRoles);
            query.add(Filters.or(
                    notSet,
                    bothEmpty,
                    roleCheck
            ));
        }
        return query;
    }

    public boolean isAccessible(MongoCollection<Document> collection, Bson filter, ObjectId id) {
        if(id == null) {
            return true;
        }
        List<Bson> findFilters = new ArrayList<>();
        findFilters.add(Filters.eq("_id", id));
        if (filter != null) {
            findFilters.add(filter);
        }
        FindIterable<Document> find = collection.find(Filters.and(findFilters));
        Document document = find.first();
        if(document != null) {
            FindIterable<Document> list = collection.find();
            List<Bson> filters = applyAccessFilter();
            filters.add(Filters.eq("_id", id));
            if (filter != null) {
                filters.add(filter);
            }
            list.filter(Filters.and(filters));
            document = list.projection(Projections.include("_id")).first();

            return document != null;
        }
        return true;
    }

    public <T extends CollectionModel> boolean isAccessibleOnCollection (MongoCollection<T> collection, Bson filter, ObjectId id) {
        if(id == null) {
            return true;
        }
        List<Bson> findFilters = new ArrayList<>();
        findFilters.add(Filters.eq("_id", id));
        if (filter != null) {
            findFilters.add(filter);
        }
        FindIterable<T> find = collection.find(Filters.and(findFilters));
        T document = find.first();
        if(document != null) {
            FindIterable<T> list = collection.find();
            List<Bson> filters = applyAccessFilter();
            filters.add(Filters.eq("_id", id));
            if (filter != null) {
                filters.add(filter);
            }
            list.filter(Filters.and(filters));
            document = list.projection(Projections.include("_id")).first();

            return document != null;
        }
        return true;
    }
}
