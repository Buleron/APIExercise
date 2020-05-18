package models.collection.content;


import org.bson.codecs.pojo.annotations.BsonDiscriminator;

@BsonDiscriminator(key = "type", value = "NONE")
public class BaseContent implements IContent  {
}
