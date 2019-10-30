package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import models.collection.Dashboard;
import org.apache.http.Header;
import org.bson.types.ObjectId;
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
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

public class DashboardControllerTest extends WithApplication {
    private static final String AUTHORIZATION = "Authorization";
    private static final String BEARER = "Bearer ";
    private static String AuthURL = "http://localhost:1900/api/dashboard/";
    private static String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNWRhZGEwM2VlZTVjZDkwYjljN2NjOTM2IiwiaXNzIjoiZXhjZXJjaXNlQXBpIiwiZXhwIjoxODg3OTczNDM5fQ.hnS0cK8V4341gfNIcUEkGfn7ysKvIBBtem_Mu0R5UWA";
    private static final String BEARER_Token = BEARER + token;
    private static final String WrongToken = "asdasdjasdhjaisdfhjknxczlzidhkjvnc.sodifhjsdf.sdfhksdl";

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
    public void TestsAll() throws IOException {
        Dashboard dashboard;
        boolean passed;
        passed = createDashboard_MissingToken();
        if (!passed) return;
        passed = createDashboard_BadRequest();
        if (!passed) return;
        passed = createDashboard_WrongToken();
        if (!passed) return;
        dashboard = createDashboard_OK();
        if (dashboard.getId().toString().isEmpty()) {
            System.out.println("Something went wrong while executing tests");
            return;
        }
        passed = updateDashboard_BadRequest();
        if (!passed) return;
        passed = updateDashboard_WrongToken();
        if (!passed) return;
        passed = updateDashboard_OK(dashboard);
        if (!passed) return;


        passed = getDashboardById_BadRequest();
        if (!passed) return;
        passed = getDashboardById_WrongToken();
        if(!passed) return;
        passed = getDashboardById_OK(dashboard.getId().toString());
        if(!passed) return;

        passed = dashboardGetAll();
        if(!passed) return;

        passed = deleteDashboard_BadRequest();
        if(!passed) return;
        passed = deleteDashboard_WrongToken();
        if(!passed) return;
        passed = deleteDashboard_NoToken();
        if(!passed) return;
        passed = deleteDashboard_NotFound();
        if(!passed) return;
        passed = deleteDashboard_OK(dashboard.getId().toString());
        if(!passed) return;

        System.out.println(dashboard);
        assertTrue(passed);
        System.out.println("Successfully with create dashboard");
    }

    private Dashboard createDashboard_OK() throws IOException {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("name", "asasdasdd");
        dataNode.put("description", "asdasdasdasd");
        dataNode.put("parentId", "5dada6f7ee5cd920804cdb31");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION, BEARER_Token)
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readValue(resultStr, JsonNode.class);
        Dashboard dashboard = DatabaseUtils.jsonToJavaClass(actualObj, Dashboard.class);
        System.out.println(dashboard);
        assertEquals(OK, result.status());
        return dashboard;
    }

    private boolean createDashboard_MissingToken() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("Content-Type", "application/json")
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        System.out.println(result);
        assertEquals(FORBIDDEN, result.status());
        return result.status() == FORBIDDEN;
    }

    private boolean createDashboard_BadRequest() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION, BEARER_Token)
                .uri(AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(BAD_REQUEST, result.status());
        return result.status() == BAD_REQUEST;
    }

    private boolean createDashboard_WrongToken() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(POST)
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION, BEARER + WrongToken)
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        System.out.println(result);
        assertEquals(UNAUTHORIZED, result.status());
        return result.status() == UNAUTHORIZED;
    }

    private boolean updateDashboard_BadRequest() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION, BEARER_Token)
                .uri(AuthURL);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
        return result.status() == BAD_REQUEST;
    }

    private boolean updateDashboard_WrongToken() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("name", "UPDATED FROM TEST");
        dataNode.put("description", "UPDATED From tests yeap updated again");
        dataNode.put("parentId", "5dada6f7ee5cd920804cdb31");
        dataNode.put("_id", "5dada6f7ee5cd920804cdb31");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION, BEARER + WrongToken)
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        return result.status() == UNAUTHORIZED;
    }

    private boolean updateDashboard_OK(Dashboard dashboard) {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("name", dashboard.getName() + " updated");
        dataNode.put("description", dashboard.getDescription() + " Updated");
        dataNode.put("parentId", dashboard.getParentId());
        dataNode.put("_id", dashboard.getId().toString());
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(PUT)
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION, BEARER_Token)
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
        return result.status() == OK;
    }

    private boolean getDashboardById_BadRequest() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("Content-Type", "application/json")
                .uri(AuthURL + "5dada6f7ee5cd920804cdb31");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
        return result.status() == FORBIDDEN;
    }

    private boolean getDashboardById_WrongToken() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION, BEARER + WrongToken)
                .bodyJson(dataNode)
                .uri(AuthURL + "5dada6f7ee5cd920804cdb31");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
        return result.status() == UNAUTHORIZED;
    }

    private boolean getDashboardById_OK(String dashboardId) {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION, BEARER_Token)
                .bodyJson(dataNode)
                .uri(AuthURL + dashboardId);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
        return result.status() == OK;
    }

    private boolean dashboardGetAll() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .header("Content-Type", "application/json")
                .header(AUTHORIZATION, BEARER_Token)
                .bodyJson(dataNode)
                .uri(AuthURL);

        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
        return result.status() == OK;
    }

    private boolean deleteDashboard_BadRequest() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("_id", "amassed");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .bodyJson(dataNode)
                .header(AUTHORIZATION, BEARER_Token)
                .uri(AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
        return result.status() == FORBIDDEN;
    }

    private boolean deleteDashboard_WrongToken() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("_id", "5dada734ee5cd920804cdb32");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .bodyJson(dataNode)
                .header(AUTHORIZATION, BEARER + WrongToken)
                .uri(AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
        return result.status() == FORBIDDEN;
    }

    private boolean deleteDashboard_NoToken() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("_id", "5dada734ee5cd920804cdb32");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .bodyJson(dataNode)
                .uri(AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
        return result.status() == FORBIDDEN;
    }

    private boolean deleteDashboard_NotFound() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .header(AUTHORIZATION, BEARER_Token)
                .uri(AuthURL + "5dada6f7ee5cd920804cdb31");
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(NOT_FOUND, result.status());
        return result.status() == NOT_FOUND;
    }

    private boolean deleteDashboard_OK(String dashboardId) {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(DELETE)
                .header(AUTHORIZATION, BEARER_Token)
                .uri(AuthURL + dashboardId);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
        return result.status() == OK;
    }
}
