package controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import jwt.JwtControllerHelper;
import models.Response;
import models.User;
import models.enums.ResponseMessage;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Http;
import play.mvc.Result;

import javax.inject.Inject;
import java.io.UnsupportedEncodingException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    @Inject
    private JwtControllerHelper jwtControllerHelper;
    @Inject
    private Config config;
    private DashboardControllers dashboardControllers = new DashboardControllers();
    private static final String result = "result";

    public Result authenticate(Http.Request request) throws UnsupportedEncodingException {
        JsonNode node = dashboardControllers.checkRequest(request);
        if (node == null)
            return ok(Json.newObject().putPOJO(result, new Response(false, -1, ResponseMessage.PARAMETERS_ERROR.toString())));
        User user = new ObjectMapper().convertValue(node, User.class);
        if (user.getUsername().isEmpty() && user.getPassword().isEmpty()) {
            return ok(Json.newObject().putPOJO(result, new Response(false, -1, ResponseMessage.MISSING_PARAMETERS.toString())));
        }
        //check if user exists with username and password;
        boolean out = user.auth(user.getUsername(), user.getPassword());
        if (out)
            return ok(Json.newObject().putPOJO("token", getSignedToken(user.getUsername())));
        return ok(Json.newObject().putPOJO(result, new Response(false, -1, ResponseMessage.WRONG_CREDENTIAL.toString())));
    }

    private String getSignedToken(String username) throws UnsupportedEncodingException {
        String secret = config.getString("play.http.secret.key");
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("excerciseApi")
                .withClaim("user_id", username)
                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusYears(10).toInstant()))
                .sign(algorithm);
    }
}
