package dk.magenta.datafordeler.geo.data.postcode;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.geo.data.RawData;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;

import java.util.Collections;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PostcodeRawData extends RawData {

    public class PostcodeRawProperties {

        @JsonProperty("OBJECTID")
        public int objectId;

        @JsonProperty("Postdistri")
        public String name;

        @JsonProperty("Postnummer")
        public int code;

    }

    @JsonProperty("properties")
    public PostcodeRawProperties properties;

    public PostcodeRawProperties getProperties() {
        return this.properties;
    }

    @Override
    public List<GeoMonotemporalRecord> getMonotemporalRecords() {
        return Collections.emptyList();
    }

}
