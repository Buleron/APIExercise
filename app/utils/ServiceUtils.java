package utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.exceptions.RequestException;
import oauth2.PlatformAttributes;
import org.bson.Document;
import play.libs.concurrent.HttpExecutionContext;
import play.mvc.Http;
import play.mvc.Http.Request;

import javax.xml.ws.spi.http.HttpContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Created by Agon on 11/22/2016.
 */
public class ServiceUtils {

    public static <T> CompletableFuture<JsonNode> toJsonNode(List<T> result) {
        CompletableFuture<JsonNode> promise = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                promise.complete(DatabaseUtils.toJsonNode(result));
            } catch (Exception ex) {
                ex.printStackTrace();
                promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "parsing_exception"));
            }
        });
        return promise;
    }

    public static <T> CompletableFuture<JsonNode> toJsonNode(CompletableFuture<T> future) {
        CompletableFuture<JsonNode> promise = new CompletableFuture<>();

        future.thenApply((result) -> {
            try {
                JsonNode node = null;
                if (result instanceof List<?>) {
                    node = DatabaseUtils.toJsonNode((List<?>) result);
                } else {
                    node = DatabaseUtils.toJsonNode(result);
                }

                if (node == null) {
                    promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "parsing_exception"));
                } else {
                    promise.complete(node);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "parsing_exception"));
            }
            return result;
        });

        future.exceptionally((error) -> {
            promise.completeExceptionally(error);
            return null;
        });
        return promise;
    }

    public static <T> CompletableFuture<JsonNode> toJsonNode(T result) {
        CompletableFuture<JsonNode> promise = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                JsonNode node = null;
                if (result instanceof List<?>) {
                    node = DatabaseUtils.toJsonNode((List<?>) result);
                } else {
                    node = DatabaseUtils.toJsonNode(result);
                }
                if (node == null) {
                    promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "parsing_exception"));
                } else {
                    promise.complete(node);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "parsing_exception"));
            }
        });
        return promise;
    }

    public static <T> CompletableFuture<JsonNode> toJsonNode(T result, Executor context) {
        CompletableFuture<JsonNode> promise = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                JsonNode node = null;
                if (result instanceof List<?>) {
                    node = DatabaseUtils.toJsonNode((List<?>) result);
                } else {
                    node = DatabaseUtils.toJsonNode(result);
                }
                if (node == null) {
                    promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "parsing_exception"));
                } else {
                    promise.complete(node);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "parsing_exception"));
            }
        }, context);
        return promise;
    }


    public static CompletableFuture<Document> parseBody(Http.RequestBody request, Executor context) {
        CompletableFuture<Document> promise = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            System.out.println(context);
            JsonNode json = request.asJson();
            if (!json.isObject()) {
                promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "invalid_parameters"));
                return;
            }
            promise.complete(DatabaseUtils.toDocument((ObjectNode) json));
        }, context);
        return promise;
    }

    public static <T> CompletableFuture<T> parseBodyOfType(Http.RequestBody request,Executor ex, Class<T> valueType) {
        CompletableFuture<T> promise = new CompletableFuture<T>();
        CompletableFuture.runAsync(() -> {
            try {
                promise.complete(DatabaseUtils.jsonToJavaClass(request.asJson(), valueType));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                promise.completeExceptionally(e);
            }

        }, ex);
        return promise;
    }
//

    public static CompletableFuture<List<Document>> parseListBody(Http.RequestBody request, Executor context) {
        CompletableFuture<List<Document>> promise = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            JsonNode json = request.asJson();
            if (!json.isArray()) {
                promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "invalid_parameters"));
                return;
            }
            promise.complete(DatabaseUtils.toListDocument((ArrayNode) json));
        }, context);
        return promise;
    }

    public static <T> CompletableFuture<List<T>> parseListBodyOfType(Http.RequestBody request, Executor context, Class<T> type) {
        CompletableFuture<List<T>> promise = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            try {
                List<T> items = new ArrayList<>();
                JsonNode json = request.asJson();
                if (!json.isArray()) {
                    promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "invalid_parameters"));
                    return;
                }
                for (JsonNode node : json) {
                    items.add(DatabaseUtils.jsonToJavaClass(node, type));
                }
                promise.complete(items);
            } catch (IOException e) {
                e.printStackTrace();
                promise.completeExceptionally(e);
            }
        }, context);
        return promise;
    }

    public static <T> Document javaClassToDocument(T valueType) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        try {
            return Document.parse(objectMapper.writeValueAsString(valueType));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public static String extractToken(Request request) {
       return request.attrs().get(PlatformAttributes.VERIFIED_JWT);
    }

}
