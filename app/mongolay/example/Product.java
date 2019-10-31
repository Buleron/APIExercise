package mongolay.example;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import models.collection.CollectionModel;
import models.collection.User;
import models.utils.ObjectIdDeSerializer;
import models.utils.ObjectIdStringSerializer;
import mongolay.annotations.Entity;
import mongolay.annotations.Reference;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

/**
 * Created by agonlohaj on 14 Aug, 2019
 */
@Entity(collection = "products")
@Data
public class Product extends CollectionModel {
    private String name;
    private String brand;
    private String description;

    @Reference(from = {"name", "brand"}, to = {"name", "brand"})
    ProductDescription productDescription;


    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    private ObjectId categoryId;

    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    private ObjectId userId;

    @BsonIgnore
    @Reference(from = "userId", to = "_id")
    private User user;
}
