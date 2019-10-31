package mongolay.annotations;


import mongolay.Constants;
import java.lang.annotation.*;


/**
 * @author Olafur Gauti Gudmundsson
 * @author Scott Hernandez
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Reference {
    /**
     * The name of the Mongo value to store the field. Defaults to the name of the field being annotated.
     */
    String[] from() default { Constants.ID_KEY };
    /**
     * The name of the Mongo value to store the field. Defaults to the name of the field being annotated.
     */
    String[] to() default { Constants.ID_KEY };
}
