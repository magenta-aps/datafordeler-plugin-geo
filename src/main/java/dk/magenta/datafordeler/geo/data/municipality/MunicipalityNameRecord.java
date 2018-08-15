package dk.magenta.datafordeler.geo.data.municipality;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.magenta.datafordeler.geo.data.GeoMonotemporalRecord;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.xml.bind.annotation.XmlTransient;

@Entity
public class MunicipalityNameRecord extends GeoMonotemporalRecord {

    public static final String DB_FIELD_ENTITY = "entity";

    @JsonIgnore
    @XmlTransient
    @ManyToOne(optional = false)
    private MunicipalityEntity entity;

    public MunicipalityEntity getEntity() {
        return this.entity;
    }

    public void setEntity(MunicipalityEntity entity) {
        this.entity = entity;
    }



    private String name;

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
