package models.collection.content;

import lombok.Data;
import models.enums.ContentType;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Data
@BsonDiscriminator(key = "type", value = "TEXT")
public class TextContent implements IContent {
    ContentType type = ContentType.TEXT;
    private String text;
}
