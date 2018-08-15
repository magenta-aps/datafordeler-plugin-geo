package dk.magenta.datafordeler.geo.data.municipality;

import dk.magenta.datafordeler.geo.data.AreaRecord;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class MunicipalityShapeRecord extends AreaRecord {

    public static final String DB_FIELD_ENTITY = "entity";

    @ManyToOne(optional = false)
    private MunicipalityEntity entity;

    public MunicipalityEntity getEntity() {
        return this.entity;
    }

    public void setEntity(MunicipalityEntity entity) {
        this.entity = entity;
    }
}
