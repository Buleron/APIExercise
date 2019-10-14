package controllers;

import com.typesafe.config.Config;
//import jwt.JwtControllerHelper;
import play.mvc.Controller;

import javax.inject.Inject;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {
    @Inject
//    private JwtControllerHelper jwtControllerHelper;
//    @Inject
    private Config config;
    private DashboardControllers dashboardControllers = new DashboardControllers();
    private static final String result = "result";

//    public Result authenticate(Http.Request request) throws UnsupportedEncodingException {
//        JsonNode node = dashboardControllers.checkRequest(request);
//        if (node == null)
//            return ok(Json.newObject().putPOJO(result, new Response(false, -1, ResponseMessage.PARAMETERS_ERROR.toString())));
//        User user = new ObjectMapper().convertValue(node, User.class);
//        if (user.getUsername().isEmpty() && user.getPassword().isEmpty()) {
//            return ok(Json.newObject().putPOJO(result, new Response(false, -1, ResponseMessage.MISSING_PARAMETERS.toString())));
//        }
//        //check if user exists with username and password;
//        boolean out = user.auth(user.getUsername(), user.getPassword());
//        if (out)
//            return ok(Json.newObject().putPOJO("token", getSignedToken(user.getUsername())));
//        return ok(Json.newObject().putPOJO(result, new Response(false, -1, ResponseMessage.WRONG_CREDENTIAL.toString())));
//    }
//
//    private String getSignedToken(String username) throws UnsupportedEncodingException {
//        String secret = config.getString("play.http.secret.key");
//        Algorithm algorithm = Algorithm.HMAC256(secret);
//        return JWT.create()
//                .withIssuer("excerciseApi")
//                .withClaim("user_id", username)
//                .withExpiresAt(Date.from(ZonedDateTime.now(ZoneId.systemDefault()).plusYears(10).toInstant()))
//                .sign(algorithm);
//    }
}
