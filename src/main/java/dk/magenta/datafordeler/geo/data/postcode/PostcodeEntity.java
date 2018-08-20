package dk.magenta.datafordeler.geo.data.postcode;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dk.magenta.datafordeler.core.database.IdentifiedEntity;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.GeoEntity;
import dk.magenta.datafordeler.geo.data.SumiffiikEntity;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Collection;
import java.util.UUID;

@Entity
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + PostcodeEntity.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + PostcodeEntity.TABLE_NAME + PostcodeEntity.DB_FIELD_SUMIFFIIK_ID,
                columnList = PostcodeEntity.DB_FIELD_SUMIFFIIK_ID
        ),
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + PostcodeEntity.TABLE_NAME + PostcodeEntity.DB_FIELD_NAME,
                columnList = PostcodeEntity.DB_FIELD_NAME
        ),
})
public class PostcodeEntity extends SumiffiikEntity implements IdentifiedEntity {

    public static final String TABLE_NAME = "geo_postcode";

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="type")
    public static final String schema = "Postcode";

    public PostcodeEntity() {
    }

    public PostcodeEntity(PostcodeRawData record) {
        this.setObjectId(record.getProperties().objectId);
        this.code = record.getProperties().code;
        this.name = record.getProperties().name;
    }

    public static UUID generateUUID(int postcode) {
        String uuidInput = "postcode:"+postcode;
        return UUID.nameUUIDFromBytes(uuidInput.getBytes());
    }



    public static final String DB_FIELD_CODE = "code";
    public static final String IO_FIELD_CODE = "code";
    @Column(name = DB_FIELD_CODE)
    @JsonProperty
    private int code;

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }



    public static final String DB_FIELD_NAME = "name";
    public static final String IO_FIELD_NAME = "name";
    @Column(name = DB_FIELD_NAME)
    @JsonProperty
    private String name;

    public String getName() {
        return this.name;
    }

    @JsonProperty(value = "name")
    public void setName(String name) {
        this.name = name;
    }



    @Override
    public boolean merge(GeoEntity other) {
        return false;
    }

    public void addMonotemporalRecord(GeoMonotemporalRecord record) {
    }

    @Override
    public IdentifiedEntity getNewest(Collection<IdentifiedEntity> collection) {
        return null;
    }
}
