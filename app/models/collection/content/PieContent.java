package models.collection.content;

import lombok.Data;
import models.enums.ContentType;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.ArrayList;
import java.util.List;

@Data
@BsonDiscriminator(key = "type", value = "PIE")
public class PieContent extends BaseContent {
    ContentType type = ContentType.PIE;
    List<DataContent> data = new ArrayList<>();
}
