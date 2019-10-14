package mongolay.annotations;

import java.lang.annotation.*;
import mongolay.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Reference {

    String[] from() default { Constants.ID_KEY };

    String[] to() default { Constants.ID_KEY };
}
