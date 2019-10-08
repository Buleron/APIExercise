package controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.typesafe.config.Config;
import jwt.JwtControllerHelper;
import models.User;
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

    public Result authenticate(Http.Request request) throws UnsupportedEncodingException {
        if (request.body().toString().isEmpty()) {
            Logger.of("json body is null");
            return forbidden();
        }

        JsonNode node = request.body().asJson();
        ObjectMapper mapper = new ObjectMapper();
        User user = mapper.convertValue(node, User.class);
        ObjectNode result = Json.newObject();

        if (user.getUsername().isEmpty() && user.getPassword().isEmpty()) {
            result.put("status", false);
            result.put("result", "Missing parameters");
            return ok(result);
        }
        //check if user exists with username and password;
        boolean out = user.auth(user.getUsername(), user.getPassword());
        if (out) {
            result.put("status", true);
            result.put("token", getSignedToken(user.getUsername()));
            return ok(result);

        } else {
            result.put("status", false);
            result.put("result", "Wrong username or password");
            return ok(result);
        }
    }

    private String getSignedToken(String username) throws UnsupportedEncodingException {
        String secret = config.getString("play.http.secret.key");
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.create()
                .withIssuer("excerciseApi")
                .withClaim("user_id", username)
                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusMinutes(10).toInstant()))
                .sign(algorithm);
    }
}
