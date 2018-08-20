package dk.magenta.datafordeler.geo.data.unitaddress;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.geo.data.RawData;
import dk.magenta.datafordeler.geo.data.SumiffiikRawData;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;
import org.geojson.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UnitAddressRawData extends SumiffiikRawData {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class UnitAddressRawProperties extends RawProperties {

        @JsonProperty("access_adress_id")
        public String accessAddressSumiffiik;

        @JsonProperty("Enhedsnummer")
        public String unitNumber;

        @JsonProperty("Enhedsanvendelse")
        public Integer usage;

        @JsonProperty("Etage")
        public String floor;

        @JsonProperty("Dor_lejlighedsnummer")
        public String door;

        @JsonProperty("Objektstatus")
        public Integer objectStatus;

        @JsonProperty("ImporteretKomplette")
        public String importComplete;

        @JsonProperty("Datakilde")
        public Integer source;

        public UUID getAccessAddressSumiffiikAsUUID() {
            return SumiffiikRawData.getSumiffiikAsUUID(this.accessAddressSumiffiik);
        }

    }

    @JsonProperty("attributes")
    public UnitAddressRawProperties properties;

    @Override
    public List<GeoMonotemporalRecord> getMonotemporalRecords() {
        ArrayList<GeoMonotemporalRecord> records = new ArrayList<>();
        records.add(
                new UnitAddressFloorRecord(this.properties.floor)
                        .setEditor(this.properties.editor)
                        .setRegistrationFrom(this.properties.editDate)
        );
        records.add(
                new UnitAddressDoorRecord(this.properties.door)
                        .setEditor(this.properties.editor)
                        .setRegistrationFrom(this.properties.editDate)
        );
        records.add(
                new UnitAddressUsageRecord(this.properties.usage)
                        .setEditor(this.properties.editor)
                        .setRegistrationFrom(this.properties.editDate)
        );
        records.add(
                new UnitAddressNumberRecord(this.properties.unitNumber)
                        .setEditor(this.properties.editor)
                        .setRegistrationFrom(this.properties.editDate)
        );
        records.add(
                new UnitAddressStatusRecord(this.properties.objectStatus)
                        .setEditor(this.properties.editor)
                        .setRegistrationFrom(this.properties.editDate)
        );
        records.add(
                new UnitAddressImportRecord(this.properties.importComplete)
                        .setEditor(this.properties.importComplete)
                        .setRegistrationFrom(this.properties.editDate)
        );
        records.add(
                new UnitAddressSourceRecord(this.properties.source)
                        .setEditor(this.properties.editor)
                        .setRegistrationFrom(this.properties.editDate)
        );
        return records;
    }

    @Override
    public RawProperties getProperties() {
        return this.properties;
    }

}
