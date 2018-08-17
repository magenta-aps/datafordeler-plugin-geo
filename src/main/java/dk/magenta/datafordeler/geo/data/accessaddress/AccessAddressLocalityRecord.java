package dk.magenta.datafordeler.geo.data.accessaddress;

import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.common.LocalityReferenceRecord;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressLocalityRecord.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressLocalityRecord.TABLE_NAME + AccessAddressLocalityRecord.DB_FIELD_ENTITY,
                columnList = AccessAddressLocalityRecord.DB_FIELD_ENTITY + DatabaseEntry.REF
        ),
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressLocalityRecord.TABLE_NAME + AccessAddressLocalityRecord.DB_FIELD_CODE,
                columnList = AccessAddressLocalityRecord.DB_FIELD_CODE
        ),
})
public class AccessAddressLocalityRecord extends LocalityReferenceRecord<AccessAddressEntity> {

    public static final String TABLE_NAME = "geo_accessaddress_locality";

    public AccessAddressLocalityRecord() {
    }

    public AccessAddressLocalityRecord(Integer code) {
        super(code);
    }

}
