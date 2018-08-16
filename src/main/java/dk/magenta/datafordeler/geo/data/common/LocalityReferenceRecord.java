package dk.magenta.datafordeler.geo.data.common;

import dk.magenta.datafordeler.geo.data.GeoEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public class LocalityReferenceRecord<E extends GeoEntity> extends GeoMonotemporalRecord<E> {

    public LocalityReferenceRecord() {
    }

    public LocalityReferenceRecord(Integer code) {
        this.code = code;
    }

    public static final String DB_FIELD_CODE = "code";
    @Column(name = DB_FIELD_CODE)
    private Integer code;

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        LocalityReferenceRecord that = (LocalityReferenceRecord) o;
        return Objects.equals(this.code, that.code);
    }

}
