package controllers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.util.JSON;
import mongo.MongoDB;
import org.bson.Document;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import play.Application;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;
import play.twirl.api.Content;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.POST;
import static play.test.Helpers.route;
import static utils.Constants.BEARER;

public class Helper {

    private static String URL = "http://localhost:1900/api";

    public static ObjectNode contentBuilder() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String dataContent = "{\n" +
                "  \"dashboardId\": \"5db83b638f1ed921046a6deb\",\n" +
                "  \"content\": [{\n" +
                "    \"text\": \"FromTets\",\n" +
                "    \"subject\": \"Test purposes\",\n" +
                "    \"email\": \"test@tests.test\",\n" +
                "    \"type\": \"EMAIL\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"type\": \"TEXT\",\n" +
                "    \"text\": \"these data are tests only\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"type\": \"IMAGE\",\n" +
                "    \"url\": \"www.googl.com/updateFromTests\"\n" +
                "  },\n" +
                "  {\n" +
                "    \"type\": \"LINE\",\n" +
                "    \"data\": [{\"category\": \"yo\", \"value\": 23}, {\"category\": \"yo2\", \"value\": 23}, {\"category\": \"yo3\", \"value\": 23}]\n" +
                "  }]\n" +
                "}";
        return (ObjectNode) mapper.readValue(dataContent, JsonNode.class);
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
