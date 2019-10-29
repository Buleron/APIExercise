package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.collection.Dashboard;
import org.apache.http.Header;
import play.test.WithApplication;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;
import utils.DatabaseUtils;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class DashboardControllerTest extends WithApplication {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static String AuthURL = "http://localhost:1900/api/dashboard/";
    private static String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNWRhZGEwM2VlZTVjZDkwYjljN2NjOTM2IiwiaXNzIjoiZXhjZXJjaXNlQXBpIiwiZXhwIjoxODg3OTczNDM5fQ.hnS0cK8V4341gfNIcUEkGfn7ysKvIBBtem_Mu0R5UWA";
    private static final String BEARER_Token = BEARER+token;
    private static final String WrongToken = "asdasdjasdhjaisdfhjknxczlzidhkjvnc.sodifhjsdf.sdfhksdl";

    @Override
    protected Application provideApplication() {
        return new GuiceApplicationBuilder().build();
    }
    public static ObjectNode objectNodeWithPublicWriteAccess () {
        ObjectNode dataNode = Json.newObject();
        ArrayNode writeACL = Json.newArray();
        writeACL.add("*");
        dataNode.set("writeACL", writeACL);
        return dataNode;
    }

//    public static String BuildResult(Result result) {
//        String resultStr = play.test.Helpers.contentAsString(result);
//        if(!resultStr.contains("ERR_AUTHORIZATION_HEADER")){
//            ObjectNode json = (ObjectNode) Json.parse(resultStr);
//            System.out.println("RESPONSE: " + json.toString());
//            String token = json.get("token").asText();
//            return token;
//        } else {
//            return resultStr;
//        }
//    }

    @Test
    public void createDashboard_OK() throws IOException {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("name", "asasdasdd");
        dataNode.put("description", "asdasdasdasd");
        dataNode.put("parentId", "5dada6f7ee5cd920804cdb31");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("Content-Type","application/json")
                .header(AUTHORIZATION,BEARER_Token)
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readValue(resultStr, JsonNode.class);
        Dashboard dashboard = DatabaseUtils.jsonToJavaClass(actualObj, Dashboard.class);
        System.out.println(dashboard);
        assertEquals(OK, result.status());
    }

    @Test
    public void createDashboard_MissingToken() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("Content-Type","application/json")
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        System.out.println(result);
        assertEquals(FORBIDDEN, result.status());
    }
    @Test
    public void createDashboard_BadRequest()  {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("Content-Type","application/json")
                .header(AUTHORIZATION,BEARER_Token)
                .uri(AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void createDashboard_WrongToken() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("Content-Type","application/json")
                .header(AUTHORIZATION,BEARER+WrongToken)
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        System.out.println(result);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void updateDashboard() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("name", "UPDATED FROM TEST");
        dataNode.put("description", "UPDATED From tests");
        dataNode.put("parentId", "5dada6f7ee5cd920804cdb31");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .header("Content-Type","application/json")
                .header(AUTHORIZATION,BEARER_Token)
                .bodyJson(dataNode)
                .uri(AuthURL+"5dada6f7ee5cd920804cdb31");
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void getDashboardById() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("Content-Type","application/json")
                .header(AUTHORIZATION,BEARER_Token)
                .bodyJson(dataNode)
                .uri(AuthURL+"5dada6f7ee5cd920804cdb31");
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void dashboardGetAll() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("Content-Type","application/json")
                .header(AUTHORIZATION,BEARER_Token)
                .bodyJson(dataNode)
                .uri(AuthURL);

        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void deleteDashboard() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("_id", "amassed");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .bodyJson(dataNode)
                .header(AUTHORIZATION,BEARER_Token)
                .uri(AuthURL);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }
}
