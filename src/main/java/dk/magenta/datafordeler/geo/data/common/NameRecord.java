package dk.magenta.datafordeler.geo.data.common;

import dk.magenta.datafordeler.geo.data.GeoEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public class NameRecord<E extends GeoEntity> extends GeoMonotemporalRecord<E> {

    public NameRecord() {
    }

    public NameRecord(String name) {
        this.name = name;
    }

    public static final String DB_FIELD_NAME = "name";
    @Column(name = DB_FIELD_NAME)
    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        NameRecord that = (NameRecord) o;
        return Objects.equals(this.name, that.name);
    }

}
