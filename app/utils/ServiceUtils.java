package utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.exceptions.RequestException;
import org.bson.Document;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Http.Request;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import static play.mvc.Controller.request;

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
            	if(result instanceof List<?>) {
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
            	if(result instanceof List<?>) {
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
                if(result instanceof List<?>) {
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

       
    public static CompletableFuture<Document> parseBody(Executor context) {
        CompletableFuture<Document> promise = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
	        JsonNode json = request().body().asJson();
	        if (!json.isObject()) {
	            promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "invalid_parameters"));
	            return;
	        }
	        promise.complete(DatabaseUtils.toDocument((ObjectNode) json));
        }, context);
        return promise;
    }

    public static <T> CompletableFuture<T> parseBodyOfType(Executor ex, Class<T> valueType) {
        CompletableFuture<T> promise = new CompletableFuture<T>();
        CompletableFuture.runAsync(() -> {
            JsonNode body = request().body().asJson();

            try {
                promise.complete(DatabaseUtils.jsonToJavaClass(body, valueType));
            } catch (JsonParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                promise.completeExceptionally(e);
            } catch (JsonMappingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                promise.completeExceptionally(e);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                promise.completeExceptionally(e);
            }

        }, ex);
        return promise;
    }
//
    
    public static CompletableFuture<List<Document>> parseListBody(Executor context) {
        CompletableFuture<List<Document>> promise = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
            JsonNode json = request().body().asJson();
            if(!json.isArray()) {
                promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "invalid_parameters"));
                return;
            }
	        promise.complete(DatabaseUtils.toListDocument((ArrayNode) json));
        }, context);
        return promise;
    }
    
    public  static <T> CompletableFuture<List<T>> parseListBodyOfType(Executor context, Class<T> type) {
        CompletableFuture<List<T>> promise = new CompletableFuture<>();
        CompletableFuture.runAsync(() -> {
        	try {
	        	List<T> items = new ArrayList<>();
	            JsonNode json = request().body().asJson();
	            if(!json.isArray()) {
	                promise.completeExceptionally(new RequestException(Http.Status.BAD_REQUEST, "invalid_parameters"));
	                return;
	            }
	            for(JsonNode node : (ArrayNode) json ) {
					items.add(DatabaseUtils.jsonToJavaClass((JsonNode) node, type));
	            }
		        promise.complete(items);
			} catch (JsonParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				promise.completeExceptionally(e);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				promise.completeExceptionally(e);
			} catch (IOException e) {
				// TODO Auto-generated catch block
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
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
    
}
