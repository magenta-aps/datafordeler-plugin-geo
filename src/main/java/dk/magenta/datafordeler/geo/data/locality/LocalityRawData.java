package dk.magenta.datafordeler.geo.data.locality;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;
import dk.magenta.datafordeler.geo.data.RawData;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalityRawData extends RawData {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class LocalityRawProperties extends RawAreaProperties {

        @JsonProperty("Lokalitetsnavn")
        public String name;

        @JsonProperty("Lokalitetskode")
        public String code;

        @JsonProperty("Kommunekode")
        public Integer municipality;
    }

    @JsonProperty
    public LocalityRawProperties properties;

    @Override
    public List<GeoMonotemporalRecord> getMonotemporalRecords() {
        ArrayList<GeoMonotemporalRecord> records = new ArrayList<>();

        records.add(
                new LocalityNameRecord(this.properties.name)
                .setEditor(this.properties.editor)
                .setRegistrationFrom(this.properties.editDate)
        );

        records.add(
                new LocalityMunicipalityRecord(this.properties.municipality)
                .setEditor(this.properties.editor)
                .setRegistrationFrom(this.properties.editDate)
        );

        MultiPolygon multiPolygon = null;
        if (this.shape instanceof Polygon) {
            Polygon polygon = (Polygon) this.shape;
            multiPolygon = new MultiPolygon();
            multiPolygon.add(polygon);
        } else if (this.shape instanceof MultiPolygon) {
            multiPolygon = (MultiPolygon) this.shape;
        }
        if (multiPolygon != null) {
            records.add(
                    new LocalityShapeRecord(this.properties.area, this.properties.length, multiPolygon)
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
