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
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressImportRecord.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressImportRecord.TABLE_NAME + UnitAddressImportRecord.DB_FIELD_ENTITY,
                columnList = UnitAddressImportRecord.DB_FIELD_ENTITY + DatabaseEntry.REF
        ),
})
public class UnitAddressImportRecord extends GeoMonotemporalRecord<UnitAddressEntity> {

    public static final String TABLE_NAME = "geo_unit_address_import";

    public UnitAddressImportRecord() {
    }

    public UnitAddressImportRecord(String importComplete) {
        this.importComplete = importComplete;
    }


    public static final String DB_FIELD_IMPORT = "importComplete";
    @Column(name = DB_FIELD_IMPORT)
    private String importComplete;

    public String getImportComplete() {
        return this.importComplete;
    }

    public void setImportComplete(String importComplete) {
        this.importComplete = importComplete;
    }

    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        UnitAddressImportRecord that = (UnitAddressImportRecord) o;
        return Objects.equals(this.importComplete, that.importComplete);
    }
}
