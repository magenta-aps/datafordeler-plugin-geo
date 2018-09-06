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

@Entity
@Table(name="geo_config")
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

    @Id
    @Column(name = "id")
    private final String plugin = GeoPlugin.class.getName();



    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType municipalityRegisterType = RegisterType.REMOTE_HTTP;

    public RegisterType getMunicipalityRegisterType() {
        return this.municipalityRegisterType;
    }

    @Column
    private String municipalityURL = "";

    public String getMunicipalityURL() {
        return this.municipalityURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType postcodeRegisterType = RegisterType.REMOTE_HTTP;

    public RegisterType getPostcodeRegisterType() {
        return this.postcodeRegisterType;
    }

    @Column
    private String postcodeURL = "";

    public String getPostcodeURL() {
        return this.postcodeURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType localityRegisterType = RegisterType.REMOTE_HTTP;

    public RegisterType getLocalityRegisterType() {
        return this.localityRegisterType;
    }

    @Column
    private String localityURL = "";

    public String getLocalityURL() {
        return this.localityURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType roadRegisterType = RegisterType.DISABLED;

    public RegisterType getRoadRegisterType() {
        return this.roadRegisterType;
    }

    @Column
    private String roadURL = "";


    public String getRoadURL() {
        return this.roadURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType buildingRegisterType = RegisterType.REMOTE_HTTP;

    public RegisterType getBuildingRegisterType() {
        return this.buildingRegisterType;
    }

    @Column
    private String buildingURL = "";


    public String getBuildingURL() {
        return this.buildingURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType accessAddressRegisterType = RegisterType.REMOTE_HTTP;

    public RegisterType getAccessAddressRegisterType() {
        return this.accessAddressRegisterType;
    }

    @Column
    private String accessAddressURL = "";


    public String getAccessAddressURL() {
        return this.accessAddressURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType unitAddressRegisterType = RegisterType.REMOTE_HTTP;

    public RegisterType getUnitAddressRegisterType() {
        return this.unitAddressRegisterType;
    }

    @Column
    private String unitAddressURL = "";


    public String getUnitAddressURL() {
        return this.unitAddressURL;
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
                return this.municipalityURL;
            case PostcodeEntity.schema:
                return this.postcodeURL;
            case LocalityEntity.schema:
                return this.localityURL;
            case RoadEntity.schema:
                return this.roadURL;
            case BuildingEntity.schema:
                return this.buildingURL;
            case AccessAddressEntity.schema:
                return this.accessAddressURL;
            case UnitAddressEntity.schema:
                return this.unitAddressURL;
        }
        return null;
    }
}
