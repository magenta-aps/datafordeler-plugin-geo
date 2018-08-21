package dk.magenta.datafordeler.geo.data.common;

import dk.magenta.datafordeler.geo.data.GeoEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public class SourceRecord<E extends GeoEntity> extends GeoMonotemporalRecord<E> {

    public SourceRecord() {
    }

    public SourceRecord(Integer source) {
        this.source = source;
    }


    public static final String DB_FIELD_SOURCE = "source";
    @Column(name = DB_FIELD_SOURCE)
    private Integer source;

    public Integer getSource() {
        return this.source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        SourceRecord that = (SourceRecord) o;
        return Objects.equals(this.source, that.source);
    }
}
