package dk.magenta.datafordeler.geo.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public abstract class RawData {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public abstract class RawProperties {

        @JsonProperty("OBJECTID")
        public int objectId;

        @JsonProperty("id")
        public String sumiffiikId;

        @JsonProperty("Creator")
        public String creator;

        @JsonProperty("CreationDate")
        public long creationDate;

        @JsonProperty("Editor")
        public String editor;

        @JsonProperty("EditDate")
        public long editDate;

        @JsonIgnore
        public UUID getSumiffiikAsUUID() {
            if (this.sumiffiikId != null && this.sumiffiikId.length() == 38) {
                return UUID.fromString(this.sumiffiikId.substring(1, 37));
            }
            return null;
        }
    }

    public abstract class RawLineProperties extends RawProperties {

        @JsonProperty("SHAPE.STLength()")
        public double length;

    }

    public abstract class RawAreaProperties extends RawProperties {

        @JsonProperty("SHAPE.STArea()")
        public double area;

        @JsonProperty("SHAPE.STLength()")
        public double length;

    }

    public abstract List<GeoMonotemporalRecord> getMonotemporalRecords();
}
