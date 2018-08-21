package dk.magenta.datafordeler.geo.data.accessaddress;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dk.magenta.datafordeler.core.database.IdentifiedEntity;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.GeoEntity;
import dk.magenta.datafordeler.geo.data.SumiffiikEntity;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressEntity.TABLE_NAME, indexes = {
        @Index(name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressEntity.TABLE_NAME + AccessAddressEntity.DB_FIELD_BNR, columnList = AccessAddressEntity.DB_FIELD_BNR),
})
public class AccessAddressEntity extends SumiffiikEntity implements IdentifiedEntity {

    public static final String TABLE_NAME = "geo_access_address";

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="type")
    public static final String schema = "AccessAddress";

    public AccessAddressEntity() {
    }

    public AccessAddressEntity(AccessAddressRawData record) {
        super(record);
        System.out.println("Create AccessAddressEntity");
        this.setBnr(record.properties.bnr);
    }

    public static UUID generateUUID(String bnr) {
        String uuidInput = "adgangsadresse:"+bnr;
        return UUID.nameUUIDFromBytes(uuidInput.getBytes());
    }



    public static final String DB_FIELD_BNR = "bnr";
    public static final String IO_FIELD_BNR = "bnr";
    @Column(name = DB_FIELD_BNR)
    @JsonProperty
    private String bnr;

    public String getBnr() {
        return this.bnr;
    }

    @JsonProperty(value = "bnr")
    public void setBnr(String bnr) {
        this.bnr = bnr;
    }


    public static final String DB_FIELD_HOUSE_NUMBER = "houseNumber";
    public static final String IO_FIELD_HOUSE_NUMBER = "husNummer";
    @OneToMany(mappedBy = AccessAddressLocalityRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_HOUSE_NUMBER)
    Set<AccessAddressHouseNumberRecord> houseNumber = new HashSet<>();

    public Set<AccessAddressHouseNumberRecord> getHouseNumber() {
        return this.houseNumber;
    }


    public static final String DB_FIELD_ROAD = "road";
    public static final String IO_FIELD_ROAD = "vej";
    @OneToMany(mappedBy = AccessAddressRoadRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_ROAD)
    Set<AccessAddressRoadRecord> road = new HashSet<>();

    public Set<AccessAddressRoadRecord> getRoad() {
        return this.road;
    }



    public static final String DB_FIELD_MUNICIPALITY = "municipality";
    public static final String IO_FIELD_MUNICIPALITY = "kommune";
    @OneToMany(mappedBy = AccessAddressMunicipalityRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_MUNICIPALITY)
    Set<AccessAddressMunicipalityRecord> municipality = new HashSet<>();

    public Set<AccessAddressMunicipalityRecord> getMunicipality() {
        return this.municipality;
    }



    public static final String DB_FIELD_LOCALITY = "locality";
    public static final String IO_FIELD_LOCALITY = "lokalitet";
    @OneToMany(mappedBy = AccessAddressLocalityRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_LOCALITY)
    Set<AccessAddressLocalityRecord> locality = new HashSet<>();

    public Set<AccessAddressLocalityRecord> getLocality() {
        return this.locality;
    }





    public static final String DB_FIELD_SHAPE = "shape";
    public static final String IO_FIELD_SHAPE = "form";
    @OneToMany(mappedBy = AccessAddressShapeRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_SHAPE)
    Set<AccessAddressShapeRecord> shape = new HashSet<>();

    public Set<AccessAddressShapeRecord> getShape() {
        return this.shape;
    }



    @Override
    public boolean merge(GeoEntity other) {
        return false;
    }

    public void addMonotemporalRecord(GeoMonotemporalRecord record) {
        boolean added = false;
        System.out.println("Add "+record.getClass().getSimpleName()+" to AccessAddressEntity");
        if (record instanceof AccessAddressRoadRecord) {
            System.out.println("roadCode "+((AccessAddressRoadRecord) record).getCode());
            added = addItem(this.road, record);
        }
        if (record instanceof AccessAddressHouseNumberRecord) {
            added = addItem(this.houseNumber, record);
        }
        if (record instanceof AccessAddressMunicipalityRecord) {
            added = addItem(this.municipality, record);
        }
        if (record instanceof AccessAddressLocalityRecord) {
            added = addItem(this.locality, record);
        }
        if (record instanceof AccessAddressShapeRecord) {
            added = addItem(this.shape, record);
        }
        if (added) {
            System.out.println("adding");
            record.setEntity(this);
        } else {

            System.out.println("not adding");
        }
    }

    @Override
    public IdentifiedEntity getNewest(Collection<IdentifiedEntity> collection) {
        return null;
    }
}
