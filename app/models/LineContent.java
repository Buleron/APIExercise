package models;

import lombok.Data;
import lombok.ToString;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@Data
public class LineContent implements IContent {
    List<Document> data = new ArrayList<>();
}
