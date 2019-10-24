package actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.fasterxml.jackson.databind.JsonNode;
import models.collection.User;
import models.collection.chat.ChatMessage;
import models.enums.SocketMessageTypes;
import org.bson.types.ObjectId;
import services.ChatService;
import utils.DatabaseUtils;
import utils.ServiceUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

public class ChatUnreadActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    // activate the extension
    private ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
    private User user;
    private ChatService service;
    private ActorRef out;
    private Executor context;

    public static Props props(ActorRef out, User user, ChatService service, Executor context) {
        return Props.create(ChatUnreadActor.class, () -> new ChatUnreadActor(out,  user, service, context));
    }

    ChatUnreadActor(ActorRef out, User user, ChatService service, Executor context) {
        this.user = user;
        this.out = out;
        this.service = service;
        this.context = context;
        mediator.tell(new DistributedPubSubMediator.Subscribe(user.getId().toString(), getSelf()), getSelf());
        initialise();
    }

    public void initialise () {
        service.findMyMessages(context,user)
                .thenCompose(rooms -> {
            return CompletableFuture.completedFuture(rooms.stream().map(ChatMessage::getRoomId).collect(Collectors.toList()));
        }).thenCompose((rooms) -> {
            for (ObjectId room : rooms) {
                mediator.tell(new DistributedPubSubMediator.Subscribe(room.toString(), getSelf()), getSelf());
            }
            return service.getAllMessagesByRooms(rooms,context);
        })
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply((unread) -> {
                    out.tell(unread.toString(), getSelf());
                    return unread;
                }).exceptionally((error) -> {
            return null;
        });
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::handleMessage)
                .match(ChatMessage.class, this::messageReceived)
                .match(DistributedPubSubMediator.CountSubscribers.class, msg -> log.info("count"))
                .build();
    }

    public void messageReceived (ChatMessage message) {
        if (message == null) {
            return;
        }
        JsonNode chat = DatabaseUtils.toJsonNode(message);
        if (chat != null) {
            out.tell(chat.toString(), getSelf());
        }
    }

    public void handleMessage (String message) {
        if(message.equals(SocketMessageTypes.PING.name())) {
            out.tell(SocketMessageTypes.PONG.name(), getSelf());
        }
    }


}
