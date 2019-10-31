package mongolay.example;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.collection.User;
import models.utils.ObjectIdDeSerializer;
import models.utils.ObjectIdStringSerializer;
import mongolay.annotations.Reference;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;

/**
 * Created by agonlohaj on 14 Aug, 2019
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AggregatedProducts {
    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    private ObjectId userId;
    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    private ObjectId categoryId;

    @BsonIgnore
    @Reference(from = "userId", to = "_id")
    private User user;

    @BsonIgnore
    @Reference(from = "categoryId", to = "_id")
    private Category category;
}
