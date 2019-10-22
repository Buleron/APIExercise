package models;

import Interfaces.IContent;
import lombok.Data;
import models.enums.ContentType;

@Data
public class ImageContent implements IContent {
    ContentType type = ContentType.IMAGE;
    private String url;
}
