package dk.magenta.datafordeler.geo.data.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.geo.data.GeoEntity;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public class ImportRecord<E extends GeoEntity> extends GeoMonotemporalRecord<E> {

    public ImportRecord() {
    }

    public ImportRecord(String importComplete) {
        this.importComplete = importComplete;
    }


    public static final String DB_FIELD_IMPORT = "importComplete";
    public static final String IO_FIELD_IMPORT = "importeretKomplette";
    @Column(name = DB_FIELD_IMPORT)
    @JsonProperty(value = IO_FIELD_IMPORT)
    private String importComplete;

    public String getImportComplete() {
        return this.importComplete;
    }

    public void setImportComplete(String importComplete) {
        this.importComplete = importComplete;
    }

    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        ImportRecord that = (ImportRecord) o;
        return Objects.equals(this.importComplete, that.importComplete);
    }
}
