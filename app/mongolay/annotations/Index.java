package mongolay.annotations;
import mongolay.utils.IndexType;
import java.lang.annotation.*;


/**
 * Defines an index
 *
 * @author Scott Hernandez
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Index {
    /**
     * "Direction" of the indexing.  Defaults to {@link IndexType#ASC}.
     *
     * @see IndexType
     */
    IndexType type() default IndexType.DESC;

}
