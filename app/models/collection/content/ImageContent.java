package models.collection.content;

import lombok.Data;
import models.enums.ContentType;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Data
@BsonDiscriminator(key = "type", value = "IMAGE")
public class ImageContent extends BaseContent {
    ContentType type = ContentType.IMAGE;
    private String url;
}