package models;

import Interfaces.IContent;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class TextContent implements IContent {
    private String text;
}
