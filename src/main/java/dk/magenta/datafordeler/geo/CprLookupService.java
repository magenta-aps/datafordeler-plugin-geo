package dk.magenta.datafordeler.geo;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.fapi.BaseQuery;
import dk.magenta.datafordeler.cpr.CprRecordFilter;
import dk.magenta.datafordeler.cpr.records.road.RoadRecordQuery;
import dk.magenta.datafordeler.cpr.records.road.data.RoadNameBitemporalRecord;
import dk.magenta.datafordeler.cpr.records.road.data.RoadPostalcodeBitemporalRecord;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CprLookupService {

    private Logger log = LogManager.getLogger("dk.magenta.datafordeler.geo.CprLookupService");

    private Session session;
    private static Pattern houseNumberPattern = Pattern.compile("(\\d+)(.*)");

    private static HashMap<Integer, String> municipalityCacheDK = new HashMap<>();
    
    
    static {
        municipalityCacheDK.put(101,"Københavns Kommune");
        municipalityCacheDK.put(147,"Frederiksberg Kommune");
        municipalityCacheDK.put(151,"Ballerup Kommune");
        municipalityCacheDK.put(153,"Brøndby Kommune");
        municipalityCacheDK.put(155,"Dragør Kommune");
        municipalityCacheDK.put(0157,"Gentofte Kommune");
        municipalityCacheDK.put(159,"Gladsaxe Kommune");
        municipalityCacheDK.put(161,"Glostrup Kommune");
        municipalityCacheDK.put(163,"Herlev Kommune");
        municipalityCacheDK.put(165,"Albertslund Kommune");
        municipalityCacheDK.put(167,"Hvidovre Kommune");
        municipalityCacheDK.put(169,"Høje Taastrup Kommune");
        municipalityCacheDK.put(173,"Lyngby-Taarbæk Kommune");
        municipalityCacheDK.put(175,"Rødovre Kommune");
        municipalityCacheDK.put(183,"Ishøj Kommune");
        municipalityCacheDK.put(185,"Tårnby Kommune");
        municipalityCacheDK.put(187,"Vallensbæk Kommune");
        municipalityCacheDK.put(190,"Furesø Kommune");
        municipalityCacheDK.put(201,"Allerød Kommune");
        municipalityCacheDK.put(210,"Fredensborg Kommune");
        municipalityCacheDK.put(217,"Helsingør Kommune");
        municipalityCacheDK.put(219,"Hillerød Kommune");
        municipalityCacheDK.put(223,"Hørsholm Kommune");
        municipalityCacheDK.put(230,"Rudersdal Kommune");
        municipalityCacheDK.put(240,"Egedal Kommune");
        municipalityCacheDK.put(250,"Frederikssund Kommune");
        municipalityCacheDK.put(253,"Greve Kommune");
        municipalityCacheDK.put(259,"Køge Kommune");
        municipalityCacheDK.put(260,"Halsnæs Kommune");
        municipalityCacheDK.put(265,"Roskilde Kommune");
        municipalityCacheDK.put(269,"Solrød Kommune");
        municipalityCacheDK.put(270,"Gribskov Kommune");
        municipalityCacheDK.put(306,"Odsherred Kommune");
        municipalityCacheDK.put(316,"Holbæk Kommune");
        municipalityCacheDK.put(320,"Faxe Kommune");
        municipalityCacheDK.put(326,"Kalundborg Kommune");
        municipalityCacheDK.put(329,"Ringsted Kommune");
        municipalityCacheDK.put(330,"Slagelse Kommune");
        municipalityCacheDK.put(336,"Stevns Kommune");
        municipalityCacheDK.put(340,"Sorø Kommune");
        municipalityCacheDK.put(350,"Lejre Kommune");
        municipalityCacheDK.put(360,"Lolland Kommune");
        municipalityCacheDK.put(370,"Næstved Kommune");
        municipalityCacheDK.put(376,"Guldborgsund Kommune");
        municipalityCacheDK.put(390,"Vordingborg Kommune");
        municipalityCacheDK.put(400,"Bornholms Regionskommune");
        municipalityCacheDK.put(410,"Middelfart Kommune");
        municipalityCacheDK.put(420,"Assens Kommune");
        municipalityCacheDK.put(430,"Faaborg-Midtfyn Kommune");
        municipalityCacheDK.put(440,"Kerteminde Kommune");
        municipalityCacheDK.put(450,"Nyborg Kommune");
        municipalityCacheDK.put(461,"Odense Kommune");
        municipalityCacheDK.put(479,"Svendborg Kommune");
        municipalityCacheDK.put(480,"Nordfyns Kommune");
        municipalityCacheDK.put(482,"Langeland Kommune");
        municipalityCacheDK.put(492,"Ærø Kommune");
        municipalityCacheDK.put(510,"Haderslev Kommune");
        municipalityCacheDK.put(530,"Billund Kommune");
        municipalityCacheDK.put(540,"Sønderborg Kommune");
        municipalityCacheDK.put(550,"Tønder Kommune");
        municipalityCacheDK.put(561,"Esbjerg Kommune");
        municipalityCacheDK.put(563,"Fanø Kommune");
        municipalityCacheDK.put(573,"Varde Kommune");
        municipalityCacheDK.put(575,"Vejen Kommune");
        municipalityCacheDK.put(580,"Aabenraa Kommune");
        municipalityCacheDK.put(607,"Fredericia Kommune");
        municipalityCacheDK.put(615,"Horsens Kommune");
        municipalityCacheDK.put(621,"Kolding Kommune");
        municipalityCacheDK.put(630,"Vejle Kommune");
        municipalityCacheDK.put(657,"Herning Kommune");
        municipalityCacheDK.put(661,"Holstebro Kommune");
        municipalityCacheDK.put(665,"Lemvig Kommune");
        municipalityCacheDK.put(671,"Struer Kommune");
        municipalityCacheDK.put(706,"Syddjurs Kommune");
        municipalityCacheDK.put(707,"Norddjurs Kommune");
        municipalityCacheDK.put(710,"Favrskov Kommune");
        municipalityCacheDK.put(727,"Odder Kommune");
        municipalityCacheDK.put(730,"Randers Kommune");
        municipalityCacheDK.put(740,"Silkeborg Kommune");
        municipalityCacheDK.put(741,"Samsø Kommune");
        municipalityCacheDK.put(746,"Skanderborg Kommune");
        municipalityCacheDK.put(751,"Aarhus Kommune");
        municipalityCacheDK.put(756,"Ikast-Brande Kommune");
        municipalityCacheDK.put(760,"Ringkøbing-Skjern Kommune");
        municipalityCacheDK.put(766,"Hedensted Kommune");
        municipalityCacheDK.put(773,"Morsø Kommune");
        municipalityCacheDK.put(779,"Skive Kommune");
        municipalityCacheDK.put(787,"Thisted Kommune");
        municipalityCacheDK.put(791,"Viborg Kommune");
        municipalityCacheDK.put(810,"Brønderslev Kommune");
        municipalityCacheDK.put(813,"Frederikshavn Kommune");
        municipalityCacheDK.put(820,"Vesthimmerlands Kommune");
        municipalityCacheDK.put(825,"Læsø Kommune");
        municipalityCacheDK.put(840,"Rebild Kommune");
        municipalityCacheDK.put(846,"Mariagerfjord Kommune");
        municipalityCacheDK.put(849,"Jammerbugt Kommune");
        municipalityCacheDK.put(851,"Aalborg Kommune");
        municipalityCacheDK.put(860,"Hjørring Kommune");
    }

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

        geoLookupDTO.setMunicipalityName(municipalityCacheDK.get(municipalityCode));
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
