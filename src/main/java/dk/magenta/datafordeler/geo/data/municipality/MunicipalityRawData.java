package dk.magenta.datafordeler.geo.data.municipality;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import dk.magenta.datafordeler.geo.data.GeoMonotemporalRecord;
import dk.magenta.datafordeler.geo.data.RawData;
import org.geojson.MultiPolygon;
import org.geojson.Polygon;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    public List<GeoMonotemporalRecord> getMonotemporalRecords() {
        ArrayList<GeoMonotemporalRecord> records = new ArrayList<>();
        records.add(
                new MunicipalityNameRecord(this.properties.municipalityName)
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
                    new MunicipalityShapeRecord(this.properties.area, this.properties.length, multiPolygon)
                    .setEditor(this.properties.editor)
                    .setRegistrationFrom(this.properties.editDate)
            );
        }

        return records;
    }

}
