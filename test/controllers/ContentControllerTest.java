package controllers;

import Interfaces.IContent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.collection.Content;
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
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;
import static utils.Constants.BEARER;
import static utils.Constants.WRONG_TOKEN;

public class ContentControllerTest {

    private static String AuthURL = "/content/";
    private static String token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNWRhZGEwM2VlZTVjZDkwYjljN2NjOTM2IiwiaXNzIjoiZXhjZXJjaXNlQXBpIiwiZXhwIjoxODg3OTczNDM5fQ.hnS0cK8V4341gfNIcUEkGfn7ysKvIBBtem_Mu0R5UWA";
    private static final String BEARER_Token = BEARER + token;
    private static final String WrongToken = BEARER +"asdasdjasdhjaisdfhjknxczlzidhkjvnc.sodifhjsdf.sdfhksdl";
    private static Application app;
    private Content content;

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
    public void createContentOK() throws IOException {
        ObjectNode dataNode = Json.newObject();
//        dataNode.put("name", "createName from tests");
//        dataNode.put("description", "description created from tests");
//        dataNode.put("parentId", "5dada6f7ee5cd920804cdb31");
        Http.RequestBuilder request = Helper.buildRequest(POST, BEARER_Token, dataNode, AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readValue(resultStr, JsonNode.class);
        content = DatabaseUtils.jsonToJavaClass(actualObj, Content.class);
        System.out.println(content);
        assertEquals(OK, result.status());

    }

    @Test
    public void createContentAuthorizationMissing() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(POST, dataNode, AuthURL);
        Result result = route(app, request);
        System.out.println(result);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void createContentMissingBodyRequest() {
        Http.RequestBuilder request = Helper.buildRequest(POST, BEARER_Token, AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void createContentFakeAuthorization() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(POST, WrongToken, dataNode, AuthURL);
        Result result = route(app, request);
        System.out.println(result);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void updateContentMissingBodyRequest() {
        Http.RequestBuilder request = Helper.buildRequest(PUT, BEARER_Token, AuthURL);
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void updateContentFakeAuthorization() {
        ObjectNode dataNode = Json.newObject();
//        dataNode.put("name", "UPDATED FROM TEST");
//        dataNode.put("description", "UPDATED From tests yap updated again");
//        dataNode.put("parentId", "5dada6f7ee5cd920804cdb31");
//        dataNode.put("_id", "5dada6f7ee5cd920804cdb31");
        Http.RequestBuilder request = Helper.buildRequest(PUT, WRONG_TOKEN, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void updateContentAuthorizationMissing() {
        ObjectNode dataNode = Json.newObject();
//        dataNode.put("name", "UPDATED FROM TEST");
//        dataNode.put("description", "UPDATED From tests yap updated again");
//        dataNode.put("parentId", "5dada6f7ee5cd920804cdb31");
//        dataNode.put("_id", "5dada6f7ee5cd920804cdb31");
        Http.RequestBuilder request = Helper.buildRequest(PUT, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void updateContentOK() {
        ObjectNode dataNode = Json.newObject();
//        dataNode.put("name", content.getContent() + " updated");
//        dataNode.put("description", "dashboard.getDescription()" + " Updated");
//        dataNode.put("parentId", "dashboard.getParentId()");
//        dataNode.put("_id", "dashboard.getId().toString()");
        Http.RequestBuilder request = Helper.buildRequest(PUT, BEARER_Token, dataNode, AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void getContentByIdFakeAuthorization() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(GET, WRONG_TOKEN, dataNode, AuthURL+ "5dada6f7ee5cd920804cdb31");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void getContentByIdAuthorizationMissing() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(GET, dataNode, AuthURL+ "5dada6f7ee5cd920804cdb31");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void getContentByIdOK() {
        Http.RequestBuilder request = Helper.buildRequest(GET, BEARER_Token, AuthURL+ content.getId().toString());
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void ContentGetAllOK() {
        Http.RequestBuilder request = Helper.buildRequest(GET, BEARER_Token, AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void ContentGetAllFakeAuthorization() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(GET, WrongToken,dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void ContentGetAllAuthorizationMissing() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(GET,dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void deleteContentWithUnknownResource() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("_id", "amassed");
        Http.RequestBuilder request = Helper.buildRequest(DELETE, BEARER_Token,dataNode, AuthURL+"029waistbandsAkaUKA");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void deleteContentFakeAuthorization() {
        Http.RequestBuilder request = Helper.buildRequest(DELETE, WrongToken, AuthURL+"029waistbandsAkaUKA");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void deleteContentAuthorizationMissing() {
        Http.RequestBuilder request = Helper.buildRequest(DELETE,AuthURL+"029waistbandsAkaUKA");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void deleteContentResultOK() {
        Http.RequestBuilder request = Helper.buildRequest(DELETE, BEARER_Token, AuthURL+ content.getId().toString());
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void deleteContentWithResourceNotFound() {
        Http.RequestBuilder request = Helper.buildRequest(DELETE, BEARER_Token, AuthURL+ content.getId().toString());
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(NOT_FOUND, result.status());
    }
}
