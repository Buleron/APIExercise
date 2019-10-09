package controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Lists;
import models.Content;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static play.mvc.Results.ok;


public class ContentController {

     private DashboardControllers dashboardControllers = new DashboardControllers();

    public Result createContent(Http.Request request) throws JsonProcessingException {
        JsonNode node = dashboardControllers.checkRequest(request);
        ObjectNode result = Json.newObject();
        if(node == null)
            return ok(result.put("status",false));
        ObjectMapper mapper = new ObjectMapper();

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        Content content =  mapper.convertValue(node, Content.class);
        String uniqueID = UUID.randomUUID().toString();

       // content.setId(uniqueID);
        //content.save();
        result.put("status", true);
        result.put("status", content.toString());
        return ok(result);
    }

    public Result updateContent(Http.Request request) {
        JsonNode node = dashboardControllers.checkRequest(request);
        ObjectNode result = Json.newObject();
        if(node == null)
            return ok(result.put("status",false));
        ObjectMapper mapper = new ObjectMapper();
        Content content = mapper.convertValue(node, Content.class);
        content.save();
        result.put("status", true);
        result.put("status", content.toString());
        return ok(result);
    }

    public Result deleteContent(Http.Request request)  {
        JsonNode node = dashboardControllers.checkRequest(request);
        ObjectNode result = Json.newObject();
        if(node == null)
            return ok(result.put("status",false));
        ObjectMapper mapper = new ObjectMapper();
        Content content = mapper.convertValue(node, Content.class);
        content.delete();
        result.put("status", true);
        result.put("result", content.toString());
        return ok(result);
    }

    public Result getContent(Http.Request request)  {
        JsonNode node =  dashboardControllers.checkRequest(request);
        ObjectNode result = Json.newObject();
        if(node == null)
            return ok(result.put("status",false));
        ObjectMapper mapper = new ObjectMapper();
        Content content = mapper.convertValue(node, Content.class);
        content.finds();
        result.put("status", true);

        result.put("result", content.toString());
        return ok(result);
    }
}
