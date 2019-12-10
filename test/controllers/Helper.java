package controllers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.util.JSON;
import models.collection.content.*;
import models.enums.ContentType;
import mongo.MongoDB;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import play.Application;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.twirl.api.Content;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.POST;
import static play.test.Helpers.route;
import static utils.Constants.BEARER;

public class Helper {

    private static String URL = "http://localhost:1900/api";

    public static ObjectNode contentBuilder() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        DashboardContent dashboardContent = new DashboardContent();
        dashboardContent.setDashboardId(new ObjectId("5db83b638f1ed921046a6deb"));
        // Email
        EmailContent emailContent = new EmailContent();
        emailContent.setEmail("buleroni22@gmail.com");
        emailContent.setSubject("test");
        emailContent.setText("hello");
        emailContent.setType(ContentType.EMAIL);
        // Image
        ImageContent imageContent = new ImageContent();
        imageContent.setUrl("www.google.com/updateFromTests");
        // Text
        TextContent textContent = new TextContent();
        textContent.setText("This is just test purposes");
        // Content
        LineContent lineContent = new LineContent();

        DataContent dataContent1 = new DataContent();
        dataContent1.setName("yo2");
        dataContent1.setValue(23);

        DataContent dataContent = new DataContent();
        dataContent.setName("yo2");
        dataContent.setValue(23);

        List<DataContent> dataContentList = new ArrayList<>();
        dataContentList.add(dataContent);
        dataContentList.add(dataContent1);

        lineContent.setData(dataContentList);
        List<BaseContent> baseContentList = new ArrayList<>();
        baseContentList.add(emailContent);
        baseContentList.add(imageContent);
        baseContentList.add(lineContent);
        baseContentList.add(textContent);
        dashboardContent.setContent(baseContentList);

        return mapper.valueToTree(dashboardContent);
    }

    //also check for role cases;
    public static ObjectNode objectNodeWithPublicWriteAccess() {
        ObjectNode dataNode = Json.newObject();
        ArrayNode writeACL = Json.newArray();
        writeACL.add("*");
        dataNode.set("writeACL", writeACL);
        return dataNode;
    }


    /* Returns the result of the authentication of the previously added user */
    public static String authUser(Application app) {
        ObjectNode userNode = Json.newObject();
        userNode.put("username", "admin");
        userNode.put("password", "admin");
        Http.RequestBuilder request =  buildRequest(POST,userNode,"/authenticate/");
        Result result = route(app, request);
        String token = Helper.getToken(result);
        return  BEARER+token;
    }

    /* Returns the String token
     * @param result - the result from the authentication*/
    public static String getToken(Result result) {
        String resultStr = play.test.Helpers.contentAsString(result);
        ObjectNode json = (ObjectNode) Json.parse(resultStr);
        return json.get("token").asText();
    }

    /* Returns a RequestBuilder based on the parameters
     * @param method - it can be one of the CRUD commands
     * @param uri - The uri to the controller*/
    public static Http.RequestBuilder buildRequest(String method, String uri) {
        return new Http.RequestBuilder().method(method).uri(URL + uri);

    }

    /* Returns a RequestBuilder based on the parameters
     * @param method - it can be one of the CRUD commands
     * @param header - the token
     * @param uri - The uri to the controller*/
    public static Http.RequestBuilder buildRequest(String method, String header, String uri) {
        return new Http.RequestBuilder().method(method).header("Authorization", header).uri(URL + uri);

    }

    /*Returns a RequestBuilde based on the parameters
     * @param method - it can be one of the CRUD commands
     * @param json = the json ObjectNode
     * @param uri - The uri to the controller*/
    public static Http.RequestBuilder buildRequest(String method, ObjectNode json, String uri) {
        return new Http.RequestBuilder().method(method).bodyJson(json).uri(URL + uri);

    }
    /*Returns a RequestBuilde based on the parameters
     * @param method - it can be one of the CRUD commands
     * @param header - the token
     * @param json = the json ObjectNode
     * @param uri - The uri to the controller*/
    public static Http.RequestBuilder buildRequest(String method, String header, ObjectNode json, String uri) {
        return new Http.RequestBuilder().method(method).header("Authorization", header).bodyJson(json)
                .uri(URL + uri);
    }
    /*Returns a RequestBuilde based on the parameters
     * @param method - it can be one of the CRUD commands
     * @param header - the token
     * @param json = the json ArrayNode
     * @param uri - The uri to the controller*/
    public static Http.RequestBuilder buildRequest(String method, String header, ArrayNode json, String uri) {
        return new Http.RequestBuilder().method(method).header("Authorization", header).bodyJson(json)
                .uri(URL + uri);
    }
    /* Returns a JsonNode
     * @param document - The Mongo document to convert into JsonNode*/
    public static JsonNode toJsonNode(Document document) {
        if (document == null) {
            return null;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
        ObjectReader reader = mapper.readerFor(JsonNode.class);
        try {
            return reader.readValue(JSON.serialize(document));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
