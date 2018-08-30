package dk.magenta.datafordeler.geo.data.common;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.Identification;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.geo.data.GeoEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityQuery;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.util.Objects;
import java.util.UUID;

@MappedSuperclass
public class LocalityReferenceRecord<E extends GeoEntity> extends GeoMonotemporalRecord<E> {

    public LocalityReferenceRecord() {
    }

    public LocalityReferenceRecord(String code) {
        this.code = code;
    }

    public LocalityReferenceRecord(UUID uuid) {
        this.uuid = uuid;
    }

    public static final String DB_FIELD_CODE = "code";
    public static final String IO_FIELD_CODE = "lokalitetskode";
    @Column(name = DB_FIELD_CODE)
    @JsonProperty(IO_FIELD_CODE)
    private String code;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }



    @Transient
    @JsonIgnore
    private UUID uuid;

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }



    public static final String DB_FIELD_REFERENCE = "reference";
    @ManyToOne
    @JsonIgnore
    private Identification reference;

    public Identification getReference() {
        return this.reference;
    }

    public void wire(Session session) {
        if (this.reference == null && this.code != null) {
            LocalityQuery query = new LocalityQuery();
            query.setCode(this.code);
            for (LocalityEntity locality : QueryManager.getAllEntities(session, query, LocalityEntity.class)) {
                this.reference = locality.getIdentification();
                break;
            }
        }
        if (this.reference == null && this.uuid != null) {
            LocalityEntity localityEntity = QueryManager.getEntity(session, this.uuid, LocalityEntity.class);
            if (localityEntity != null) {
                this.reference = localityEntity.getIdentification();
                this.code = localityEntity.getCode();
            }
        }
    }

    public boolean equalData(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equalData(o)) return false;
        LocalityReferenceRecord that = (LocalityReferenceRecord) o;
        return Objects.equals(this.code, that.code);
    }

}
