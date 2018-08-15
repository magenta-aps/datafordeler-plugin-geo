package dk.magenta.datafordeler.geo.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.DatabaseEntry;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlElement;
import java.time.OffsetDateTime;
import java.util.Objects;

@MappedSuperclass
public class GeoNontemporalRecord extends DatabaseEntry {


    public static final String DB_FIELD_UPDATED = "dafoUpdated";
    public static final String IO_FIELD_UPDATED = "sidstOpdateret";
    @Column(name = DB_FIELD_UPDATED)
    @JsonProperty(value = IO_FIELD_UPDATED)
    @XmlElement(name = IO_FIELD_UPDATED)
    public OffsetDateTime dafoUpdated;

    public OffsetDateTime getDafoUpdated() {
        return this.dafoUpdated;
    }

    public GeoNontemporalRecord setDafoUpdated(OffsetDateTime dafoUpdated) {
        this.dafoUpdated = dafoUpdated;
        return this;
    }

    protected static void copy(GeoNontemporalRecord from, GeoNontemporalRecord to) {
        to.dafoUpdated = from.dafoUpdated;
    }

    public boolean equalData(Object o) {
        if (o==null || (getClass() != o.getClass())) return false;
        return true;
    }

}
