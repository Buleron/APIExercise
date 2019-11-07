package actor;

import javax.inject.Named;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import scala.concurrent.ExecutionContext;
import scala.concurrent.duration.Duration;
import javax.inject.Inject;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

public class MyActorTask {
    private final ActorRef someActor;
    private final ActorSystem actorSystem;
    private final Executor executor;

    @Inject
        MyActorTask(@Named("Scheduled-Tick") ActorRef someActor, ActorSystem actorSystem, Executor executor) {
        this.someActor = someActor;
        this.actorSystem = actorSystem;
        this.executor = executor;
        this.initialize();
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

}