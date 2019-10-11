package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.WriteResult;
import models.Dashboard;
import models.Response;
import models.enums.ResponseMessage;
import org.mongodb.morphia.Key;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import static play.mvc.Results.ok;

public class DashboardControllers {
    private static final String result = "result";

    public Result createDashboard(Http.Request request) {
        JsonNode node = checkRequest(request);
        if(node == null)
            return ok(Json.newObject().putPOJO(result,new Response(false,-1, ResponseMessage.PARAMETERS_ERROR.toString())));
        Dashboard dashboard = new ObjectMapper().convertValue(node, Dashboard.class);
        dashboard.setId(UUID.randomUUID().toString());
        dashboard.setCreatedAt(new java.sql.Timestamp(Instant.now().toEpochMilli()));
        Key<Dashboard> res = dashboard.save();
        if(res.getId().toString().isEmpty())
            return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.NO_DATA_FOUND.toString())));
        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),dashboard)));
    }

    public Result updateDashboard(Http.Request request) {
        JsonNode node = checkRequest(request);
        if(node == null)
            return ok(Json.newObject().putPOJO(result,new Response(false,-1,ResponseMessage.PARAMETERS_ERROR.toString())));
        Dashboard dashboard = new ObjectMapper().convertValue(node, Dashboard.class);
        Key<Dashboard> res = dashboard.save();
        if(res.getId().toString().isEmpty())
            return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.NO_DATA_FOUND.toString())));
        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),dashboard)));
    }


    public Result deleteDashboard(Http.Request request) {
        JsonNode node = checkRequest(request);
        if (node == null)
            return ok(Json.newObject().putPOJO(result, new Response(false, -1, ResponseMessage.PARAMETERS_ERROR.toString())));
        Dashboard dashboard = new ObjectMapper().convertValue(node, Dashboard.class);
        WriteResult res = dashboard.deleteById(dashboard.getId());
        if (res.getN() == 1)
            return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.SUCCESSFULLY.toString(), dashboard)));
        return ok(Json.newObject().putPOJO(result, new Response(true, 0, ResponseMessage.NO_DATA_FOUND.toString())));
    }

    public Result getAllDashboards()  {
        List<Dashboard> dashboards =  new Dashboard().findAll();
        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),dashboards)));
    }
    public Result getDashboardById(Http.Request request) {
        JsonNode node = checkRequest(request);
        if(node == null)
            return ok(Json.newObject().putPOJO(result,new Response(false,-1,ResponseMessage.PARAMETERS_ERROR.toString())));
        Dashboard dashboard = new ObjectMapper().convertValue(node, Dashboard.class);
       Dashboard res = dashboard.findById(dashboard.getId());
        return ok(Json.newObject().putPOJO(result, new Response(true,0,ResponseMessage.SUCCESSFULLY.toString(),res)));
    }


    public JsonNode checkRequest(Http.Request request){
        if (request.body().toString().isEmpty()) {
            Logger.of("json body is null");
            return null;
        }
        JsonNode node = request.body().asJson();
        return node;
    }
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
