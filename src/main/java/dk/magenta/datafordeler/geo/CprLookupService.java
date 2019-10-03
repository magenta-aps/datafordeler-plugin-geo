package dk.magenta.datafordeler.geo;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.fapi.BaseQuery;
import dk.magenta.datafordeler.core.util.DoubleHashMap;
import dk.magenta.datafordeler.cpr.CprRecordFilter;
import dk.magenta.datafordeler.cpr.records.road.RoadRecordQuery;
import dk.magenta.datafordeler.cpr.records.road.data.RoadNameBitemporalRecord;
import dk.magenta.datafordeler.cpr.records.road.data.RoadPostalcodeBitemporalRecord;
import dk.magenta.datafordeler.cvr.records.unversioned.Municipality;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CprLookupService {

    private Logger log = LogManager.getLogger("dk.magenta.datafordeler.geo.CprLookupService");

    private Session session;
    private static Pattern houseNumberPattern = Pattern.compile("(\\d+)(.*)");

    private HashMap<Integer, Municipality> municipalityCacheDK = new HashMap<>();

    public CprLookupService(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return this.session;
    }

    public GeoLookupDTO doLookup(int municipalityCode, int roadCode) {
        return this.doLookup(municipalityCode, roadCode, null);
    }

    public GeoLookupDTO doLookup(int municipalityCode, int roadCode, String houseNumber) {

        GeoLookupDTO geoLookupDTO = new GeoLookupDTO();

        OffsetDateTime now = OffsetDateTime.now();

        Municipality municipality = this.getMunicipalityDK(session, municipalityCode);
        if (municipality != null) {
            geoLookupDTO.setMunicipalityName(municipality.getName());
            if (geoLookupDTO.getMunicipalityName() != null) {
                geoLookupDTO.setMunicipalityName(geoLookupDTO.getMunicipalityName().substring(0, 1).toUpperCase() + geoLookupDTO.getMunicipalityName().substring(1).toLowerCase());
            }
        }
        this.populateRoadDK(geoLookupDTO, session, municipalityCode, roadCode, houseNumber);

        return geoLookupDTO;
    }


    public String getPostalCodeDistrict(int code) {
        //TODO: Implement something or delete
        return null;
    }




    private void populateRoadDK(GeoLookupDTO lookup, Session session, int municipalityCode, int roadCode, String houseNumber) {
        dk.magenta.datafordeler.cpr.records.road.data.RoadEntity roadEntity = this.getRoadDK(session, municipalityCode, roadCode);
        if (roadEntity != null) {
            OffsetDateTime now = OffsetDateTime.now();
            RoadNameBitemporalRecord nameRecord = CprRecordFilter.filterRecordsByRegistrationAndEffectReturnNewest(roadEntity.getName(), now);
            lookup.setRoadName(nameRecord.getRoadName());

            RoadPostalcodeBitemporalRecord postCode = this.getRoadPostalCodeDK(roadEntity, houseNumber);
            if (postCode != null) {
                lookup.setPostalCode(postCode.getPostalCode());
                lookup.setPostalDistrict(postCode.getPostalDistrict());
            }
        }
    }



    private Municipality getMunicipalityDK(Session session, int municipalityCode) {
        Municipality municipality;
        if (municipalityCacheDK.containsKey(municipalityCode)) {
            municipality = municipalityCacheDK.get(municipalityCode);
            if (municipality != null) {
                session.merge(municipality);
            }
            return municipality;
        }
        municipality = QueryManager.getItem(session, Municipality.class, Collections.singletonMap(Municipality.DB_FIELD_CODE, municipalityCode));
        if (municipality != null) {
            municipalityCacheDK.put(municipalityCode, municipality);
        }
        return municipality;
    }

    private dk.magenta.datafordeler.cpr.records.road.data.RoadEntity getRoadDK(Session session, int municipalityCode, int roadCode) {
        try {
            RoadRecordQuery roadQuery = new RoadRecordQuery();
            roadQuery.setVejkode(roadCode);
            roadQuery.addKommunekode(municipalityCode);
            List<dk.magenta.datafordeler.cpr.records.road.data.RoadEntity> roadEntities = QueryManager.getAllEntities(session, roadQuery, dk.magenta.datafordeler.cpr.records.road.data.RoadEntity.class);
            return roadEntities.get(0);
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            log.error("Failure parsing CPR adress-record", e);
        }
        return null;
    }


    private RoadPostalcodeBitemporalRecord getRoadPostalCodeDK(dk.magenta.datafordeler.cpr.records.road.data.RoadEntity roadEntity, String houseNumber) {
        OffsetDateTime now = OffsetDateTime.now();
        List<RoadPostalcodeBitemporalRecord> postalCodeRecords = CprRecordFilter.filterRecordsByRegistrationAndEffect(roadEntity.getPostcode(), now);
        for (RoadPostalcodeBitemporalRecord postcodeRecord : postalCodeRecords) {
            if (houseNumber != null) {//All this is probably ok, I copied it from working functionality after refactoring gladdreg out of the project
                Matcher m = houseNumberPattern.matcher(houseNumber);
                if (m.find()) {
                    int numberPart = Integer.parseInt(m.group(1));
                    String letterPart = m.group(2).toLowerCase();
                    Matcher from = houseNumberPattern.matcher(postcodeRecord.getFromHousenumber());
                    Matcher to = houseNumberPattern.matcher(postcodeRecord.getToHousenumber());
                    if (from.find() && to.find()) {
                        int fromNumber = Integer.parseInt(from.group(1));
                        int toNumber = Integer.parseInt(to.group(1));
                        if (fromNumber < numberPart && numberPart < toNumber) {
                            return postcodeRecord;
                        } else if (fromNumber == numberPart || numberPart == toNumber) {
                            String fromLetter = from.group(2).toLowerCase();
                            String toLetter = from.group(2).toLowerCase();
                            if ((fromNumber < numberPart || fromLetter.isEmpty() || fromLetter.compareTo(letterPart) <= 0) && (numberPart < toNumber || toLetter.isEmpty() || letterPart.compareTo(toLetter) < 0)) {
                                return postcodeRecord;
                            }
                        }
                    }
                } else {
                    return postcodeRecord;
                }
            }
        }
        return null;
    }



    private static void setQueryNow(BaseQuery query) {
        OffsetDateTime now = OffsetDateTime.now();
        query.setRegistrationFrom(now);
        query.setRegistrationTo(now);
        query.setEffectFrom(now);
        query.setEffectTo(now);
    }

}
