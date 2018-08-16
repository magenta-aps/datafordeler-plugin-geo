package dk.magenta.datafordeler.geo.data.municipality;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dk.magenta.datafordeler.core.database.IdentifiedEntity;
import dk.magenta.datafordeler.geo.data.GeoEntity;
import dk.magenta.datafordeler.geo.data.GeoMonotemporalRecord;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@MappedSuperclass
public abstract class SumiffiikEntity extends GeoEntity implements IdentifiedEntity {

    public SumiffiikEntity() {
    }

    public SumiffiikEntity(MunicipalityRawData record) {
        this.setSumiffiikId(record.properties.sumiffiikId);
        this.setObjectId(record.properties.objectId);
        this.setCreator(record.properties.creator);
        this.setCreationDate(record.properties.creationDate);
    }


    public static final String DB_FIELD_OBJECT_ID = "objectId";
    @Column(name = DB_FIELD_OBJECT_ID)
    @JsonProperty
    private int objectId;

    public int getObjectId() {
        return this.objectId;
    }

    @JsonProperty(value = "OBJECTID")
    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }


    public static final String DB_FIELD_SUMIFFIIK_ID = "sumiffiikId";
    @Column(name = DB_FIELD_SUMIFFIIK_ID)
    @JsonProperty
    private String sumiffiikId;

    @JsonProperty(value = "id")
    public String getSumiffiikId() {
        return this.sumiffiikId;
    }

    public void setSumiffiikId(String sumiffiikId) {
        this.sumiffiikId = sumiffiikId;
    }

}
