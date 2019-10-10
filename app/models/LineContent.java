package models;

import Interfaces.IContent;
import lombok.Data;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@Data
public class LineContent implements IContent {
    List<Document> data = new ArrayList<>();
}
