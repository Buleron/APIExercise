package models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import models.collection.chat.ChatMessage;

import java.util.List;

/**
 * Created by agonlohaj on 31 Oct, 2019
 */
@Data
@AllArgsConstructor
public class PaginatedChatMessages {
	private String until;
	private List<ChatMessage> messages;
}
