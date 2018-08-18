package dk.magenta.datafordeler.geo.data.unitaddress;

import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressStatusRecord.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressStatusRecord.TABLE_NAME + UnitAddressStatusRecord.DB_FIELD_ENTITY,
                columnList = UnitAddressStatusRecord.DB_FIELD_ENTITY + DatabaseEntry.REF
        ),
})
public class UnitAddressStatusRecord extends GeoMonotemporalRecord<UnitAddressEntity> {

    public static final String TABLE_NAME = "geo_unit_address_status";

    public UnitAddressStatusRecord() {
    }

    public UnitAddressStatusRecord(Integer status) {
        this.status = status;
    }


    public static final String DB_FIELD_STATUS = "status";
    @Column(name = DB_FIELD_STATUS)
    private Integer status;

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        UnitAddressStatusRecord that = (UnitAddressStatusRecord) o;
        return Objects.equals(this.status, that.status);
    }
}
