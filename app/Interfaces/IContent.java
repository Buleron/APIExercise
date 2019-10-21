package Interfaces;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import models.*;
import models.enums.ContentType;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
        property="type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value= EmailContent.class, name = "EMAIL"),
        @JsonSubTypes.Type(value= TextContent.class, name = "TEXT"),
        @JsonSubTypes.Type(value= ImageContent.class, name = "IMAGE"),
        @JsonSubTypes.Type(value= LineContent.class, name = "LINE")
})
public interface IContent {
    ContentType type = ContentType.TEXT;
    ContentType email = ContentType.EMAIL;
    ContentType image = ContentType.IMAGE;
    ContentType line = ContentType.LINE;
}
