package models;

import Interfaces.IContent;
import lombok.Data;
import models.enums.ContentType;

@Data
public class EmailContent implements IContent {
    ContentType type = ContentType.EMAIL;
    private String text;
    private String email;
    private String subject;
}
