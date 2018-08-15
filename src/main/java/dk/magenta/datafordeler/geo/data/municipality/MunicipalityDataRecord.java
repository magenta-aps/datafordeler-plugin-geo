package dk.magenta.datafordeler.geo.data.municipality;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dk.magenta.datafordeler.geo.data.GeoMonotemporalRecord;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlTransient;
import java.time.OffsetDateTime;

@MappedSuperclass
public abstract class MunicipalityDataRecord extends GeoMonotemporalRecord {

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

    @Override
    public MunicipalityDataRecord setEditor(String editor) {
        return (MunicipalityDataRecord) super.setEditor(editor);
    }

    @Override
    public MunicipalityDataRecord setRegistrationFrom(OffsetDateTime registrationFrom) {
        return (MunicipalityDataRecord) super.setRegistrationFrom(registrationFrom);
    }

    @Override
    public MunicipalityDataRecord setRegistrationFrom(long registrationFrom) {
        return (MunicipalityDataRecord) super.setRegistrationFrom(registrationFrom);
    }

    @Override
    public MunicipalityDataRecord setRegistrationTo(OffsetDateTime registrationTo) {
        return (MunicipalityDataRecord) super.setRegistrationTo(registrationTo);
    }

    @Override
    public MunicipalityDataRecord setRegistrationTo(long registrationTo) {
        return (MunicipalityDataRecord) super.setRegistrationTo(registrationTo);
    }
}
