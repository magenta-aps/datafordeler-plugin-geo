package dk.magenta.datafordeler.geo.data.accessaddress;

import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.common.ImportRecord;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressImportRecord.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressImportRecord.TABLE_NAME + AccessAddressImportRecord.DB_FIELD_ENTITY,
                columnList = AccessAddressImportRecord.DB_FIELD_ENTITY + DatabaseEntry.REF
        ),
})
public class AccessAddressImportRecord extends ImportRecord<AccessAddressEntity> {

    public static final String TABLE_NAME = "geo_access_address_import";

    public AccessAddressImportRecord() {
    }

    public AccessAddressImportRecord(String importComplete) {
        super(importComplete);
    }

}
