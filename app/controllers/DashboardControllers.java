package controllers;

import models.collection.Dashboard;
import mongo.MongoDB;
import play.i18n.MessagesApi;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.BodyParser;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;
import services.DashboardService;
import utils.DatabaseUtils;
import utils.ServiceUtils;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;

import static play.mvc.Results.ok;

public class DashboardControllers {

    @Inject
    MongoDB mongoDB;
    @Inject
    MessagesApi messagesApi;

    @Inject
    HttpExecutionContext
    context;

    public CompletableFuture<Result> all (Http.Request request) {
        return CompletableFuture.supplyAsync(() -> new DashboardService(mongoDB.getDatabase()).all(context.current()))
            .thenCompose(ServiceUtils::toJsonNode)
            .thenApply(Results::ok)
            .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }
    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> findById(String id){
        return ServiceUtils.parseBodyOfType(context.current(),Dashboard.class)
                .thenCompose((item) -> new DashboardService(mongoDB.getDatabase()).findById(id,context.current()))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception,messagesApi));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> save (Http.Request request) {
        return ServiceUtils.parseBodyOfType(context.current(), Dashboard.class)
                .thenCompose((item) -> new DashboardService(mongoDB.getDatabase()).save(item, context.current()))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> update (Http.Request request) {
        return ServiceUtils.parseBodyOfType(context.current(), Dashboard.class)
                .thenCompose((item) -> new DashboardService(mongoDB.getDatabase()).update(item))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }

    @BodyParser.Of(BodyParser.Json.class)
    public CompletableFuture<Result> delete (Http.Request request) {
        return ServiceUtils.parseBodyOfType(context.current(), Dashboard.class)
                .thenCompose((item) -> new DashboardService(mongoDB.getDatabase()).delete(item))
                .thenCompose(ServiceUtils::toJsonNode)
                .thenApply(Results::ok)
                .exceptionally((exception) -> DatabaseUtils.resultFromThrowable(exception, messagesApi));
    }
//
//    public Result createDashboard(Http.Request request) {
//        JsonNode node = checkRequest(request);
//        if(node == null)
//            return ok(Json.newObject().putPOJO(result,new Response(false,-1, ResponseMessage.PARAMETERS_ERROR.toString())));
//        Dashboard dashboard = new ObjectMapper().convertValue(node, Dashboard.class);
//        dashboard.setId(UUID.randomUUID().toString());
//        dashboard.setCreatedAt(new java.sql.Timestamp(Instant.now().toEpochMilli()));
//        Key<Dashboard> res = dashboard.save();
//        if(res.getId().toString().isEmpty())
//            return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.NO_DATA_FOUND.toString())));
//        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),dashboard)));
//    }
//
//    public Result updateDashboard(Http.Request request) {
//        JsonNode node = checkRequest(request);
//        if(node == null)
//            return ok(Json.newObject().putPOJO(result,new Response(false,-1,ResponseMessage.PARAMETERS_ERROR.toString())));
//        Dashboard dashboard = new ObjectMapper().convertValue(node, Dashboard.class);
//        Key<Dashboard> res = dashboard.save();
//        if(res.getId().toString().isEmpty())
//            return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.NO_DATA_FOUND.toString())));
//        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),dashboard)));
//    }
//
//
//    public Result deleteDashboard(Http.Request request) {
//        JsonNode node = checkRequest(request);
//        if (node == null)
//            return ok(Json.newObject().putPOJO(result, new Response(false, -1, ResponseMessage.PARAMETERS_ERROR.toString())));
//        Dashboard dashboard = new ObjectMapper().convertValue(node, Dashboard.class);
//        WriteResult res = dashboard.deleteById(dashboard.getId());
//        if (res.getN() == 1)
//            return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.SUCCESSFULLY.toString(), dashboard)));
//        return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.NO_DATA_FOUND.toString())));
//    }
//
//    public Result getAllDashboards()  {
//        List<Dashboard> dashboards =  new Dashboard().findAll();
//
//        return status(200, Json.newObject().putPOJO(result, new Response(false, -1, ResponseMessage.PARAMETERS_ERROR.toString())));
////        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),dashboards)));
//    }
//    public Result getDashboardById(Http.Request request) {
//        JsonNode node = checkRequest(request);
//        if(node == null)
//            return ok(Json.newObject().putPOJO(result,new Response(false,-1,ResponseMessage.PARAMETERS_ERROR.toString())));
//        Dashboard dashboard = new ObjectMapper().convertValue(node, Dashboard.class);
//       Dashboard res = dashboard.findById(dashboard.getId());
//        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),res)));
//    }
//
//
//    public JsonNode checkRequest(Http.Request request){
//        if (request.body().toString().isEmpty()) {
//            Logger.of("json body is null");
//            return null;
//        }
//        JsonNode node = request.body().asJson();
//        return node;
//    }
//
//    public Result requiresJwt() {
//        return jwtControllerHelper.verify(request(), res -> {
//            if (res.left.isPresent()) {
//                return forbidden(res.left.get().toString());
//            }
//
//            VerifiedJwt verifiedJwt = res.right.get();
//            Logger.debug("{}", verifiedJwt);
//
//            ObjectNode result = Json.newObject();
//            result.put("access", "granted");
//            result.put("secret_data", "birds fly");
//            return ok(result);
//        });
//    }
//
//    public Result requiresJwtViaFilter() {
//        Optional<VerifiedJwt> oVerifiedJwt = request().attrs().getOptional(Attrs.VERIFIED_JWT);
//        return oVerifiedJwt.map(jwt -> {
//            Logger.debug(jwt.toString());
//            return ok("access granted via filter");
//        }).orElse(forbidden("eh, no verified jwt found"));
//    }
}
