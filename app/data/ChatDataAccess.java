package data;

import com.mongodb.client.MongoDatabase;
import models.collection.chat.ChatMessage;

public class ChatDataAccess extends DataAccess<ChatMessage>{

    public ChatDataAccess(MongoDatabase mongo) {
        super(mongo, ChatMessage.class);
    }
}
