package models;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

/**
 * Response.
 */
@NoArgsConstructor
@Builder
public @Data
class Response {
    /**
     * Response success.
     */
    private boolean success;
    /**
     * Response code.
     */
    private int code;
    /**
     * Response message.
     */
    private String message;
    /**
     * Response data.
     */
    private Object data;

    /**
     * Response .
     *
     * @param success Success as boolean.
     */
    public Response(boolean success) {
        this.success = success;
    }
    /**
     * Response .
     *
     * @param success Success as boolean.
     * @param code    Message as int.
     */
    public Response(boolean success, int code) {
        this(success);
        this.code = code;
    }

    /**
     * Response .
     *
     * @param success Success as boolean.
     * @param code    Message as string.
     * @param message Message as string.
     */
    public Response(boolean success, int code, String message) {
        this(success, code);
        this.message = message;
    }

    /**
     * Response .
     *
     * @param success Success as boolean.
     * @param code    code as int.
     * @param message Message as string.
     * @param data    Object data.
     */
    public Response(boolean success, int code, String message, Object data) {
        this(success, code, message);
        this.data = data;
    }
    /**
     * Response .
     * Convert Response into String
     */
    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(this);
        } catch (JsonProcessingException er) {
            er.printStackTrace();
        }
        return objectMapper.toString();
    }
}
