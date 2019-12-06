package models.collection.content;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

import javax.swing.text.Document;

@JsonInclude(Include.NON_NULL)
public @Data class  DataContent {
    private String name;
    private int value;

}