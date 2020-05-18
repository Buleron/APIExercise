package models.collection;

import com.auth0.jwt.JWT;
import com.auth0.jwt.exceptions.*;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Data;
import org.bson.codecs.pojo.annotations.BsonIgnore;

import java.util.Date;

public @Data class UserToken extends CollectionModel {
    private String token;
    private Date expiresAt;
    private String client;

    @BsonIgnore
    public String getUserId(String token) {
        // extract userId out of token
        return decode(token);
    }

    /**
     * Perform the verification against the given Token, using any previous configured options.
     * @param token to decode.
     * @return a verified and decoded JWT.
     */
    public String decode(String token) throws JWTVerificationException {
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getClaim("user_id").asString();
    }
}
