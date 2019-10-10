package models;

import Interfaces.IContent;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class EmailContent implements IContent {
    private String text;
    private String email;
    private String subject;
}
