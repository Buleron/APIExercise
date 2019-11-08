package actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class MyActorTask extends AbstractActor {

    private final ActorRef someActor;
    private final ActorSystem actorSystem;
    private final Executor executor;

    public static Props props(ActorRef out, ActorSystem actorSystem, Executor context) {
        return Props.create(MyActorTask.class, () -> new MyActorTask(out, actorSystem, context));
    }


    public MyActorTask(ActorRef someActor,ActorSystem actorSystem, Executor executor) {
        this.actorSystem = actorSystem;
        this.someActor = someActor;
        this.executor = executor;

        ActorRef mediator = DistributedPubSub.get(getContext().getSystem()).mediator();
        mediator.tell(new DistributedPubSubMediator.Subscribe("outScheduler", getSelf()), getSelf());
        initialize();
    }

    private void initialize() {
        actorSystem
                .scheduler()
                .schedule(
                        Duration.create(0, TimeUnit.SECONDS), // initialDelay
                        Duration.create(30, TimeUnit.SECONDS), // interval
                        someActor,
                        "tick", // message,
                        (ExecutionContext) executor,
                        ActorRef.noSender());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(
                        m -> {
                            this.handleMessage();
                            System.out.println("outScheduler");
                        })
                .build();
    }

    private void handleMessage() {
        someActor.tell("outScheduler", getSelf());
    }
}