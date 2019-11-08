package controllers;

import models.collection.User;
import mongo.MongoDB;
import play.i18n.MessagesApi;
import play.libs.Files;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.HomeService;
import utils.DatabaseUtils;
import utils.ServiceUtils;

import javax.inject.Inject;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

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
        if (picture.getContentType().contains("image")) {
            String fileName = picture.getFilename();
            long fileSize = picture.getFileSize();
            Files.TemporaryFile file = picture.getRef();
            String url = "C:/Users/38345/Desktop/uploadServerFile/";
            String path = url + fileName;
            file.copyTo(Paths.get(path), true);
            return ok("File Uploaded!");
        } else {
            return badRequest().flashing("error", "Upload picture only my friend");
        }
    }

}
