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
    private String pullCronSchedule = null;

    public String getPullCronSchedule() {
        return this.pullCronSchedule;
    }



    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType municipalityRegisterType = RegisterType.REMOTE_HTTP;

    public RegisterType getMunicipalityRegisterType() {
        return this.municipalityRegisterType;
    }

    @Column(length = 1024)
    private String municipalityRegisterURL = "";

    public String getMunicipalityRegisterURL() {
        return this.municipalityRegisterURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType postcodeRegisterType = RegisterType.REMOTE_HTTP;

    public RegisterType getPostcodeRegisterType() {
        return this.postcodeRegisterType;
    }

    @Column(length = 1024)
    private String postcodeRegisterURL = "";

    public String getPostcodeRegisterURL() {
        return this.postcodeRegisterURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType localityRegisterType = RegisterType.REMOTE_HTTP;

    public RegisterType getLocalityRegisterType() {
        return this.localityRegisterType;
    }

    @Column(length = 1024)
    private String localityRegisterURL = "";

    public String getLocalityRegisterURL() {
        return this.localityRegisterURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType roadRegisterType = RegisterType.DISABLED;

    public RegisterType getRoadRegisterType() {
        return this.roadRegisterType;
    }

    @Column(length = 1024)
    private String roadRegisterURL = "";


    public String getRoadRegisterURL() {
        return this.roadRegisterURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType buildingRegisterType = RegisterType.REMOTE_HTTP;

    public RegisterType getBuildingRegisterType() {
        return this.buildingRegisterType;
    }

    @Column(length = 1024)
    private String buildingRegisterURL = "";


    public String getBuildingRegisterURL() {
        return this.buildingRegisterURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType accessAddressRegisterType = RegisterType.REMOTE_HTTP;

    public RegisterType getAccessAddressRegisterType() {
        return this.accessAddressRegisterType;
    }

    @Column(length = 1024)
    private String accessAddressRegisterURL = "";


    public String getAccessAddressRegisterURL() {
        return this.accessAddressRegisterURL;
    }

    @Column
    @Enumerated(EnumType.ORDINAL)
    private RegisterType unitAddressRegisterType = RegisterType.REMOTE_HTTP;

    public RegisterType getUnitAddressRegisterType() {
        return this.unitAddressRegisterType;
    }

    @Column(length = 1024)
    private String unitAddressRegisterURL = "";


    public String getUnitAddressRegisterURL() {
        return this.unitAddressRegisterURL;
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
