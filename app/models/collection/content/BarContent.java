package models.collection.content;

import lombok.Data;
import models.enums.ContentType;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.ArrayList;
import java.util.List;

@Data
@BsonDiscriminator(key = "type", value = "BAR")
public class BarContent extends BaseContent {
    ContentType type = ContentType.BAR;
    List<DataContent> data = new ArrayList<>();
}
