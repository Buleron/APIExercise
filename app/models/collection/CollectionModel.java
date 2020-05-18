package models.collection;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;
import models.utils.ObjectIdDeSerializer;
import models.utils.ObjectIdSerializer;
import org.bson.codecs.pojo.annotations.BsonId;
import org.bson.codecs.pojo.annotations.BsonIgnore;
import org.bson.types.ObjectId;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(of={"id"})
@JsonInclude(Include.NON_NULL)
public @Data class CollectionModel implements Cloneable {
    @JsonProperty("_id")
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    protected ObjectId _id;
    // formatting purpose property
    @Setter(AccessLevel.NONE)
    @JsonProperty("id")
    protected String id;

    // formatting purpose property
    @Setter(AccessLevel.NONE)
    @BsonIgnore
    protected Long createdAt;
    protected Long updatedAt;

    protected Set<String> readACL = new HashSet<>();
    protected Set<String> writeACL = new HashSet<>();

    @BsonId
    @JsonProperty("_id")
    @JsonDeserialize(using = ObjectIdDeSerializer.class)
    public void setId(ObjectId id) {
        if (id == null) {
            this.id = null;
            this._id = null;
            this.createdAt = null;
            return;
        }
        this.id = id.toString();
        this.createdAt = id.getTimestamp() * 1000L;
        this._id = id;
    }

    @BsonId
    @JsonProperty("_id")
    @JsonSerialize(using = ObjectIdSerializer.class)
    public ObjectId getId() {
        return _id;
    }

    @BsonIgnore
    public Long getLastUpdate() {
        if (updatedAt != null) {
            return updatedAt;
        }
        return createdAt;
    }

    @Override
    public CollectionModel clone() throws CloneNotSupportedException {
        CollectionModel clone = (CollectionModel) super.clone();
        clone.setId(this.getId());
        clone.setReadACL(new HashSet<>(this.getReadACL()));
        clone.setWriteACL(new HashSet<>(this.getWriteACL()));
        clone.setUpdatedAt(this.getUpdatedAt());
        return clone;
    }
}
