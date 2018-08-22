package dk.magenta.datafordeler.geo.data.accessaddress;

import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.core.database.Identification;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;
import dk.magenta.datafordeler.geo.data.road.RoadEntity;
import dk.magenta.datafordeler.geo.data.road.RoadQuery;
import org.hibernate.Session;

import javax.persistence.*;
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

    public AccessAddressRoadRecord(Integer municipalityCode, Integer roadCode) {
        this.municipalityCode = municipalityCode;
        this.roadCode = roadCode;
    }


    public static final String DB_FIELD_MUNICIPALITY_CODE = "municipalityCode";
    public static final String IO_FIELD_MUNICIPALITY_CODE = "kommune";
    @Column(name = DB_FIELD_MUNICIPALITY_CODE, nullable = true)
    private Integer municipalityCode;

    public Integer getMunicipalityCode() {
        return this.municipalityCode;
    }

    public void setMunicipalityCode(Integer municipalityCode) {
        this.municipalityCode = municipalityCode;
    }



    public static final String DB_FIELD_ROAD_CODE = "roadCode";
    @Column(name = DB_FIELD_ROAD_CODE)
    private Integer roadCode;

    public Integer getRoadCode() {
        return this.roadCode;
    }

    public void setRoadCode(Integer roadCode) {
        this.roadCode = roadCode;
    }


    @ManyToOne
    private Identification reference;

    public Identification getReference() {
        return this.reference;
    }

    public void wire(Session session) {
        if (this.reference == null && this.municipalityCode != null && this.roadCode != null) {
            RoadQuery query = new RoadQuery();
            //query.setMunicipality(Integer.toString(this.municipalityCode));
            query.setCode(Integer.toString(this.roadCode));
            System.out.println("Wiring...");
            for (RoadEntity road : QueryManager.getAllEntities(session, query, RoadEntity.class)) {
                System.out.println("found!");
                System.out.println(road.getMunicipality()+"|"+road.getCode());
                this.reference = road.getIdentification();
                return;
            }
            System.out.println("not found");
        }
    }



    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        AccessAddressRoadRecord that = (AccessAddressRoadRecord) o;
        return Objects.equals(this.roadCode, that.roadCode);
    }
}
