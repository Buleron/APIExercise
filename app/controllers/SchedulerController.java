package controllers;

import actor.SchedulerActor;
import akka.actor.ActorSystem;
import akka.cluster.Cluster;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import play.i18n.MessagesApi;
import play.libs.F;
import play.libs.concurrent.HttpExecutionContext;
import play.libs.streams.ActorFlow;
import play.mvc.Result;
import play.mvc.Results;
import utils.DatabaseUtils;
import utils.ServiceUtils;
import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

public class SchedulerController {
    @Inject
    HttpExecutionContext ec;
    @Inject
    Materializer materializer;
    @Inject
    MessagesApi messagesApi;
    @Inject
    ActorSystem system;


    public CompletableFuture<Result> scheduler() {
        return CompletableFuture.supplyAsync(this::test)
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    public CompletableFuture<F.Either<Object, Flow<Object, Object, ?>>> test() {
        Cluster cluster = Cluster.get(system);
        return CompletableFuture
                .completedFuture(F.Either.Right(ActorFlow.actorRef((out) -> SchedulerActor.props(out,system, ec.current()),
                        cluster.system(), materializer)));
    }

}
