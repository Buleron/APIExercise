package oauth2;

import jwt.VerifiedJwt;
import models.collection.User;
import play.libs.typedmap.TypedKey;

public class PlatformAttributes {
    public static final TypedKey<User> AUTHENTICATED_USER = TypedKey.create("AUTHENTICATED_USER");
    public static final TypedKey<String> VERIFIED_JWT = TypedKey.create("verifiedJwt");
}
