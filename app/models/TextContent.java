package models;

import Interfaces.IContent;
import lombok.Data;
import models.enums.ContentType;

@Data
public class TextContent implements IContent {
    ContentType type = ContentType.TEXT;
    private String text;
}
