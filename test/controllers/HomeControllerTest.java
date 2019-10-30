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
        String token = Helper.getToken(result);
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


    public String getAnyToken() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("username", "admin");
        dataNode.put("password", "admin");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        String token = Helper.getToken(result);
        System.out.println(token);
        assertEquals(OK, result.status());
        return token;
    }

}
