package dk.magenta.datafordeler.geo.data.accessaddress;

import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressUsageRecord.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressUsageRecord.TABLE_NAME + AccessAddressUsageRecord.DB_FIELD_ENTITY,
                columnList = AccessAddressUsageRecord.DB_FIELD_ENTITY + DatabaseEntry.REF
        ),
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressUsageRecord.TABLE_NAME + AccessAddressUsageRecord.DB_FIELD_USAGE,
                columnList = AccessAddressUsageRecord.DB_FIELD_USAGE
        ),
})
public class AccessAddressUsageRecord extends GeoMonotemporalRecord<AccessAddressEntity> {

    public static final String TABLE_NAME = "geo_access_address_usage";

    public AccessAddressUsageRecord() {
    }

    public AccessAddressUsageRecord(Integer usage) {
        this.usage = usage;
    }


    public static final String DB_FIELD_USAGE = "accessaddress";
    @Column(name = DB_FIELD_USAGE)
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
        AccessAddressUsageRecord that = (AccessAddressUsageRecord) o;
        return Objects.equals(this.usage, that.usage);
    }
}
