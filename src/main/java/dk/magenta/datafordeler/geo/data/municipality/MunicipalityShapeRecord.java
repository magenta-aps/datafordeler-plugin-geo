package dk.magenta.datafordeler.geo.data.municipality;

import com.vividsolutions.jts.geom.MultiPolygon;
import dk.magenta.datafordeler.geo.data.AreaRecord;

import javax.persistence.Entity;

@Entity
public class MunicipalityShapeRecord extends AreaRecord<MunicipalityEntity> {

    public MunicipalityShapeRecord() {
    }

    public MunicipalityShapeRecord(double area, double circumference, MultiPolygon shape) {
        super(area, circumference, shape);
    }

    public MunicipalityShapeRecord(double area, double circumference, org.geojson.MultiPolygon shape) {
        super(area, circumference, shape);
    }
}
