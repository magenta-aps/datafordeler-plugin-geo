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
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressRoadRecord.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressRoadRecord.TABLE_NAME + AccessAddressRoadRecord.DB_FIELD_ENTITY,
                columnList = AccessAddressRoadRecord.DB_FIELD_ENTITY + DatabaseEntry.REF
        ),
})
public class AccessAddressRoadRecord extends GeoMonotemporalRecord<AccessAddressEntity> {

    public static final String TABLE_NAME = "geo_access_address_road";

    public AccessAddressRoadRecord() {
    }

    public AccessAddressRoadRecord(Integer code) {
        this.code = code;
    }


    public static final String DB_FIELD_CODE = "code";
    @Column(name = DB_FIELD_CODE)
    private Integer code;

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        AccessAddressRoadRecord that = (AccessAddressRoadRecord) o;
        return Objects.equals(this.code, that.code);
    }
}
