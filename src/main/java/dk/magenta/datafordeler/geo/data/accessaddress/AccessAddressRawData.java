package dk.magenta.datafordeler.geo.data.accessaddress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.geo.data.RawData;
import dk.magenta.datafordeler.geo.data.SumiffiikRawData;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;
import org.geojson.Point;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessAddressRawData extends SumiffiikRawData {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class AccessAddressRawProperties extends RawProperties {

        @JsonProperty("BNummer")
        public String bnr;

        @JsonProperty("LokalitetsKode")
        public String locality;

        @JsonProperty("KommuneKode")
        public Integer municipality;

        @JsonProperty("BlokNavn")
        public String blockName;

        @JsonProperty("ObjektStatus")
        public Integer objectStatus;

        @JsonProperty("HusNummer")
        public String houseNumber;

        @JsonProperty("ImporteretKomplette")
        public String importComplete;

        @JsonProperty("Postnummer")
        public Integer postcode;

        @JsonProperty("Vejkode")
        public String roadcode;

        @JsonProperty("DataKilde")
        public Integer dataSource;
    }

    @JsonProperty
    public AccessAddressRawProperties properties;

    @Override
    public List<GeoMonotemporalRecord> getMonotemporalRecords() {
        ArrayList<GeoMonotemporalRecord> records = new ArrayList<>();

        records.add(
                new AccessAddressMunicipalityRecord(this.properties.municipality)
                .setEditor(this.properties.editor)
                .setRegistrationFrom(this.properties.editDate)
        );

        /*records.add(
                new AccessAddressLocalityRecord(this.properties.locality)
                .setEditor(this.properties.editor)
                .setRegistrationFrom(this.properties.editDate)
        );*/

        if (this.shape instanceof Point) {
            Point point = (Point) this.shape;

            records.add(
                    new AccessAddressShapeRecord(point)
                            .setEditor(this.properties.editor)
                            .setRegistrationFrom(this.properties.editDate)
            );
        }

        return records;
    }

    @Override
    public RawProperties getProperties() {
        return this.properties;
    }

}
