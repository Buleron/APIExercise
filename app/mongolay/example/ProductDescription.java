package mongolay.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.collection.CollectionModel;
import mongolay.annotations.Entity;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * Created by agonlohaj on 14 Aug, 2019
 */
@Entity(collection = "descriptions")
@Data
@AllArgsConstructor
public class ProductDescription extends CollectionModel {
    @NotEmpty
    private String size;
    @NotEmpty
    private String weight;

    private String name;
    private String brand;
}
