package models.collection.content;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import models.enums.ContentType;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME,
        property="type",
        visible = true)
@JsonSubTypes({
        @JsonSubTypes.Type(value= EmailContent.class, name = "EMAIL"),
        @JsonSubTypes.Type(value= TextContent.class, name = "TEXT"),
        @JsonSubTypes.Type(value= ImageContent.class, name = "IMAGE"),
        @JsonSubTypes.Type(value= LineContent.class, name = "LINE"),
        @JsonSubTypes.Type(value= PieContent.class, name = "PIE"),
        @JsonSubTypes.Type(value= BarContent.class, name = "BAR"),
        @JsonSubTypes.Type(value= TreeContent.class, name = "TREEMAP")
})
public interface IContent {
    ContentType type = ContentType.TEXT;
}
