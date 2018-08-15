package dk.magenta.datafordeler.geo.data.municipality;

import javax.persistence.Entity;
import java.util.Objects;

@Entity
public class MunicipalityNameRecord extends MunicipalityDataRecord {

    public MunicipalityNameRecord() {
    }

    public MunicipalityNameRecord(String name) {
        this.name = name;
    }

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
        MunicipalityNameRecord that = (MunicipalityNameRecord) o;
        return Objects.equals(this.name, that.name);
    }

}
