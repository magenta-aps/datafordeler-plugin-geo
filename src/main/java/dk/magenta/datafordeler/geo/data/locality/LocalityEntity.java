package dk.magenta.datafordeler.geo.data.locality;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dk.magenta.datafordeler.core.database.IdentifiedEntity;
import dk.magenta.datafordeler.core.database.Registration;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.GeoEntity;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;
import dk.magenta.datafordeler.geo.data.SumiffiikEntity;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.Filters;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + LocalityEntity.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + LocalityEntity.TABLE_NAME + LocalityEntity.DB_FIELD_SUMIFFIIK_ID,
                columnList = LocalityEntity.DB_FIELD_SUMIFFIIK_ID
        ),
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + LocalityEntity.TABLE_NAME + LocalityEntity.DB_FIELD_CODE,
                columnList = LocalityEntity.DB_FIELD_CODE
        ),
})
public class LocalityEntity extends SumiffiikEntity implements IdentifiedEntity {

    public static final String TABLE_NAME = "geo_locality";

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="type")
    public static final String schema = "Locality";

    public LocalityEntity() {
    }

    public LocalityEntity(LocalityRawData record) {
        super(record);
        this.setCode(record.properties.code);
    }

    public static UUID generateUUID(int localityCode) {
        String uuidInput = "lokalitet:"+localityCode;
        return UUID.nameUUIDFromBytes(uuidInput.getBytes());
    }


    public static final String DB_FIELD_CODE = "code";
    public static final String IO_FIELD_CODE = "kode";
    @Column(name = DB_FIELD_CODE)
    @JsonProperty
    private String code;

    public String getCode() {
        return this.code;
    }

    @JsonProperty(value = "Lokalitetskode")
    public void setCode(String code) {
        this.code = code;
    }




    public static final String DB_FIELD_NAME = "name";
    public static final String IO_FIELD_NAME = "navn";
    @OneToMany(mappedBy = LocalityNameRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    @Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_REGISTRATION_AFTER),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_REGISTRATION_BEFORE)
    })
    @JsonProperty(IO_FIELD_NAME)
    Set<LocalityNameRecord> name = new HashSet<>();

    public Set<LocalityNameRecord> getName() {
        return this.name;
    }



    public static final String DB_FIELD_MUNICIPALITY = "municipality";
    public static final String IO_FIELD_MUNICIPALITY = "kommune";
    @OneToMany(mappedBy = LocalityMunicipalityRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_MUNICIPALITY)
    Set<LocalityMunicipalityRecord> municipality = new HashSet<>();

    public Set<LocalityMunicipalityRecord> getMunicipality() {
        return this.municipality;
    }



    public static final String DB_FIELD_SHAPE = "shape";
    public static final String IO_FIELD_SHAPE = "form";
    @OneToMany(mappedBy = LocalityShapeRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_SHAPE)
    Set<LocalityShapeRecord> shape = new HashSet<>();

    public Set<LocalityShapeRecord> getShape() {
        return this.shape;
    }



    @Override
    public boolean merge(GeoEntity other) {
        return false;
    }

    public void addMonotemporalRecord(GeoMonotemporalRecord record) {
        boolean added = false;
        if (record instanceof LocalityNameRecord) {
            added = addItem(this.name, record);
        }
        if (record instanceof LocalityMunicipalityRecord) {
            added = addItem(this.municipality, record);
        }
        if (record instanceof LocalityShapeRecord) {
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
