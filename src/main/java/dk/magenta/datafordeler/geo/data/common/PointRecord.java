package dk.magenta.datafordeler.geo.data.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vividsolutions.jts.geom.*;
import dk.magenta.datafordeler.geo.data.GeoEntity;
import org.geojson.LngLatAlt;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@MappedSuperclass
public abstract class PointRecord<E extends GeoEntity> extends GeoMonotemporalRecord<E> {

    public PointRecord() {
    }

    public PointRecord(Point shape) {
        this.shape = shape;
    }

    public PointRecord(org.geojson.Point shape) {
        this.setShape(shape);
    }

    public static final String DB_FIELD_LENGTH = "length";
    public static final String IO_FIELD_LENGTH = "længde";
    @Column(name = DB_FIELD_LENGTH)
    @JsonProperty(IO_FIELD_LENGTH)
    private double length;

    public double getLength() {
        return this.length;
    }

    public PointRecord setLength(double length) {
        this.length = length;
        return this;
    }



    public static final String DB_FIELD_SHAPE = "shape";
    public static final String IO_FIELD_SHAPE = "form";
    @Column(name = DB_FIELD_SHAPE)
    //@Type(type = "org.hibernate.spatial.GeometryType")
    private Point shape;

    public Point getShape() {
        return this.shape;
    }

    public PointRecord setShape(Point shape) {
        this.shape = shape;
        return this;
    }

    public PointRecord setShape(org.geojson.Point shape) {
        this.shape = PointRecord.convert(shape);
        return this;
    }


    private static GeometryFactory geometryFactory = new GeometryFactory();


    public static Point convert(org.geojson.Point original) {
        Coordinate coordinate = PointRecord.convert(original.getCoordinates());
        return new Point(
                geometryFactory.getCoordinateSequenceFactory().create(new Coordinate[]{coordinate}),
                geometryFactory
        );
    }

    public static org.geojson.Point convert(Point original) {
        return new org.geojson.Point(PointRecord.convert(original.getCoordinate()));
    }

    public static Coordinate convert(LngLatAlt original) {
        return new Coordinate(original.getLongitude(), original.getLatitude());
    }

    public static LngLatAlt convert(Coordinate original) {
        return new LngLatAlt(original.x, original.y);
    }
}
