package dk.magenta.datafordeler.geo.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.core.database.Identification;
import dk.magenta.datafordeler.core.database.IdentifiedEntity;
import org.hibernate.Session;

import javax.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class GeoEntity extends DatabaseEntry implements IdentifiedEntity {

    public abstract boolean merge(GeoEntity other);

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    protected Identification identification;

    @Override
    public Identification getIdentification() {
        return this.identification;
    }

    public void setIdentification(Identification identification) {
        this.identification = identification;
    }

    private String creator;

    public String getCreator() {
        return this.creator;
    }

    @JsonProperty(value = "Creator")
    public void setCreator(String creator) {
        this.creator = creator;
    }



    private OffsetDateTime creationDate;

    public OffsetDateTime getCreationDate() {
        return this.creationDate;
    }

    @JsonProperty(value = "CreationDate")
    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }

    @JsonProperty(value = "CreationDate")
    public void setCreationDate(long creationDate) {
        this.setCreationDate(Instant.ofEpochMilli(creationDate).atOffset(ZoneOffset.UTC));
    }



    @Override
    public void forceLoad(Session session) {
    }

    public abstract <T extends RawData> boolean update(T rawData);
}
