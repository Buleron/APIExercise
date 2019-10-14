package controllers;

import play.libs.concurrent.HttpExecutionContext;

import javax.inject.Inject;

public class ContentController {

     private DashboardControllers dashboardControllers = new DashboardControllers();
     private static final String result = "result";
     @Inject
     HttpExecutionContext context;

//    public CompletableFuture<Result> createContent(Http.Request request) {
//        return CompletableFuture.supplyAsync(() -> {
//            JsonNode node = dashboardControllers.checkRequest(request);
//            if(node == null)
//                return ok(Json.newObject().putPOJO(result,new Response(false, Http.Status.NOT_FOUND, ResponseMessage.PARAMETERS_ERROR.toString())));
//            Content content =  new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).convertValue(node, Content.class);
//            content.setId(UUID.randomUUID().toString());
//            //check if dashboard parent id exists before insert
//            if(!new Dashboard().checkIfExists(content.getDashboardId()))
//                return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.WRONG_DASHBOARD_ID.toString())));
//            Key<Content> res = content.save();
//            if(res.getId().toString().isEmpty())
//                return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.NO_DATA_FOUND.toString())));
//            return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),content)));
//        }, context.current());
//    }
//
//    public Result updateContent(Http.Request request) {
//        JsonNode node = dashboardControllers.checkRequest(request);
//        if(node == null)
//            return ok(Json.newObject().putPOJO(result,new Response(false,-1,ResponseMessage.PARAMETERS_ERROR.toString())));
//        Content content = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).convertValue(node, Content.class);
//        //check if dashboard parent id exists before insert
//        if(!new Dashboard().checkIfExists(content.getDashboardId()))
//            return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.WRONG_DASHBOARD_ID.toString())));
//        Key<Content> res = content.save();
//        if(res.getId().toString().isEmpty())
//            return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.NO_DATA_FOUND.toString())));
//        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),content)));
//    }
//
//    public Result deleteContent(Http.Request request)  {
//        JsonNode node = dashboardControllers.checkRequest(request);
//        if(node == null)
//            return ok(Json.newObject().putPOJO(result,new Response(false,-1,ResponseMessage.PARAMETERS_ERROR.toString())));
//        Content content = new ObjectMapper().convertValue(node, Content.class);
//        WriteResult res = content.deleteById(content.getId());
//        if (res.getN() == 1)
//            return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.SUCCESSFULLY.toString())));
//        return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.NO_DATA_FOUND.toString())));
//    }
//
//    public Result getContentById(Http.Request request)  {
//        JsonNode node =  dashboardControllers.checkRequest(request);
//        if(node == null)
//            return ok(Json.newObject().putPOJO(result,new Response(false,-1,ResponseMessage.PARAMETERS_ERROR.toString())));
//        Content content = new ObjectMapper().convertValue(node, Content.class);
//        Content res = content.findById(content.getId());
//        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),res)));
//    }
//
//    public Result getAllContent()  {
//        Content content = new Content();
//        List<Content> res = content.findAll();
//        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),res)));
//    }
}
