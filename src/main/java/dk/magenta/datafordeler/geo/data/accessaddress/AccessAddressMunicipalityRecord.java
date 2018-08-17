package dk.magenta.datafordeler.geo.data.accessaddress;

import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.common.MunicipalityReferenceRecord;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntity;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressMunicipalityRecord.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressMunicipalityRecord.TABLE_NAME + AccessAddressMunicipalityRecord.DB_FIELD_ENTITY,
                columnList = AccessAddressMunicipalityRecord.DB_FIELD_ENTITY + DatabaseEntry.REF
        ),
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + AccessAddressMunicipalityRecord.TABLE_NAME + AccessAddressMunicipalityRecord.DB_FIELD_CODE,
                columnList = AccessAddressMunicipalityRecord.DB_FIELD_CODE
        ),
})
public class AccessAddressMunicipalityRecord extends MunicipalityReferenceRecord<AccessAddressEntity> {

    public static final String TABLE_NAME = "geo_access_address_municipality";

    public AccessAddressMunicipalityRecord() {
    }

    public AccessAddressMunicipalityRecord(Integer code) {
        super(code);
    }

}
