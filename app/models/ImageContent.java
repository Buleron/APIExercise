package models;

import Interfaces.IContent;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ImageContent implements IContent {
    private String url;
}
