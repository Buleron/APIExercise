package models;

import Interfaces.IContent;
import lombok.Data;
import models.enums.ContentType;

import java.util.ArrayList;
import java.util.List;

@Data
public class LineContent implements IContent {
    ContentType type = ContentType.LINE;
    List<DataContent> data = new ArrayList<>();
}
