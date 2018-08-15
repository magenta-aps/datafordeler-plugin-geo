package dk.magenta.datafordeler.geo.data.municipality;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dk.magenta.datafordeler.core.database.Identification;
import dk.magenta.datafordeler.core.database.IdentifiedEntity;
import dk.magenta.datafordeler.geo.data.AreaRecord;
import dk.magenta.datafordeler.geo.data.GeoEntity;
import dk.magenta.datafordeler.geo.data.GeoMonotemporalRecord;
import dk.magenta.datafordeler.geo.data.RawData;
import org.hibernate.Session;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

@Entity
public class MunicipalityEntity extends GeoEntity implements IdentifiedEntity {

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="type")
    public static final String schema = "Municipality";

    public MunicipalityEntity() {
    }

    public MunicipalityEntity(MunicipalityRawData record) {
        this.setSumiffiikId(record.properties.sumiffiikId);
        this.setObjectId(record.properties.objectId);
        this.setCode(record.properties.code);
        this.setCreationDate(record.properties.creationDate);
    }

    public static UUID generateUUID(int municipalityCode) {
        String uuidInput = "kommune:"+municipalityCode;
        return UUID.nameUUIDFromBytes(uuidInput.getBytes());
    }



    @JsonProperty
    private int objectId;

    public int getObjectId() {
        return this.objectId;
    }

    @JsonProperty(value = "OBJECTID")
    public void setObjectId(int objectId) {
        this.objectId = objectId;
    }


    @JsonProperty
    private String sumiffiikId;

    @JsonProperty(value = "id")
    public String getSumiffiikId() {
        return this.sumiffiikId;
    }

    public void setSumiffiikId(String sumiffiikId) {
        this.sumiffiikId = sumiffiikId;
    }




    @JsonProperty
    private int code;

    public int getCode() {
        return this.code;
    }

    @JsonProperty(value = "Kommunekode")
    public void setCode(int code) {
        this.code = code;
    }


    public static final String DB_FIELD_NAME = "name";
    public static final String IO_FIELD_NAME = "navn";
    @OneToMany(mappedBy = MunicipalityNameRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_NAME)
    Set<MunicipalityNameRecord> name = new HashSet<>();

    public Set<MunicipalityNameRecord> getName() {
        return this.name;
    }







    public static final String DB_FIELD_SHAPE = "shape";
    public static final String IO_FIELD_SHAPE = "form";
    @OneToMany(mappedBy = MunicipalityShapeRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_SHAPE)
    Set<MunicipalityShapeRecord> shape = new HashSet<>();

    public Set<MunicipalityShapeRecord> getShape() {
        return this.shape;
    }



    @Override
    public boolean merge(GeoEntity other) {
        return false;
    }

    @Override
    public void forceLoad(Session session) {

    }

    @Override
    public <T extends RawData> boolean update(T rawData) {
        boolean updated = false;
        if (rawData instanceof MunicipalityRawData) {
            MunicipalityRawData raw = (MunicipalityRawData) rawData;
            MunicipalityNameRecord nameRecord = GeoMonotemporalRecord.newestRecord(this.name);

            if (nameRecord == null || !Objects.equals(nameRecord.getName(), raw.properties.municipalityName)) {
                MunicipalityNameRecord newRecord = raw.getNameRecord();
                newRecord.setEntity(this);
                this.name.add(newRecord);
            }

        }
        return updated;
    }

    @Override
    public IdentifiedEntity getNewest(Collection<IdentifiedEntity> collection) {
        return null;
    }
}
