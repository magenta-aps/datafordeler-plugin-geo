package dk.magenta.datafordeler.geo.data.common;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class LineRecord extends GeoMonotemporalRecord {

    public static final String DB_FIELD_LENGTH = "length";
    public static final String IO_FIELD_LENGTH = "længde";
    @Column(name = DB_FIELD_LENGTH)
    @JsonProperty(IO_FIELD_LENGTH)
    private double length;

    public double getLength() {
        return this.length;
    }

    public void setLength(double length) {
        this.length = length;
    }

}
