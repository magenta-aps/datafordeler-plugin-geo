package dk.magenta.datafordeler.geo.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class AreaRecord extends GeoMonotemporalRecord {

    public static final String DB_FIELD_AREA = "area";
    public static final String IO_FIELD_AREA = "areal";
    @Column(name = DB_FIELD_AREA)
    @JsonProperty(IO_FIELD_AREA)
    private double area;

    public double getArea() {
        return this.area;
    }

    public void setArea(double area) {
        this.area = area;
    }



    public static final String DB_FIELD_CIRCUMFERENCE = "circumference";
    public static final String IO_FIELD_CIRCUMFERENCE = "omkreds";
    @Column(name = DB_FIELD_CIRCUMFERENCE)
    @JsonProperty(IO_FIELD_CIRCUMFERENCE)
    private double circumference;

    public double getCircumference() {
        return this.circumference;
    }

    public void setCircumference(double circumference) {
        this.circumference = circumference;
    }
}
