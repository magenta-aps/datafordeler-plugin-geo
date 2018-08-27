package dk.magenta.datafordeler.geo.data.building;

import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.common.UsageRecord;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = GeoPlugin.DEBUG_TABLE_PREFIX + BuildingUsageRecord.TABLE_NAME, indexes = {
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + BuildingUsageRecord.TABLE_NAME + BuildingUsageRecord.DB_FIELD_ENTITY,
                columnList = BuildingUsageRecord.DB_FIELD_ENTITY + DatabaseEntry.REF
        ),
        @Index(
                name = GeoPlugin.DEBUG_TABLE_PREFIX + BuildingUsageRecord.TABLE_NAME + BuildingUsageRecord.DB_FIELD_USAGE,
                columnList = BuildingUsageRecord.DB_FIELD_USAGE
        ),
})
public class BuildingUsageRecord extends UsageRecord<BuildingEntity> {

    public static final String TABLE_NAME = "geo_building_usage";

    public BuildingUsageRecord() {
    }

    public BuildingUsageRecord(Integer usage) {
        super(usage);
    }

}
