package controllers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.util.JSON;
import org.bson.Document;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;
import java.io.IOException;


public class Helper {

    private static String URL = "http://localhost:1900/api";

    //also check for role cases;
    public static ObjectNode objectNodeWithPublicWriteAccess() {
        ObjectNode dataNode = Json.newObject();
        ArrayNode writeACL = Json.newArray();
        writeACL.add("*");
        dataNode.set("writeACL", writeACL);
        return dataNode;
    }


    /* Returns the result of the authentication of the previously added user */
    public static Http.RequestBuilder authUser() {
        ObjectNode userNode = Helper.objectNodeWithPublicWriteAccess();
        userNode.put("username", "admin");
        userNode.put("password", "admin");
        return buildRequest("POST",userNode,"authenticate/");
    }

    /* Returns the String token
     * @param result - the result from the authentication*/
    public static String getToken(Result result) {
        String resultStr = play.test.Helpers.contentAsString(result);
        ObjectNode json = (ObjectNode) Json.parse(resultStr);
        System.out.println("RESPONSE: " + json.toString());
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
