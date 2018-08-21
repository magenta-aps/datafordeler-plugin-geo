package dk.magenta.datafordeler.geo.data.road;

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
public class RoadRawData extends SumiffiikRawData {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public class RoadRawProperties extends RawLineProperties {

        @JsonProperty("Vejnavn")
        public String name;

        @JsonProperty("Vejadresseringsnavn")
        public String addressingName;

        //@JsonProperty("Vejkode")
        @JsonIgnore
        public int code;

        @JsonProperty("Lokalitetskode")
        public String locality;

        @JsonProperty("Kommunekode")
        public Integer municipality;
    }

    @JsonProperty
    public RoadRawProperties properties;

    @Override
    public List<GeoMonotemporalRecord> getMonotemporalRecords() {
        ArrayList<GeoMonotemporalRecord> records = new ArrayList<>();

        records.add(
                new RoadNameRecord(this.properties.name, this.properties.addressingName)
                .setEditor(this.properties.editor)
                .setRegistrationFrom(this.properties.editDate)
        );

        records.add(
                new RoadLocalityRecord(this.properties.locality)
                        .setEditor(this.properties.editor)
                        .setRegistrationFrom(this.properties.editDate)
        );

        records.add(
                new RoadMunicipalityRecord(this.properties.municipality)
                        .setEditor(this.properties.editor)
                        .setRegistrationFrom(this.properties.editDate)
        );

        MultiLineString multiLineString = null;
        if (this.shape instanceof LineString) {
            LineString lineString = (LineString) this.shape;
            multiLineString = new MultiLineString();
            multiLineString.add(lineString.getCoordinates());
        } else if (this.shape instanceof MultiLineString) {
            multiLineString = (MultiLineString) this.shape;
        }
        if (multiLineString != null) {
            records.add(
                    new RoadShapeRecord(this.properties.length, multiLineString)
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
