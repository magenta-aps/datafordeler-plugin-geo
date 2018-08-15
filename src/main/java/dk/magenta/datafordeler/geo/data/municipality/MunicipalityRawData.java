package dk.magenta.datafordeler.geo.data.municipality;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.geo.data.RawData;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MunicipalityRawData extends RawData {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class MunicipalityRawProperties extends RawAreaProperties {

        @JsonProperty("Kommunenavn")
        public String municipalityName;

        @JsonProperty("Kommunekode")
        public int code;
    }

    @JsonProperty
    public MunicipalityRawProperties properties;

    @JsonIgnore
    public MunicipalityNameRecord getNameRecord() {
        MunicipalityNameRecord newRecord = new MunicipalityNameRecord();
        newRecord.setName(this.properties.municipalityName);
        newRecord.setEditor(this.properties.editor);
        newRecord.setRegistrationFrom(this.properties.editDate);
        return newRecord;
    }

}
