package mongolay.example;

import lombok.Data;
import models.collection.CollectionModel;
import mongolay.annotations.Entity;
import mongolay.annotations.Reference;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by agonlohaj on 14 Aug, 2019
 */
@Entity(collection = "categories")
@Data
public class Category extends CollectionModel {
	public String name;

	@BsonIgnore
	@Reference(from = "_id", to = "categoryId")
	private List<Product> productList = new ArrayList<>();
}
