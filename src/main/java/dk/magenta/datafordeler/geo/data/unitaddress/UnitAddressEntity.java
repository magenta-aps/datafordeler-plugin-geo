package dk.magenta.datafordeler.geo.data.unitaddress;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import dk.magenta.datafordeler.core.database.Identification;
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
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressEntity.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressEntity.TABLE_NAME + UnitAddressEntity.DB_FIELD_SUMIFFIIK_ID,
                columnList = UnitAddressEntity.DB_FIELD_SUMIFFIIK_ID
        ),
})
public class UnitAddressEntity extends SumiffiikEntity implements IdentifiedEntity {

    public static final String TABLE_NAME = "geo_unit_address";

    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, include=JsonTypeInfo.As.PROPERTY, property="type")
    public static final String schema = "UnitAddress";

    public UnitAddressEntity() {
    }

    public UnitAddressEntity(UnitAddressRawData record) {
        super(record);
    }

    public static UUID generateUUID(int localityCode) {
        String uuidInput = "enhedsadresse:"+localityCode;
        return UUID.nameUUIDFromBytes(uuidInput.getBytes());
    }



    public static final String DB_FIELD_ACCESS_ADDRESS = "accessAddress";
    public static final String IO_FIELD_ACCESS_ADDRESS = "adgangsAdresse";
    @ManyToOne(targetEntity = Identification.class)
    private Identification accessAddress;

    public Identification getAccessAddress() {
        return this.accessAddress;
    }

    public void setAccessAddress(Identification accessAddress) {
        this.accessAddress = accessAddress;
    }




    public static final String DB_FIELD_FLOOR = "floor";
    public static final String IO_FIELD_FLOOR = "etage";
    @OneToMany(mappedBy = UnitAddressFloorRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_FLOOR)
    Set<UnitAddressFloorRecord> floor = new HashSet<>();

    public Set<UnitAddressFloorRecord> getFloor() {
        return this.floor;
    }



    public static final String DB_FIELD_DOOR = "door";
    public static final String IO_FIELD_DOOR = "sidedør";
    @OneToMany(mappedBy = UnitAddressDoorRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_DOOR)
    Set<UnitAddressDoorRecord> door = new HashSet<>();

    public Set<UnitAddressDoorRecord> getDoor() {
        return this.door;
    }



    public static final String DB_FIELD_NUMBER = "number";
    public static final String IO_FIELD_NUMBER = "nummer";
    @OneToMany(mappedBy = UnitAddressNumberRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_NUMBER)
    Set<UnitAddressNumberRecord> number = new HashSet<>();

    public Set<UnitAddressNumberRecord> getNumber() {
        return this.number;
    }



    public static final String DB_FIELD_USAGE = "usage";
    public static final String IO_FIELD_USAGE = "anvendelse";
    @OneToMany(mappedBy = UnitAddressUsageRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_USAGE)
    Set<UnitAddressUsageRecord> usage = new HashSet<>();

    public Set<UnitAddressUsageRecord> getUsage() {
        return this.usage;
    }




    public static final String DB_FIELD_STATUS = "status";
    public static final String IO_FIELD_STATUS = "status";
    @OneToMany(mappedBy = UnitAddressStatusRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_STATUS)
    Set<UnitAddressStatusRecord> status = new HashSet<>();

    public Set<UnitAddressStatusRecord> getStatus() {
        return this.status;
    }



    public static final String DB_FIELD_IMPORT = "importStatus";
    public static final String IO_FIELD_IMPORT = "import";
    @OneToMany(mappedBy = UnitAddressImportRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_IMPORT)
    Set<UnitAddressImportRecord> importStatus = new HashSet<>();

    public Set<UnitAddressImportRecord> getImportStatus() {
        return this.importStatus;
    }



    public static final String DB_FIELD_SOURCE = "source";
    public static final String IO_FIELD_SOURCE = "source";
    @OneToMany(mappedBy = UnitAddressSourceRecord.DB_FIELD_ENTITY, cascade = CascadeType.ALL)
    /*@Filters({
            @Filter(name = Registration.FILTER_REGISTRATION_FROM, condition = GeoMonotemporalRecord.FILTER_EFFECT_FROM),
            @Filter(name = Registration.FILTER_REGISTRATION_TO, condition = GeoMonotemporalRecord.FILTER_EFFECT_TO)
    })*/
    @JsonProperty(IO_FIELD_SOURCE)
    Set<UnitAddressSourceRecord> source = new HashSet<>();

    public Set<UnitAddressSourceRecord> getSource() {
        return this.source;
    }



    @Override
    public boolean merge(GeoEntity other) {
        return false;
    }

    public void addMonotemporalRecord(GeoMonotemporalRecord record) {
        boolean added = false;
        if (record instanceof UnitAddressFloorRecord) {
            added = addItem(this.floor, record);
        }
        if (record instanceof UnitAddressDoorRecord) {
            added = addItem(this.door, record);
        }
        if (record instanceof UnitAddressNumberRecord) {
            added = addItem(this.number, record);
        }
        if (record instanceof UnitAddressUsageRecord) {
            added = addItem(this.usage, record);
        }
        if (record instanceof UnitAddressStatusRecord) {
            added = addItem(this.status, record);
        }
        if (record instanceof UnitAddressImportRecord) {
            added = addItem(this.importStatus, record);
        }
        if (record instanceof UnitAddressSourceRecord) {
            added = addItem(this.source, record);
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
