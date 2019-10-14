package models.exceptions;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Agon on 12/16/2016.
 */
public @Data class RequestException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// either String or List<String>
    private Object description;
    private int statusCode;
    private Object arguments;
    public RequestException(int statusCode, Object message) {
        super(message.toString());
        this.description = message;
        this.statusCode = statusCode;
        this.arguments = new ArrayList<>();
    }

    public RequestException(int statusCode, Object message, List<Object> arguments) {
        super(message.toString());
        this.description = message;
        this.statusCode = statusCode;
        this.arguments = arguments;
    }
    
    public RequestException(int statusCode, Map<String, Object> errors) {
        super(errors.keySet().toString());
        this.description = errors.keySet();
        this.statusCode = statusCode;
        this.arguments = errors;
    } 
    
    @Override
    public String getMessage() {
        return description.toString();
    }

    public int getStatusCode() {
        return statusCode;
    }     
    
    public Object getRawMessage() {
    	return description;
    }
    
    public Object getArguments() {
        return arguments;
    }
}


