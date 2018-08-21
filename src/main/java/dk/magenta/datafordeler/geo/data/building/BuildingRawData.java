package dk.magenta.datafordeler.geo.data.building;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.geo.data.RawData;
import dk.magenta.datafordeler.geo.data.SumiffiikRawData;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;
import org.geojson.LineString;
import org.geojson.MultiLineString;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildingRawData extends SumiffiikRawData {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class RoadRawProperties extends RawAreaProperties {

        @JsonProperty("a_nummer")
        public String anr;

        @JsonProperty("B_nummer")
        public String bnr;

        @JsonProperty("Lokalitetskode")
        public String locality;

        @JsonProperty("Anvendelse")
        public Integer usage;
    }

    @JsonProperty
    public RoadRawProperties properties;

    @Override
    public List<GeoMonotemporalRecord> getMonotemporalRecords() {
        ArrayList<GeoMonotemporalRecord> records = new ArrayList<>();

        records.add(
                new BuildingUsageRecord(this.properties.usage)
                .setEditor(this.properties.editor)
                .setRegistrationFrom(this.properties.editDate)
        );

        records.add(
                new BuildingLocalityRecord(this.properties.locality)
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
                    new BuildingShapeRecord(this.properties.area, this.properties.length, multiPolygon)
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
