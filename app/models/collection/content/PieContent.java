package models.collection.content;

import lombok.Data;
import models.enums.ContentType;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.ArrayList;
import java.util.List;

@Data
@BsonDiscriminator(key = "type", value = "PIE")
public class PieContent extends BaseContent {
    ContentType type = ContentType.PIE;
    Document layout;
    List<DataContent> data = new ArrayList<>();
}
