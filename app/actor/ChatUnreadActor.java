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
import services.ChatService;
import utils.DatabaseUtils;
import utils.ServiceUtils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ChatUnreadActor extends AbstractActor {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);
    private ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
    private User user;
    private ChatService service;
    private ActorRef out;
    private Executor context;
    private String roomId;

    public static Props props(ActorRef out, User user, String roomId, ChatService service, Executor context) {
        return Props.create(ChatUnreadActor.class, () -> new ChatUnreadActor(out, user, roomId, service, context));
    }

    ChatUnreadActor(ActorRef out, User user, String roomId, ChatService service, Executor context) {
        this.user = user;
        this.out = out;
        this.service = service;
        this.context = context;
        this.roomId = roomId;
        mediator.tell(new DistributedPubSubMediator.Subscribe(user.getId().toString(), getSelf()), getSelf());
        initialise();
    }

    public void initialise() {
        service.findByUsersIdRoomId(roomId, user.getId().toString(), context)
                .thenCompose(CompletableFuture::completedFuture).thenCompose(ServiceUtils::toJsonNode)
                .thenApply((myMsg) -> {
                    out.tell(myMsg.toString(), getSelf());
                    return myMsg;
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

    public void messageReceived(ChatMessage message) {
        if (message == null) {
            return;
        }
        JsonNode chat = DatabaseUtils.toJsonNode(message);
        if (chat != null) {
            out.tell(chat.toString(), getSelf());
        }
    }

    public void handleMessage(String message) {
        if (message.equals(SocketMessageTypes.PING.name())) {
            out.tell(SocketMessageTypes.PONG.name(), getSelf());
        }
    }


}
