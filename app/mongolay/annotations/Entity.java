package mongolay.annotations;


import java.lang.annotation.*;

/**
 * Allows marking and naming the collectionName
 *
 * @author Olafur Gauti Gudmundsson
 * @author Scott Hernandez
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Entity {
    /**
     * Sets the collection name to for this entity.  Defaults to the class's simple name
     *
     * @see Class#getSimpleName()
     */
    String collection() default "";
}

