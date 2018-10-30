package dk.magenta.datafordeler.geo.configuration;

import dk.magenta.datafordeler.core.configuration.Configuration;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressEntity;
import dk.magenta.datafordeler.geo.data.building.BuildingEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntity;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntity;
import dk.magenta.datafordeler.geo.data.postcode.PostcodeEntity;
import dk.magenta.datafordeler.geo.data.road.RoadEntity;
import dk.magenta.datafordeler.geo.data.unitaddress.UnitAddressEntity;

import javax.persistence.*;
import java.nio.charset.Charset;

@Entity
@Table(name = "geo_config")
public class GeoConfiguration implements Configuration {

    public enum RegisterType {
        DISABLED(0),
        LOCAL_FILE(1),
        REMOTE_HTTP(2);

        private int value;

        RegisterType(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }
    }

    public enum CharsetValue {
        UTF_8(0),
        ISO_8859_1(1);

        private int value;

        CharsetValue(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public String asString() {
            return this.name().replace('_', '-');
        }

        public Charset asCharset() {
            return Charset.forName(this.asString());
        }
    }

    @Id
    @Column(name = "id")
    private final String plugin = GeoPlugin.class.getName();


    @Column
    private String pullCronSchedule = null;

    public String getPullCronSchedule() {
        return this.pullCronSchedule;
    }



    @Column
    @Enumerated(EnumType.ORDINAL)
    private CharsetValue charsetName = CharsetValue.UTF_8;

    public CharsetValue getCharsetName() {
        return this.charsetName;
    }

    public Charset getCharset() {
        return this.charsetName.asCharset();
    }

    public void setCharsetName(CharsetValue charsetName) {
        this.charsetName = charsetName;
    }


    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType municipalityRegisterType = RegisterType.DISABLED;

    public RegisterType getMunicipalityRegisterType() {
        return this.municipalityRegisterType;
    }

    public void setMunicipalityRegisterType(RegisterType municipalityRegisterType) {
        this.municipalityRegisterType = municipalityRegisterType;
    }


    @Column(length = 1024)
    private String municipalityRegisterURL = "";

    public String getMunicipalityRegisterURL() {
        return this.municipalityRegisterURL;
    }

    public void setMunicipalityRegisterURL(String municipalityRegisterURL) {
        this.municipalityRegisterURL = municipalityRegisterURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType postcodeRegisterType = RegisterType.DISABLED;

    public RegisterType getPostcodeRegisterType() {
        return this.postcodeRegisterType;
    }

    public void setPostcodeRegisterType(RegisterType postcodeRegisterType) {
        this.postcodeRegisterType = postcodeRegisterType;
    }

    @Column(length = 1024)
    private String postcodeRegisterURL = "";

    public String getPostcodeRegisterURL() {
        return this.postcodeRegisterURL;
    }


    public void setPostcodeRegisterURL(String postcodeRegisterURL) {
        this.postcodeRegisterURL = postcodeRegisterURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType localityRegisterType = RegisterType.DISABLED;

    public RegisterType getLocalityRegisterType() {
        return this.localityRegisterType;
    }

    public void setLocalityRegisterType(RegisterType localityRegisterType) {
        this.localityRegisterType = localityRegisterType;
    }

    @Column(length = 1024)
    private String localityRegisterURL = "";

    public String getLocalityRegisterURL() {
        return this.localityRegisterURL;
    }


    public void setLocalityRegisterURL(String localityRegisterURL) {
        this.localityRegisterURL = localityRegisterURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType roadRegisterType = RegisterType.DISABLED;

    public RegisterType getRoadRegisterType() {
        return this.roadRegisterType;
    }

    public void setRoadRegisterType(RegisterType roadRegisterType) {
        this.roadRegisterType = roadRegisterType;
    }

    @Column(length = 1024)
    private String roadRegisterURL = "";


    public String getRoadRegisterURL() {
        return this.roadRegisterURL;
    }

    public void setRoadRegisterURL(String roadRegisterURL) {
        this.roadRegisterURL = roadRegisterURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType buildingRegisterType = RegisterType.DISABLED;

    public RegisterType getBuildingRegisterType() {
        return this.buildingRegisterType;
    }

    public void setBuildingRegisterType(RegisterType buildingRegisterType) {
        this.buildingRegisterType = buildingRegisterType;
    }

    @Column(length = 1024)
    private String buildingRegisterURL = "";


    public String getBuildingRegisterURL() {
        return this.buildingRegisterURL;
    }

    public void setBuildingRegisterURL(String buildingRegisterURL) {
        this.buildingRegisterURL = buildingRegisterURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType accessAddressRegisterType = RegisterType.DISABLED;

    public RegisterType getAccessAddressRegisterType() {
        return this.accessAddressRegisterType;
    }

    public void setAccessAddressRegisterType(RegisterType accessAddressRegisterType) {
        this.accessAddressRegisterType = accessAddressRegisterType;
    }

    @Column(length = 1024)
    private String accessAddressRegisterURL = "";


    public String getAccessAddressRegisterURL() {
        return this.accessAddressRegisterURL;
    }

    public void setAccessAddressRegisterURL(String accessAddressRegisterURL) {
        this.accessAddressRegisterURL = accessAddressRegisterURL;
    }


    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType unitAddressRegisterType = RegisterType.DISABLED;

    public RegisterType getUnitAddressRegisterType() {
        return this.unitAddressRegisterType;
    }

    public void setUnitAddressRegisterType(RegisterType unitAddressRegisterType) {
        this.unitAddressRegisterType = unitAddressRegisterType;
    }

    @Column(length = 1024)
    private String unitAddressRegisterURL = "";


    public String getUnitAddressRegisterURL() {
        return this.unitAddressRegisterURL;
    }

    public void setUnitAddressRegisterURL(String unitAddressRegisterURL) {
        this.unitAddressRegisterURL = unitAddressRegisterURL;
    }


    public RegisterType getRegisterType(String schema) {
        switch (schema) {
            case MunicipalityEntity.schema:
                return this.municipalityRegisterType;
            case PostcodeEntity.schema:
                return this.postcodeRegisterType;
            case LocalityEntity.schema:
                return this.localityRegisterType;
            case RoadEntity.schema:
                return this.roadRegisterType;
            case BuildingEntity.schema:
                return this.buildingRegisterType;
            case AccessAddressEntity.schema:
                return this.accessAddressRegisterType;
            case UnitAddressEntity.schema:
                return this.unitAddressRegisterType;
        }
        return null;
    }

    public String getURL(String schema) {
        switch (schema) {
            case MunicipalityEntity.schema:
                return this.municipalityRegisterURL;
            case PostcodeEntity.schema:
                return this.postcodeRegisterURL;
            case LocalityEntity.schema:
                return this.localityRegisterURL;
            case RoadEntity.schema:
                return this.roadRegisterURL;
            case BuildingEntity.schema:
                return this.buildingRegisterURL;
            case AccessAddressEntity.schema:
                return this.accessAddressRegisterURL;
            case UnitAddressEntity.schema:
                return this.unitAddressRegisterURL;
        }
        return null;
    }
}
