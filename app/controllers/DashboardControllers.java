package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import jwt.JwtControllerHelper;
import jwt.VerifiedJwt;
import jwt.filter.Attrs;
import models.Dashboard;
import play.Logger;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.Optional;

import static play.mvc.Controller.request;
import static play.mvc.Results.forbidden;
import static play.mvc.Results.ok;

public class DashboardControllers {
    @Inject
    private JwtControllerHelper jwtControllerHelper;
    @Inject
    private Config config;
    //createDashboard and updateDashboard use the same query;
    public Result createDashboard(Http.Request request) {
        JsonNode node = checkRequest(request);
        ObjectNode result = Json.newObject();
        if(node == null)
            return ok(result.put("status",false));
        ObjectMapper mapper = new ObjectMapper();
        Dashboard dashboard = mapper.convertValue(node, Dashboard.class);
        dashboard.save();
        result.put("status", true);
        result.put("status", dashboard.getId());
        return ok(result);
    }


    public Result deleteDashboard(Http.Request request)  {
        JsonNode node = checkRequest(request);
        ObjectNode result = Json.newObject();
        if(node == null)
            return ok(result.put("status",false));
        ObjectMapper mapper = new ObjectMapper();
        Dashboard dashboard = mapper.convertValue(node, Dashboard.class);
        dashboard.delete();
        result.put("status", true);
        result.put("result", dashboard.toString());
        return ok(result);
    }

    public Result getDashboard(Http.Request request)  {
        JsonNode node = checkRequest(request);
        ObjectNode result = Json.newObject();
        if(node == null)
            return ok(result.put("status",false));
        ObjectMapper mapper = new ObjectMapper();
        Dashboard dashboard = mapper.convertValue(node, Dashboard.class);
        dashboard.finds();
        result.put("status", true);
        result.put("result", dashboard.toString());
        return ok(result);
    }

    private JsonNode checkRequest(Http.Request request){
        if (request.body().toString().isEmpty()) {
            Logger.of("json body is null");
            return null;
        }
        JsonNode node = request.body().asJson();
        return node;
    }

    public Result requiresJwt() {
        return jwtControllerHelper.verify(request(), res -> {
            if (res.left.isPresent()) {
                return forbidden(res.left.get().toString());
            }

            VerifiedJwt verifiedJwt = res.right.get();
            Logger.debug("{}", verifiedJwt);

            ObjectNode result = Json.newObject();
            result.put("access", "granted");
            result.put("secret_data", "birds fly");
            return ok(result);
        });
    }

    public Result requiresJwtViaFilter() {
        Optional<VerifiedJwt> oVerifiedJwt = request().attrs().getOptional(Attrs.VERIFIED_JWT);
        return oVerifiedJwt.map(jwt -> {
            Logger.debug(jwt.toString());
            return ok("access granted via filter");
        }).orElse(forbidden("eh, no verified jwt found"));
    }
}
