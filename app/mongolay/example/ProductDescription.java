package mongolay.example;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import models.collection.MongoCollectionModel;
import models.collection.User;
import models.utils.ObjectIdDeSerializer;
import models.utils.ObjectIdStringSerializer;
import mongolay.annotations.Entity;
import mongolay.annotations.Reference;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by agonlohaj on 14 Aug, 2019
 */
@Entity(collection = "descriptions")
@Data
@AllArgsConstructor
public class ProductDescription extends MongoCollectionModel {
	@NotEmpty
	private String size;
	@NotEmpty
	private String weight;

	private String name;
	private String brand;
}
