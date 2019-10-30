package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.collection.Dashboard;
import mongo.MongoDB;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Application;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import utils.DatabaseUtils;
import java.io.IOException;
import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;
import static utils.Constants.BEARER;


public class DashboardControllerTest {

    private static String AuthURL = "/dashboard/";
    private static String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNWRhZGEwM2VlZTVjZDkwYjljN2NjOTM2IiwiaXNzIjoiZXhjZXJjaXNlQXBpIiwiZXhwIjoxODg3OTczNDM5fQ.hnS0cK8V4341gfNIcUEkGfn7ysKvIBBtem_Mu0R5UWA";
    private static final String BEARER_Token = BEARER + token;
    private static final String WrongToken = "asdasdjasdhjaisdfhjknxczlzidhkjvnc.sodifhjsdf.sdfhksdl";
    private static Application app;
    private Dashboard dashboard;


    @BeforeClass
    public static void startPlay() {
        app = Helpers.fakeApplication();
        Helpers.start(app);
        MongoDB mongoDB = app.injector().instanceOf(MongoDB.class);
    }

    @AfterClass
    public static void stopPlay() {
        if (app != null) {
            Helpers.stop(app);
            app = null;
        }
    }

    @Test
    public void createDashboardOK() throws IOException {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("name", "createName from tests");
        dataNode.put("description", "description created from tests");
        dataNode.put("parentId", "5dada6f7ee5cd920804cdb31");

        Http.RequestBuilder request = Helper.buildRequest(POST, BEARER_Token, dataNode, AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readValue(resultStr, JsonNode.class);
        dashboard = DatabaseUtils.jsonToJavaClass(actualObj, Dashboard.class);
        System.out.println(dashboard);
        assertEquals(OK, result.status());
    }

    @Test
    public void createDashboardAuthorizationMissing() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(POST, dataNode, AuthURL);
        Result result = route(app, request);
        System.out.println(result);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void createDashboardMissingBodyRequest() {
        Http.RequestBuilder request = Helper.buildRequest(POST, BEARER_Token, AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void createDashboardFakeAuthorization() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(POST, BEARER+WrongToken, dataNode, AuthURL);
        Result result = route(app, request);
        System.out.println(result);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void updateDashboardMissingBodyRequest() {
        Http.RequestBuilder request = Helper.buildRequest(PUT, BEARER_Token, AuthURL);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void updateDashboardFakeAuthorization() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("name", "UPDATED FROM TEST");
        dataNode.put("description", "UPDATED From tests yap updated again");
        dataNode.put("parentId", "5dada6f7ee5cd920804cdb31");
        dataNode.put("_id", "5dada6f7ee5cd920804cdb31");
        Http.RequestBuilder request = Helper.buildRequest(PUT, BEARER+WrongToken, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void updateDashboardAuthorizationMissing() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("name", "UPDATED FROM TEST");
        dataNode.put("description", "UPDATED From tests yap updated again");
        dataNode.put("parentId", "5dada6f7ee5cd920804cdb31");
        dataNode.put("_id", "5dada6f7ee5cd920804cdb31");
        Http.RequestBuilder request = Helper.buildRequest(PUT, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void updateDashboardOK() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("name", dashboard.getName() + " updated");
        dataNode.put("description", dashboard.getDescription() + " updated");
        dataNode.put("parentId", dashboard.getParentId());
        dataNode.put("_id", dashboard.getId().toString());

        Http.RequestBuilder request = Helper.buildRequest(PUT, BEARER_Token, dataNode, AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void getDashboardByIdFakeAuthorization() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(GET, BEARER+WrongToken, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void getDashboardByIdAuthorizationMissing() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(GET, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void getDashboardByIdOK() {
        Http.RequestBuilder request = Helper.buildRequest(GET, BEARER_Token, AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void dashboardGetAllOK() {
        Http.RequestBuilder request = Helper.buildRequest(GET, BEARER_Token, AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void dashboardGetAllFakeAuthorization() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(GET, BEARER+WrongToken, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void dashboardGetAllAuthorizationMissing() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(GET, dataNode, AuthURL);

        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void deleteDashboardWithUnknownResource() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("_id", "amassed");
        Http.RequestBuilder request = Helper.buildRequest(DELETE, BEARER_Token, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void deleteDashboardFakeAuthorization() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("_id", "5dada734ee5cd920804cdb32");
        Http.RequestBuilder request = Helper.buildRequest(DELETE, BEARER+WrongToken, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void deleteDashboardAuthorizationMissing() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("_id", "5dada734ee5cd920804cdb32");
        Http.RequestBuilder request = Helper.buildRequest(DELETE, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void deleteDashboardResultOK() {
        Http.RequestBuilder request = Helper.buildRequest(DELETE, BEARER_Token, AuthURL+dashboard.getId().toString());
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void deleteDashboardWithResourceNotFound() {
        Http.RequestBuilder request = Helper.buildRequest(DELETE, BEARER_Token, AuthURL + "5dada6f7ee5cd920804cdb31");
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(NOT_FOUND, result.status());
    }
}
