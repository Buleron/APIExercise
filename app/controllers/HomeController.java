package controllers;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.japi.pf.PFBuilder;
import akka.stream.IOResult;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import akka.util.ByteString;
import models.collection.User;
import mongo.MongoDB;
import play.http.HttpEntity;
import play.i18n.MessagesApi;
import play.libs.Files;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.*;
import services.HomeService;
import utils.DatabaseUtils;
import utils.ServiceUtils;

import javax.inject.Inject;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    @Inject
    MongoDB mongoDB;
    @Inject
    MessagesApi messagesApi;
    @Inject
    HttpExecutionContext context;
    @Inject
    Materializer materializer;

    public CompletableFuture<Result> authenticate(Http.Request request) {
        return ServiceUtils.parseBodyOfType(request.body(), context.current(), User.class)
                .thenCompose((item) -> new HomeService(mongoDB.getDatabase()).auth(item, context.current()))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    public Result upload(Http.Request request) {

        Http.MultipartFormData<Files.TemporaryFile> body = request.body().asMultipartFormData();
        Http.MultipartFormData.FilePart<Files.TemporaryFile> picture = body.getFile("picture");

        java.io.File file = new java.io.File("C:/Users/38345/Desktop/uploadServerFile/fileToServe.pdf");
        java.nio.file.Path path = file.toPath();
        Source<ByteString, CompletionStage<IOResult>> source = FileIO.fromPath(path);

        Optional<Long> contentLength = Optional.of(file.length());
//
//        return new Result(
//                new ResponseHeader(200, Collections.emptyMap()),
//                new HttpEntity().Streamed(source,contentLength,Optional.of("text/plain")));


        Source.from(Arrays.asList(-1,0,1,2,3,4,5))
                .map(n-> {
                  if(n < 5) return n.toString();
                  throw new RuntimeException("Boom!!!");
                })
                .recover(new PFBuilder().match(RuntimeException.class,ex-> "Stream Truncated").build())
                .runForeach(System.out::println,materializer);

        Source<String, NotUsed> planB = Source.from(Arrays.asList("five", "six", "seven", "eight"));

        //recovery with retries
        Source.from(Arrays.asList(-1,0,1,2,3,4,5))
                .map(m-> {
                  if(m < 5) return m.toString();
                  throw new RuntimeException("Boom");
                })
                .recoverWithRetries(
                        5,
                        new PFBuilder().match(RuntimeException.class,ex-> planB).build())
                .runForeach(System.out::println,materializer);
//
//        Flow<ScoopOfBatter, HalfCookedPancake, NotUsed> fryingPan1 =
//                Flow.of(ScoopOfBatter.class).map(batter -> new HalfCookedPancake());



        if (picture.getContentType().contains("image")) {
            String fileName = picture.getFilename();
            long fileSize = picture.getFileSize();
            Files.TemporaryFile files = picture.getRef();
            String url = "C:/Users/38345/Desktop/uploadServerFile/";
            String paths = url + fileName;
            files.copyTo(Paths.get(paths), true);
            return ok("File Uploaded!");
        } else {
            return badRequest().flashing("error", "Upload picture only my friend");
        }
    }

}
