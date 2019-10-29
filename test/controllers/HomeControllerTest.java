package controllers;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class HomeControllerTest extends WithApplication {

    private static String AuthURL = "http://localhost:1900/api/authenticate/";

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }


    public static ObjectNode objectNodeWithPublicWriteAccess() {
        ObjectNode dataNode = Json.newObject();
        ArrayNode writeACL = Json.newArray();
        writeACL.add("*");
        dataNode.set("writeACL", writeACL);
        return dataNode;
    }

    @Test
    public void authenticateSuccessfully() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("username", "admin");
        dataNode.put("password", "admin");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        String token = getTokenFromResult(result);
        System.out.println(token);
        assertEquals(OK, result.status());
    }

    @Test
    public void authenticateWrongUsernamePassword() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("username", "asasdasdd");
        dataNode.put("password", "asdasdasdasd");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    public static String getTokenFromResult(Result result) {
        String resultStr = play.test.Helpers.contentAsString(result);
        ObjectNode json = (ObjectNode) Json.parse(resultStr);
        System.out.println("RESPONSE: " + json.toString());
        String token = json.get("token").asText();
        return token;

    }

    public String getAnyToken() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("username", "admin");
        dataNode.put("password", "admin");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        String token = getTokenFromResult(result);
        System.out.println(token);
        assertEquals(OK, result.status());
        return token;
    }

}
