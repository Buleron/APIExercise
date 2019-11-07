package actor;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.cluster.pubsub.DistributedPubSub;
import akka.cluster.pubsub.DistributedPubSubMediator;
import scala.concurrent.ExecutionContext;

import javax.inject.Inject;
import java.time.Duration;
import java.util.concurrent.Executor;

public class SchedulerActor extends AbstractActor {
    @Inject
    private Executor context;
    @Inject
    private ActorSystem system;
    @Inject
    private ActorRef out;


    public static Props props(ActorRef out, ActorSystem actorSystem, Executor context) {
        return Props.create(SchedulerActor.class, () -> new SchedulerActor(out, actorSystem, context));
    }

    SchedulerActor(ActorRef out, ActorSystem actorSystem, Executor context) {
        this.out = out;
        this.system = actorSystem;
        this.context = context;

        ActorRef mediator = DistributedPubSub.get(getContext().getSystem()).mediator();
        mediator.tell(new DistributedPubSubMediator.Subscribe("scheduled-Tack", getSelf()), getSelf());
        initialize();
    }

    private void initialize() {
        this.system
                .scheduler()
                .schedule(
                        Duration.ofSeconds(5), // initialDelay
                        Duration.ofSeconds(2), // interval
                        () -> System.out.println("Running block of code"),
                        (ExecutionContext) this.context);
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .matchAny(
                        m -> {
                            this.handleMessage();
                            System.out.println("Scheduled-Tick");
                        })
                .build();
    }


    private void handleMessage() {
        out.tell("scheduled-Tack", getSelf());
    }
}
