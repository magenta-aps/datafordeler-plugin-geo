package dk.magenta.datafordeler.geo.data.unitaddress;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressUsageRecord.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressUsageRecord.TABLE_NAME + UnitAddressUsageRecord.DB_FIELD_ENTITY,
                columnList = UnitAddressUsageRecord.DB_FIELD_ENTITY + DatabaseEntry.REF
        ),
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressUsageRecord.TABLE_NAME + UnitAddressUsageRecord.DB_FIELD_USAGE,
                columnList = UnitAddressUsageRecord.DB_FIELD_USAGE
        ),
})
public class UnitAddressUsageRecord extends GeoMonotemporalRecord<UnitAddressEntity> {

    public static final String TABLE_NAME = "geo_unit_address_usage";

    public UnitAddressUsageRecord() {
    }

    public UnitAddressUsageRecord(Integer usage) {
        this.usage = usage;
    }


    public static final String DB_FIELD_USAGE = "usage";
    public static final String IO_FIELD_USAGE = "anvendelse";
    @Column(name = DB_FIELD_USAGE)
    @JsonProperty(value = IO_FIELD_USAGE)
    private Integer usage;

    public Integer getUsage() {
        return this.usage;
    }

    public void setUsage(Integer usage) {
        this.usage = usage;
    }

    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        UnitAddressUsageRecord that = (UnitAddressUsageRecord) o;
        return Objects.equals(this.usage, that.usage);
    }
}
