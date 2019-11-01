package models.collection.content;

import lombok.Data;
import models.enums.ContentType;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

import java.util.ArrayList;
import java.util.List;

@Data
@BsonDiscriminator(key = "type", value = "LINE")
public class LineContent extends BaseContent {
    ContentType type = ContentType.LINE;
    List<DataContent> data = new ArrayList<>();
}
