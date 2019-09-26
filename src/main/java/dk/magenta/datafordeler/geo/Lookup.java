package dk.magenta.datafordeler.geo;

public class Lookup {
    private String municipalityName = null;
    private String roadName = null;
    private String localityCode = null;
    private String localityAbbrev = null;
    private String localityName = null;
    private int postalCode = 0;
    private String postalDistrict = null;
    private String bNumber = null;

    public String getMunicipalityName() {
        return municipalityName;
    }

    public void setMunicipalityName(String municipalityName) {
        this.municipalityName = municipalityName;
    }

    public String getRoadName() {
        return roadName;
    }

    public void setRoadName(String roadName) {
        this.roadName = roadName;
    }

    public String getLocalityCode() {
        return localityCode;
    }

    public void setLocalityCode(String localityCode) {
        this.localityCode = localityCode;
    }

    public String getLocalityAbbrev() {
        return localityAbbrev;
    }

    public void setLocalityAbbrev(String localityAbbrev) {
        this.localityAbbrev = localityAbbrev;
    }

    public String getLocalityName() {
        return localityName;
    }

    public void setLocalityName(String localityName) {
        this.localityName = localityName;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode = postalCode;
    }

    public String getPostalDistrict() {
        return postalDistrict;
    }

    public void setPostalDistrict(String postalDistrict) {
        this.postalDistrict = postalDistrict;
    }

    public String getbNumber() {
        return bNumber;
    }

    public void setbNumber(String bNumber) {
        this.bNumber = bNumber;
    }
}
