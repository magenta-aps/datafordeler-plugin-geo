package dk.magenta.datafordeler.geo.data.common;

import dk.magenta.datafordeler.geo.data.GeoEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public class ObjectStatusRecord<E extends GeoEntity> extends GeoMonotemporalRecord<E> {

    public ObjectStatusRecord() {
    }

    public ObjectStatusRecord(Integer status) {
        this.status = status;
    }


    public static final String DB_FIELD_STATUS = "status";
    @Column(name = DB_FIELD_STATUS)
    private Integer status;

    public Integer getStatus() {
        return this.status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        ObjectStatusRecord that = (ObjectStatusRecord) o;
        return Objects.equals(this.status, that.status);
    }
}
