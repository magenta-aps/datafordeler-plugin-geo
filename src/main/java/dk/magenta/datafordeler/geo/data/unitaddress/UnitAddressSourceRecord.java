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
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressSourceRecord.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressSourceRecord.TABLE_NAME + UnitAddressSourceRecord.DB_FIELD_ENTITY,
                columnList = UnitAddressSourceRecord.DB_FIELD_ENTITY + DatabaseEntry.REF
        ),
})
public class UnitAddressSourceRecord extends GeoMonotemporalRecord<UnitAddressEntity> {

    public static final String TABLE_NAME = "geo_unit_address_source";

    public UnitAddressSourceRecord() {
    }

    public UnitAddressSourceRecord(Integer source) {
        this.source = source;
    }


    public static final String DB_FIELD_SOURCE = "source";
    @Column(name = DB_FIELD_SOURCE)
    private Integer source;

    public Integer getSource() {
        return this.source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        UnitAddressSourceRecord that = (UnitAddressSourceRecord) o;
        return Objects.equals(this.source, that.source);
    }
}
