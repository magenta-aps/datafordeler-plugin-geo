package dk.magenta.datafordeler.geo.data.municipality;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dk.magenta.datafordeler.core.database.IdentifiedEntity;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.*;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + MunicipalityEntity.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + MunicipalityEntity.TABLE_NAME + MunicipalityEntity.DB_FIELD_SUMIFFIIK_ID,
                columnList = MunicipalityEntity.DB_FIELD_SUMIFFIIK_ID
        ),
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + MunicipalityEntity.TABLE_NAME + MunicipalityEntity.DB_FIELD_CODE,
                columnList = MunicipalityEntity.DB_FIELD_CODE
        ),
})
public class MunicipalityEntity extends SumiffiikEntity implements IdentifiedEntity {

    public static final String TABLE_NAME = "geo_municipality";

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="type")
    public static final String schema = "Municipality";

    public MunicipalityEntity() {
    }

    public MunicipalityEntity(MunicipalityRawData record) {
        super(record);
        this.setCode(record.properties.code);
    }

    public static UUID generateUUID(int municipalityCode) {
        String uuidInput = "kommune:"+municipalityCode;
        return UUID.nameUUIDFromBytes(uuidInput.getBytes());
    }


    public static final String DB_FIELD_CODE = "code";
    public static final String IO_FIELD_CODE = "kode";
    @Column(name = DB_FIELD_CODE)
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

    public void addMonotemporalRecord(GeoMonotemporalRecord record) {
        boolean added = false;
        if (record instanceof MunicipalityNameRecord) {
            added = addItem(this.name, record);
        }
        if (record instanceof MunicipalityShapeRecord) {
            added = addItem(this.shape, record);
        }
        if (added) {
            record.setEntity(this);
        }
    }

    @Override
    public IdentifiedEntity getNewest(Collection<IdentifiedEntity> collection) {
        return null;
    }
}
