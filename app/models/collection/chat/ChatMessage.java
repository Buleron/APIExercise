package models.collection.chat;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import models.collection.CollectionModel;
import models.utils.ObjectIdDeSerializer;
import models.utils.ObjectIdStringSerializer;
import mongolay.annotations.Entity;
import mongolay.annotations.Index;
import mongolay.utils.IndexType;
import org.bson.codecs.pojo.annotations.BsonDiscriminator;
import org.bson.types.ObjectId;


@BsonDiscriminator
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
@Entity(collection = "chat")
public class ChatMessage extends CollectionModel {
    @Index(type = IndexType.DESC)
    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    private ObjectId roomId;

    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    private ObjectId userId;

    @JsonSerialize(using = ObjectIdStringSerializer.class)
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    private ObjectId  toUserId;

    @Index(type = IndexType.TEXT)
    private String text;

    @Override
    public boolean equals(Object object){
        return super.equals(object);
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

}
