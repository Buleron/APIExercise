package mongolay;


import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;

@Data
@AllArgsConstructor
public class FieldReference {
    String source;
    String target;

    public <T> Field getSourceField (Class<T> clazz) {
       return FieldUtils.getField(clazz, source, true);
    }

    public Object getValue (Object item) {
        try {
            return FieldUtils.readField(getSourceField(item.getClass()), item, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }


    public <T> Field getTargetField (Class<T> clazz) {
        return FieldUtils.getField(clazz, target, true);
    }

    public Object getTargetValue (Object item) {
        try {
            return FieldUtils.readField(getTargetField(item.getClass()), item, true);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }
}
