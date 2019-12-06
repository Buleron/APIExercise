package models.collection.content;

import lombok.Data;
import models.enums.ContentType;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Data
@BsonDiscriminator(key = "type", value = "TEXT")
public class TextContent extends BaseContent {
    ContentType type = ContentType.TEXT;
    Document layout;
    private String text;
}
