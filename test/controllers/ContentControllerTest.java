package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.client.MongoCollection;
import models.collection.content.DashboardContent;
import mongo.MongoDB;
import org.bson.Document;
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
import static play.mvc.Http.Status.NOT_FOUND;
import static play.test.Helpers.*;
import static utils.Constants.*;

public class ContentControllerTest {

    private static String AuthURL = "/dashboard/content/";
    private static String BEARER_Token = null;
    private static final String WrongToken = BEARER + "asdasdeyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1c2VyX2lkIjoiNWRhZGEwM2VlZTVjZDkwYjljN2NjOTM2IiwiaXNzIjoiZXhjZXJjaXNlQXBpIiwiZXhwIjoxODg3OTczNDM5fQ.hnS0cK8hksdl";
    private static Application app;
    private static MongoDB mongoDB;
    private static ObjectNode JsonNodeDataContent;
    private static DashboardContent dashboardContentHeader;


    @BeforeClass
    public static void startPlay() throws IOException {
        app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
        Helpers.start(app);
        BEARER_Token = Helper.authUser(app);
        JsonNodeDataContent = Helper.contentBuilder();
        mongoDB = app.injector().instanceOf(MongoDB.class);
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
        ObjectMapper mapper = new ObjectMapper();
        Http.RequestBuilder request = Helper.buildRequest(POST, BEARER_Token, JsonNodeDataContent, AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        JsonNode actualObj = mapper.readValue(resultStr, JsonNode.class);
        DashboardContent dashboardContent = DatabaseUtils.jsonToJavaClass(actualObj, DashboardContent.class);
        dashboardContentHeader = dashboardContent;
        System.out.println(dashboardContent);
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
        assertEquals(UNSUPPORTED_MEDIA_TYPE, result.status());
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
        assertEquals(UNSUPPORTED_MEDIA_TYPE, result.status());
    }

    @Test
    public void updateContentFakeAuthorization() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(PUT, WRONG_TOKEN, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void updateContentAuthorizationMissing() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(PUT, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void updateContentOK() {
        MongoCollection<Document> content = mongoDB.getDatabase().getCollection(CONTENT, Document.class);
        Document doc = content.find().first();
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode jsonNodeDataContent;
        jsonNodeDataContent = mapper.convertValue(doc,ObjectNode.class);
        Http.RequestBuilder request = Helper.buildRequest(PUT, BEARER_Token, jsonNodeDataContent, AuthURL);
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void getContentByIdFakeAuthorization() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(GET, WRONG_TOKEN, dataNode, AuthURL + "5dada6f7ee5cd920804cdb31");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void getContentByIdAuthorizationMissing() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(GET, dataNode, AuthURL + "5dada6f7ee5cd920804cdb31");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void getContentByIdOK() {
        MongoCollection<DashboardContent> content = mongoDB.getDatabase().getCollection(CONTENT, DashboardContent.class);
        DashboardContent doc = content.find().first();
        Http.RequestBuilder request = Helper.buildRequest(GET, BEARER_Token, "/dashboard/"+doc.getDashboardId().toString()+"/content/" + doc.getId().toString());
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void ContentGetAllOK() {
        Http.RequestBuilder request = Helper.buildRequest(GET, BEARER_Token, "/dashboard/"+dashboardContentHeader.getDashboardId().toString()+"/content/");
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void ContentGetAllFakeAuthorization() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(GET, WrongToken, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void ContentGetAllAuthorizationMissing() {
        ObjectNode dataNode = Json.newObject();
        Http.RequestBuilder request = Helper.buildRequest(GET, dataNode, AuthURL);
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void deleteContentWithUnknownResource() {
        ObjectNode dataNode = Json.newObject();
        dataNode.put("_id", "amassed");
        Http.RequestBuilder request = Helper.buildRequest(DELETE, BEARER_Token, dataNode, AuthURL + "029waistbandsAkaUKA");
        Result result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void deleteContentFakeAuthorization() {
        Http.RequestBuilder request = Helper.buildRequest(DELETE, WrongToken, AuthURL + "029waistbandsAkaUKA");
        Result result = route(app, request);
        assertEquals(UNAUTHORIZED, result.status());
    }

    @Test
    public void deleteContentAuthorizationMissing() {
        Http.RequestBuilder request = Helper.buildRequest(DELETE, AuthURL + "029waistbandsAkaUKA");
        Result result = route(app, request);
        assertEquals(FORBIDDEN, result.status());
    }

    @Test
    public void deleteContentResultOK() {
        Http.RequestBuilder request = Helper.buildRequest(DELETE, BEARER_Token, AuthURL + dashboardContentHeader.getId().toString());
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(OK, result.status());
    }

    @Test
    public void deleteContentWithResourceNotFound() {
        Http.RequestBuilder request = Helper.buildRequest(DELETE, BEARER_Token, AuthURL + "5daeb79aee5cd92af88b7c9e");
        Result result = route(app, request);
        String resultStr = play.test.Helpers.contentAsString(result);
        System.out.println(resultStr);
        assertEquals(NOT_FOUND, result.status());
    }
}
