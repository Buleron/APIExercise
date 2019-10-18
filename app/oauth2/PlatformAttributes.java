package oauth2;

import models.collection.User;
import play.libs.typedmap.TypedKey;

public class PlatformAttributes {
    public static final TypedKey<User> AUTHENTICATED_USER = TypedKey.create("AUTHENTICATED_USER");
}
