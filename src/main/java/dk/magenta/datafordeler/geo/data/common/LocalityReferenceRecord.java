package dk.magenta.datafordeler.geo.data.common;

import dk.magenta.datafordeler.core.database.Identification;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.geo.data.GeoEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityQuery;
import org.hibernate.Session;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import java.util.Objects;

@MappedSuperclass
public class LocalityReferenceRecord<E extends GeoEntity> extends GeoMonotemporalRecord<E> {

    public LocalityReferenceRecord() {
    }

    public LocalityReferenceRecord(String code) {
        this.code = code;
    }

    public static final String DB_FIELD_CODE = "code";
    public static final String IO_FIELD_CODE = "lokalitetskode";
    @Column(name = DB_FIELD_CODE)
    private String code;

    public String getCode() {
        return this.code;
    }

    public void setCode(String code) {
        this.code = code;
    }


    @ManyToOne
    private Identification reference;

    public Identification getReference() {
        return this.reference;
    }

    public void wire(Session session) {
        if (this.reference == null && this.code != null) {
            LocalityQuery query = new LocalityQuery();
            //query.setMunicipality(Integer.toString(this.municipalityCode));
            query.setCode(this.code);
            System.out.println("Wiring...");
            for (LocalityEntity locality : QueryManager.getAllEntities(session, query, LocalityEntity.class)) {
                System.out.println("found!");
                System.out.println(locality.getCode());
                this.reference = locality.getIdentification();
                return;
            }
            System.out.println("not found");
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
