package models.collection.content;

import lombok.Data;
import models.enums.ContentType;
import org.bson.Document;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@Data
@BsonDiscriminator(key = "type", value = "EMAIL")
public class EmailContent extends BaseContent {
    ContentType type = ContentType.EMAIL;
    Document layout;
    private String text;
    private String email;
    private String subject;
}
