package actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.collection.User;
import models.collection.chat.ChatMessage;
import models.enums.SocketMessageTypes;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import models.exceptions.RequestException;
import org.bson.types.ObjectId;
import play.mvc.Http;
import services.ChatService;
import utils.DatabaseUtils;
import utils.ServiceUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

public class ChatActor extends AbstractActor {

    private ActorRef mediator = DistributedPubSub.get(getContext().getSystem()).mediator();
    private String roomId;
    private User user;
    private ChatService chatService;
    private ActorRef out;
    private Executor context;

    private Set<String> users = new HashSet<>();

    public static Props props(ActorRef out, String roomId, User user, ChatService chatService, Executor context) {
        return Props.create(ChatActor.class, () ->  new ChatActor(out, roomId, user, chatService, context));
    }
    ChatActor(ActorRef out, String roomId, User user, ChatService chatService, Executor context) {
        this.out = out;
        this.roomId = roomId;
        this.user = user;
        this.chatService = chatService;
        this.context = context;

        mediator.tell(new DistributedPubSubMediator.Subscribe(roomId, getSelf()),getSelf());
        users.add(user.getId().toString());
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::handleMessage)
                .match(ChatMessage.class, this::messageReceived)
                .build();
    }

    public void messageReceived (ChatMessage chatMessage) {
        if(chatMessage == null) {
            return;
        }
        JsonNode chat = DatabaseUtils.toJsonNode(chatMessage);
            if(chat != null)
                out.tell(chat.toString(),getSelf());
    }


    public void handleMessage(String message) {
        if(message.equals(SocketMessageTypes.PING.name())) {
            out.tell(SocketMessageTypes.PONG.name(),getSelf());
            return;
        }
        CompletableFuture.supplyAsync(()-> {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode actualObj = mapper.readValue(message, JsonNode.class);
                ChatMessage chatMessage = DatabaseUtils.jsonToJavaClass(actualObj, ChatMessage.class);
                chatMessage.setRoomId(new ObjectId(roomId));
                chatMessage.setUserId(user.getId());
                return chatMessage;
            } catch (IOException e) {
                e.printStackTrace();
                throw new CompletionException(new RequestException(Http.Status.BAD_REQUEST, "bad_request"));
            }
        },context).thenCompose((ChatMessage) -> {
           return chatService.save(ChatMessage,context);
        }).thenCompose((result) -> ServiceUtils.toJsonNode(result))
            .thenCompose((json) -> {
                    out.tell(json.toString(), getSelf());
                    return CompletableFuture.completedFuture(json);
            });
    }


}
