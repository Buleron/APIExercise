package mongolay;


import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.bson.conversions.Bson;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;
import java.util.stream.Collectors;


@Data
@AllArgsConstructor
public class ReferencedField {
    Class<?> clazz;
    Field field;
    List<FieldReference> referencesList = new ArrayList<>();

    public void addReference(FieldReference reference) {
        referencesList.add(reference);
    }

    public boolean isCollectionField () {
        if (Collection.class.isAssignableFrom(field.getType())) {
            return true;
        } else if (Map.class.isAssignableFrom(field.getType())) {
            return true;
        } else if (Set.class.isAssignableFrom(field.getType())) {
            return true;
        }
        return false;
    }

    public boolean isMultipleFieldReference () {
        return referencesList.size() > 1;
    }

    public Class<?> getTargetClass () {
        if (isCollectionField()) {
            ParameterizedType integerListType = (ParameterizedType) field.getGenericType();
            return (Class<?>) integerListType.getActualTypeArguments()[0];
        }
        return field.getType();
    }


    /**
     * The core of the Mongo Relay Library, does the mapping based on annotations at targeted value
     * @param item
     * @param mongoRelay
     * @return
     */
    public <A extends java.util.Collection<? super TResult>, TResult> A map(A item, MongoRelay mongoRelay) {
        Collection<Object> items = this.find(item, mongoRelay);
        if (items.size() == 0) {
            return item;
        }
        // The assign part!
        for (Object source: item) {
            // find the one value that matches at the resulted values
            Collection<Object> found = items.stream().filter(target -> {
                boolean isNotAMatch = this.getReferencesList().stream().anyMatch((next) -> {
                    return !next.getTargetValue(target).equals(next.getValue(source));
                });
                return !isNotAMatch;
            }).collect(Collectors.toList());
            try {
                if (this.isCollectionField()) {
                    FieldUtils.writeField(source, this.getField().getName(), found, true);
                } else if (found.size() > 0) {
                    FieldUtils.writeField(source, this.getField().getName(), found.iterator().next(), true);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return item;
    }

    /**
     * The core of the Mongo Relay Library, does the mapping based on annotations at targeted value
     * @param item
     * @param mongoRelay
     * @return
     */
    public <A extends java.util.Collection<? super TResult>, TResult> Collection<Object> find(A item, MongoRelay mongoRelay) {
        Collection<Object> items = new ArrayList<>();
        if (this.isMultipleFieldReference()) {
            items = this.findReferenceValues(item, this.getReferencesList(), mongoRelay);

        } else {
            FieldReference fieldReference = this.getReferencesList().get(0);
            items = this.findReferenceValues(item, fieldReference, mongoRelay);
        }
        return items;
    }

    public <A extends java.util.Collection<? super TResult>, TResult> Collection<Object> findReferenceValues (A item, FieldReference fieldReference, MongoRelay relay) {
        // its a simple equals statement for each of the items
        List<Object> values = item.stream()
            .map(next -> {
                return fieldReference.getValue(next);
            })
            .filter(value -> value != null)
            .collect(Collectors.toList());

        Collection<Object> items = new ArrayList<>();
        if (values.size() == 0) {
            return items;
        }

        Class<?> referencedClass = this.getTargetClass();
        FindIterable<?> findIterable = relay.on(referencedClass)
                .getCollection().find();
        if (values.size() == 1) {
            findIterable.filter(Filters.eq(fieldReference.getTarget(), values.get(0)));
        } else {
            findIterable.filter(Filters.in(fieldReference.getTarget(), values));
        }
        findIterable.into(items);
        return items;
    }

    public <A extends java.util.Collection<? super TResult>, TResult> Collection<Object> findReferenceValues (A item, List<FieldReference> fieldReferences, MongoRelay relay) {
        // its a simple equals statement for each of the items
        List<Bson> filters = item.stream().map(next -> {
            List<Bson> matches = fieldReferences.stream()
                .map(fieldReference -> {
                    Object value = fieldReference.getValue(next);
                    return value == null ? null : Filters.eq(fieldReference.getTarget(), value);
                })
                .filter(match -> match != null)
                .collect(Collectors.toList());
            return Filters.and(matches);
        }).collect(Collectors.toList());

        Collection<Object> items = new ArrayList<>();
        if (filters.size() == 0) {
            return items;
        }

        Class<?> referencedClass = this.getTargetClass();
        relay.on(referencedClass)
                .getCollection().find()
                .filter(Filters.or(filters))
                .into(items);
        return items;
    }
}
