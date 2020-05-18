package utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoClient;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.util.JSON;
import models.exceptions.RequestException;
import org.bson.BsonDocument;
import org.bson.BsonValue;
import org.bson.Document;
import org.bson.conversions.Bson;
import play.Logger;
import play.i18n.Messages;
import play.i18n.MessagesApi;
import play.libs.Json;
import play.mvc.Http;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static play.mvc.Controller.request;
import static play.mvc.Results.badRequest;
import static play.mvc.Results.status;

/**
 * Helper class that formats data Created by Agon on 10/13/2016.
 */
public class DatabaseUtils {

	/**
	 * Convert the mongodb java driver aggregation collection response to JSON
	 * ArrayNode
	 * 
	 * @param collection
	 * @return
	 */
	public static <T> ArrayNode toJsonNode(AggregateIterable<T> collection) {
		ArrayNode result = Json.newArray();
		for (T transaction : collection) {
			JsonNode node = toJsonNode(transaction);
			if (node != null)
				result.add(node);
		}
		return result;
	}

	/**
	 * Convert the mongodb java driver find collection response to JSON
	 * ArrayNode which also mapps the _id -> id
	 * 
	 * @param collection
	 * @return
	 */
	public static <T> ArrayNode toJsonNode(FindIterable<T> collection) {
		ArrayNode result = Json.newArray();
		for (T transaction : collection) {
			JsonNode node = toJsonNode(transaction);
			if (node != null)
				result.add(node);
		}
		return result;
	}

	/**
	 * Convert the mongodb java driver find collection response to JSON
	 * ArrayNode which also mapps the _id -> id
	 * 
	 * @param collection
	 * @return
	 */
	public static <T> ArrayNode toJsonNode(List<T> collection) {
		ArrayNode result = Json.newArray();
		for (T transaction : collection) {
			JsonNode node = toJsonNode(transaction);
			if (node != null)
				result.add(node);
		}
		return result;
	}
	
	/**
	 * Convert the mongodb java driver aggregation collection response to JSON
	 * ArrayNode
	 * 
	 * @param collection
	 * @return
	 */
	public static ArrayNode toJsonNodeWithFormattedId(AggregateIterable<Document> collection) {
		ArrayNode result = Json.newArray();
		for (Document transaction : collection) {
			JsonNode node = toJsonNodeWithFormattedId(transaction);
			if (node != null)
				result.add(node);
		}
		return result;
	}

	/**
	 * Convert the mongodb java driver find collection response to JSON
	 * ArrayNode which also mapps the _id -> id
	 * 
	 * @param collection
	 * @return
	 */
	public static <T> ArrayNode toJsonNodeWithFormattedId(FindIterable<Document> collection) {
		ArrayNode result = Json.newArray();
		for (Document transaction : collection) {
			JsonNode node = toJsonNodeWithFormattedId(transaction);
			if (node != null)
				result.add(node);
		}
		return result;
	}

	/**
	 * Convert the mongodb java driver find collection response to JSON
	 * ArrayNode which also mapps the _id -> id
	 * 
	 * @param collection
	 * @return
	 */
	public static <T> ArrayNode toJsonNodeWithFormattedId(List<Document> collection) {
		ArrayNode result = Json.newArray();
		for (Document transaction : collection) {
			JsonNode node = toJsonNodeWithFormattedId(transaction);
			if (node != null)
				result.add(node);
		}
		return result;
	}

	/**
	 * Convert the mongodb java driver single document response to JsonNode
	 * which also mapps the _id -> id
	 * 
	 * @param document
	 * @return
	 */
	public static JsonNode toJsonNodeWithFormattedId(Document document) {
		JsonNode result = toJsonNode(document);
		if (result != null && result.isObject()) {
			try {
				ObjectNode objectNode = (ObjectNode) result;
				if (document.getObjectId("_id") != null) {
					objectNode.put("id", document.getObjectId("_id").toString());
					objectNode.put("createdAt", ((long) document.getObjectId("_id").getTimestamp()) * 1000);
				}
				return objectNode;
			} catch (Exception ex) {
				return result;
			}
		}
		return null;
	}
	
	/**
	 * Convert the mongodb java driver single object response to JsonNode
	 * @param <T>
	 * 
	 * @param which
	 * @return
	 */
	public static <T> JsonNode toJsonNode(T which) {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		if (which == null) {
			return null;
		}
		ObjectWriter writer = objectMapper.writerFor(which.getClass());
		ObjectReader reader = objectMapper.readerFor(JsonNode.class);
		if (which instanceof JsonNode) {
			return (JsonNode) which;
		}
		try {
			if (which instanceof Bson) {
				if (!(which instanceof Document)) {
					return fromBsonNode((Bson) which);
				} else {
					return reader.readValue(JSON.serialize(which));
				}
			}
			return reader.readValue(writer.writeValueAsString(which));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
			Logger.debug(which.toString());
		}
		return null;
	}

	/**
	 * Converts a json node into a class
	 * @param body
	 * @param valueType
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T jsonToJavaClass(JsonNode body, Class<T> valueType) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper = objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		ObjectReader reader = objectMapper.readerFor(valueType);
		return reader.readValue(body);
	}

	public static Document documentFromThrowable(Http.Request request, Throwable t, MessagesApi messagesApi) {
		if (t instanceof CompletionException) {
            CompletionException exception = (CompletionException) t;
            Throwable cause = exception.getCause();
            if (cause instanceof RequestException) {
                RequestException requestException = (RequestException) cause;
                return documentFromThrowable(request, requestException, messagesApi);
            } else {
                return documentFromError(exception.getMessage());
            }
        } else if (t instanceof RequestException) {
        	return documentFromThrowable(request, (RequestException) t, messagesApi);
        }
        return documentFromError(t.getMessage());
	}

	public static Document documentFromThrowable(Http.Request request, RequestException requestException, MessagesApi messagesApi) {
		ObjectNode error = messageFromException(request, requestException, messagesApi);
		return toDocument(error);
	}

	public static Result resultFromThrowable(Throwable t, MessagesApi messagesApi) {
		Http.Request request = null;
		try {
			request = request();
		} catch(Exception ex) {
			// try to  see if request can be extracted
		}
		try {
			return resultFromThrowable(request, t, messagesApi);
		} catch (Exception ex) {
			ex.printStackTrace();
			return badRequest(objectNodeFromError(documentFromError(t.getMessage())));
		}
	}

    public static Result resultFromThrowable(Http.Request request, Throwable t, MessagesApi messagesApi) {
        if (t instanceof CompletionException) {
            CompletionException exception = (CompletionException) t;
            Throwable cause = exception.getCause();
            if (cause instanceof RequestException) {
                RequestException requestException = (RequestException) cause;
                return resultFromThrowable(request, requestException, messagesApi);
            } else if (cause instanceof ExecutionException) {
				ExecutionException executionException = (ExecutionException) cause;
                return resultFromThrowable(request, executionException, messagesApi);
            }
        } else if (t instanceof RequestException) {
        	return resultFromThrowable(request, (RequestException) t, messagesApi);
        } else if (t instanceof ExecutionException) {
			ExecutionException exception = (ExecutionException) t;
			Throwable cause = exception.getCause();
			if (cause instanceof RequestException) {
				RequestException requestException = (RequestException) cause;
				return resultFromThrowable(request, requestException, messagesApi);
			} else {
				return badRequest(objectNodeFromError(exception.getMessage()));
			}
		}
        return badRequest(objectNodeFromError(t.getMessage()));
    }

	private static Result resultFromThrowable(Http.Request request, RequestException requestException, MessagesApi messagesApi) {
		ObjectNode error = messageFromException(request, requestException, messagesApi);
		return status(requestException.getStatusCode(), error);
	}

	private static ObjectNode messageFromException(Http.Request request, RequestException requestException, MessagesApi messagesApi) {
		Messages messages = request == null ? messagesApi.preferred(new ArrayList<>()) : messagesApi.preferred(request);

		if (requestException.getArguments() instanceof Map) {
			Map<String, Object> errors = (Map<String, Object>)requestException.getArguments();
			List<String> error = new ArrayList<String>();
			Set<String> exceptionMessages = (Set<String>) requestException.getRawMessage();

			for(String ex : exceptionMessages) {
				String message = null;
				if (errors.size() > 0 && errors.get(ex) != null) {
					message = messages.at(ex, errors.get(ex));
				} else {
					message = messages.at(ex);
				}
				if(message == null) {
					message = ex;
				}
				error.add(message);
			}

			return objectNodeFromError(error);
		} if (requestException.getDescription() instanceof List) {
            List<Object> errors = (List<Object>) requestException.getDescription();
            return objectNodeFromError(errors.stream().map(next -> {
				String error = messages.at(next.toString());
            	return error == null ? next.toString() : error;
			}).collect(Collectors.toList()));
        } else {
			if(requestException.getArguments() instanceof List) {
				List<Object> arguments = (List<Object>) requestException.getArguments();
				if (requestException.getRawMessage() instanceof List) {					
					List<String> error = new ArrayList<String>();
					List<String> exceptionMessages = (List<String>) requestException.getRawMessage();
					for (int i = 0; i < exceptionMessages.size(); i++) {
						String message = null;
						if (arguments.size() > 0 && arguments.get(i) != null) {
							message = messages.at(exceptionMessages.get(i), arguments.get(i));
						} else {
							message = messages.at(exceptionMessages.get(i));
						}
						if(message == null) {
							message = exceptionMessages.get(i);
						}
						error.add(message);
					}
					return objectNodeFromError(error);
				} else {
					String error = null;
					if (arguments.size() > 0) {
						error = messages.at(requestException.getMessage(), arguments);
					} else {
						error = messages.at(requestException.getMessage());
					}
					return objectNodeFromError(error == null ? requestException.getMessage() : error);
				}
			}
		}
		String error = null;
		error = messages.at(requestException.getMessage());
		return objectNodeFromError(error == null ? requestException.getMessage() : error);	
	}
	
	public static Document documentFromError(Object error) {
		return toDocument(objectNodeFromError(error));
	}

    public static ObjectNode objectNodeFromError(Object error) {
        ObjectNode result = Json.newObject();
        result.put("status", false);
        result.set("message", toJsonNode(error));
        return result;
    }
    

	/**
	 * Convert the mongodb java driver single object response to JsonNode
	 * 
	 * @param which
	 * @return
	 */
	public static Document bsonValueToDocumentNode(BsonValue which) {
		return bsonValueToClass(which, Document.class);
	}
	
	/**
	 * Convert the mongodb java driver single object response to JsonNode
	 * 
	 * @param which
	 * @return
	 */
	public static JsonNode bsonValueToJsonNode(BsonValue which) {
		return bsonValueToClass(which, JsonNode.class);
	}
	
	/**
	 * Convert the mongodb java driver single object response to JsonNode
	 * 
	 * @param which
	 * @return
	 */
	public static <T> T bsonValueToClass(BsonValue which, Class<T> classType) {
		if (which == null) {
			return null;
		}
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
		ObjectReader reader = mapper.readerFor(classType);
		try {
			switch(which.getBsonType()) {
			case ARRAY:
				ArrayNode array = Json.newArray();
				for(BsonValue value : which.asArray()) {
					array.add((JsonNode) bsonValueToClass(value, classType));
				}
				return (T) array;
				case DOCUMENT:
					return (T) fromBsonNode(which.asDocument());
				case INT32:
					return reader.readValue(mapper.writeValueAsString(which.asInt32().getValue()));
				case INT64:
					return reader.readValue(mapper.writeValueAsString(which.asInt64().getValue()));
				case DOUBLE:
					return reader.readValue(mapper.writeValueAsString(which.asDouble().getValue()));
				case TIMESTAMP:
					return reader.readValue(mapper.writeValueAsString(which.asTimestamp().getTime()));
				case BOOLEAN:
					return reader.readValue(mapper.writeValueAsString(which.asBoolean().getValue()));
				case OBJECT_ID:
					return reader.readValue(mapper.writeValueAsString(which.asObjectId().getValue()));
				case BINARY:
					return reader.readValue(mapper.writeValueAsString(which.asArray().getValues()));
				default:
					return reader.readValue(mapper.writeValueAsString(which.asString().getValue()));
			} 
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Convert the mongodb java driver single object response to JsonNode
	 * 
	 * @param which
	 * @return
	 */
	private static JsonNode fromBsonNode(Bson which) {
		if (which == null) {
			return null;
		}
		BsonDocument bsonDocument = which.toBsonDocument(BsonDocument.class, MongoClient.getDefaultCodecRegistry());
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(JsonParser.Feature.ALLOW_NON_NUMERIC_NUMBERS, true);
		ObjectReader reader = mapper.readerFor(JsonNode.class);
		try {
			JsonNode next = reader.readValue(bsonDocument.toJson());
			return next;
		} catch (JsonProcessingException ex) {
			ex.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
    
    public static int compareBsonValues(BsonValue which, BsonValue other) {
    	if(which.isInt32() && other.isInt32()) {
    		return which.asInt32().compareTo(other.asInt32());// compareIntegers(which.asInt32().intValue(), other.asInt32().intValue());
    	}
    	if(which.isInt64() && other.isInt64()) {
    		return which.asInt64().compareTo(other.asInt64());
    	}
    	if(which.isDouble() && other.isDouble()) {
    		return which.asDouble().compareTo(other.asDouble());
    	}
    	if(which.isTimestamp() && other.isTimestamp()) {
    		return which.asTimestamp().compareTo(other.asTimestamp());
    	}
    	if(which.isString() && other.isString()) {
    		return which.asString().compareTo(other.asString());
    	}
    	return 0;
    }

	public static JsonNode checkNull(JsonNode which) {
		if (which == null) {
			return Json.newObject();
		}
		return which;
	}

	public static ObjectNode successObjectNode(String message) {
		ObjectNode result = Json.newObject();
		result.put("status", true);
		result.put("message", message);
		return result;
	}

	private static ObjectNode objectNodeFromError(String error) {
		ObjectNode result = Json.newObject();
		result.put("status", false);
		result.put("message", error);
		return result;
	}

	/**
	 * parses a JSON object node and converts it to a mongodb java driver
	 * Document
	 * 
	 * @param value
	 * @return
	 */
	public static Document toDocument(ObjectNode value) {
		return Document.parse(value.toString());
	}

	public static Document toModel(ObjectNode value) {
		return Document.parse(value.toString());
	}
	
	public static List<Document> toListDocument(ArrayNode json) {
		List<Document> result = new ArrayList<>();
		for (JsonNode node : json) {
			result.add(toDocument((ObjectNode) node));
		}
		return result;
	}
}
