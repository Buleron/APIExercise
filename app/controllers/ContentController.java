package controllers;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.WriteResult;
import models.Content;
import models.Response;
import models.enums.ResponseMessage;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import java.util.UUID;

import static play.mvc.Results.ok;


public class ContentController {

     private DashboardControllers dashboardControllers = new DashboardControllers();
     private static final String result = "result";

    public Result createContent(Http.Request request) {
        JsonNode node = dashboardControllers.checkRequest(request);
        if(node == null)
            return ok(Json.newObject().putPOJO(result,new Response(false,-1,ResponseMessage.PARAMETERS_ERROR.toString())));
        Content content =  new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).convertValue(node, Content.class);
        String uniqueID = UUID.randomUUID().toString();
        content.setId(uniqueID);
        content.save();
        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),content)));
    }

    public Result updateContent(Http.Request request) {
        JsonNode node = dashboardControllers.checkRequest(request);
        if(node == null)
            return ok(Json.newObject().putPOJO(result,new Response(false,-1,ResponseMessage.PARAMETERS_ERROR.toString())));
        Content content = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).convertValue(node, Content.class);
        content.save();
        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),content)));
    }

    public Result deleteContent(Http.Request request)  {
        JsonNode node = dashboardControllers.checkRequest(request);
        if(node == null)
            return ok(Json.newObject().putPOJO(result,new Response(false,-1,ResponseMessage.PARAMETERS_ERROR.toString())));
        Content content = new ObjectMapper().convertValue(node, Content.class);
        WriteResult res = content.deleteById(content.getId());
        if (res.getN() == 1)
            return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.SUCCESSFULLY.toString())));
        return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.NO_DATA_FOUND.toString())));
    }

    public Result getContent(Http.Request request)  {
        JsonNode node =  dashboardControllers.checkRequest(request);
        if(node == null)
            return ok(Json.newObject().putPOJO(result,new Response(false,-1,ResponseMessage.PARAMETERS_ERROR.toString())));
        Content content = new ObjectMapper().convertValue(node, Content.class);
        Content res = content.findById(content.getId());
        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),res)));
    }
}
