package dk.magenta.datafordeler.geo.data.accessaddress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
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

        public Integer roadcode;

        @JsonProperty("Vejkode")
        public void setRoadcode(String roadCode) {
            this.roadcode = Integer.parseInt(roadCode, 10);
        }

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
        );

        records.add(
                new AccessAddressRoadRecord(this.properties.roadcode)
        );

        records.add(
                new AccessAddressHouseNumberRecord(this.properties.houseNumber)
        );

        records.add(
                new AccessAddressBlockNameRecord(this.properties.blockName)
        );

        records.add(
                new AccessAddressLocalityRecord(this.properties.locality)
        );

        records.add(
                new AccessAddressStatusRecord(this.properties.objectStatus)
        );

        records.add(
                new AccessAddressImportRecord(this.properties.importComplete)
        );

        records.add(
                new AccessAddressSourceRecord(this.properties.dataSource)
        );

        if (this.shape instanceof Point) {
            records.add(
                    new AccessAddressShapeRecord((Point) this.shape)
            );
        }

        for (GeoMonotemporalRecord record : records) {
            record.setEditor(this.properties.editor);
            record.setRegistrationFrom(this.properties.editDate);
        }

        return records;
    }

    @Override
    public RawProperties getProperties() {
        return this.properties;
    }

}
