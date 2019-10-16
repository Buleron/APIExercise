package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSubMediator;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.cluster.pubsub.DistributedPubSub;
import models.collection.User;
import models.enums.SocketMessageTypes;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executor;

public class ChatActors extends AbstractActor {
    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    // activate the extension
    private ActorRef mediator = DistributedPubSub.get(getContext().system()).mediator();
    private String roomId;
    private User user;
//    private ChatService service;
    private ActorRef out;
    private Executor context;
    // Users within the actors.chat room
    private Set<String> users = new HashSet<>();

    public static Props props(ActorRef out, String roomId, User user,  Executor context) {
        return Props.create(ChatActors.class, () -> new ChatActors(out, roomId, user,  context));
    }

    public ChatActors(ActorRef out, String roomId, User user, Executor context) {
        this.out = out;
        this.roomId = roomId;
        this.user = user;
        this.context = context;

        mediator.tell(new DistributedPubSubMediator.Subscribe(roomId, getSelf()), getSelf());

       // users.add(user.getId());
    }

    public static class JoinedRoom {
      String userID;
      public JoinedRoom(String userId){
          this.userID = userId;
      }
    }

    public static class LeftRoom {
        String userID;
        public LeftRoom (String userId) {
            this.userID = userId;
        }
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class,this::handleMessage)
                .build();
    }

    public void handleMessage (String message) {
        if(message.equals(SocketMessageTypes.PING.name())) {
            out.tell(SocketMessageTypes.PONG.name(), getSelf());
        }
    }
}
