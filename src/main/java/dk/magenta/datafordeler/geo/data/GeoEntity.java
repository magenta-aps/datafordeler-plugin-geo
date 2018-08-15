package dk.magenta.datafordeler.geo.data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.core.database.DatabaseEntry;
import dk.magenta.datafordeler.core.database.Identification;
import dk.magenta.datafordeler.core.database.IdentifiedEntity;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import org.hibernate.Session;

import javax.persistence.*;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Set;

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

    protected static <E extends GeoMonotemporalRecord> boolean addItem(Set<E> set, GeoMonotemporalRecord newItem) {
        if (newItem != null) {
            for (E oldItem : set) {
                if (newItem.equalData(oldItem) && Objects.equals(newItem.getRegistrationFrom(), oldItem.getRegistrationFrom())) {
                    return false;
                }
            }
            return set.add((E) newItem);
        }
        return false;
    }

    public abstract void addMonotemporalRecord(GeoMonotemporalRecord record);
}
