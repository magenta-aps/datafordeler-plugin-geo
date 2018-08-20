package dk.magenta.datafordeler.geo.data;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.geojson.GeoJsonObject;

import java.util.UUID;

public abstract class SumiffiikRawData extends RawData {

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
            return dk.magenta.datafordeler.geo.data.SumiffiikRawData.getSumiffiikAsUUID(this.sumiffiikId);
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

    public abstract RawProperties getProperties();

    @JsonProperty("geometry")
    public GeoJsonObject shape;

    public static UUID getSumiffiikAsUUID(String sumiffiikId) {
        if (sumiffiikId != null && sumiffiikId.length() == 38) {
            return UUID.fromString(sumiffiikId.substring(1, 37));
        }
        return null;
    }
}
