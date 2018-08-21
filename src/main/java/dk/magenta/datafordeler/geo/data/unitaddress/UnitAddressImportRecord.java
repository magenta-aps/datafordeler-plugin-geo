package dk.magenta.datafordeler.geo.data.unitaddress;

import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.common.ImportRecord;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressImportRecord.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + UnitAddressImportRecord.TABLE_NAME + UnitAddressImportRecord.DB_FIELD_ENTITY,
                columnList = UnitAddressImportRecord.DB_FIELD_ENTITY + DatabaseEntry.REF
        ),
})
public class UnitAddressImportRecord extends ImportRecord<UnitAddressEntity> {

    public static final String TABLE_NAME = "geo_unit_address_import";

    public UnitAddressImportRecord() {
    }

    public UnitAddressImportRecord(String importComplete) {
        super(importComplete);
    }

}
