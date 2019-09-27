package dk.magenta.datafordeler.geo;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.fapi.BaseQuery;

import dk.magenta.datafordeler.core.util.DoubleHashMap;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressEntity;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressQuery;
import dk.magenta.datafordeler.geo.data.locality.GeoLocalityEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityQuery;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityQuery;
import dk.magenta.datafordeler.geo.data.postcode.PostcodeEntity;
import dk.magenta.datafordeler.geo.data.road.GeoRoadEntity;
import dk.magenta.datafordeler.geo.data.road.RoadQuery;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class GeoLookupService {

    public static final boolean COMPENSATE_2018_MUNICIPALITY_SPLIT = true;
    private Session session;
    private static Pattern houseNumberPattern = Pattern.compile("(\\d+)(.*)");

    public GeoLookupService(Session session) {
        this.session = session;
    }

    public Session getSession() {
        return this.session;
    }

    private DoubleHashMap<Integer, Integer, GeoLookupDTO> cache = new DoubleHashMap<>();

    public GeoLookupDTO doLookup(int municipalityCode, int roadCode) {
        return this.doLookup(municipalityCode, roadCode, null);
    }

    public GeoLookupDTO doLookup(int municipalityCode, int roadCode, String houseNumber) {

        GeoLookupDTO geoLookupDTO = new GeoLookupDTO();

        if (COMPENSATE_2018_MUNICIPALITY_SPLIT && (municipalityCode == 959 || municipalityCode == 960)) {
            municipalityCode = 958;
        }
        GeoLocalityEntity municipalityEntity;
        if (municipalityCacheGR.containsKey(municipalityCode)) {
            municipalityEntity = municipalityCacheGR.get(municipalityCode);
            if (municipalityEntity != null) {
                geoLookupDTO.setMunicipalityName(municipalityEntity.getName().iterator().next().getName());
            }
        } else {
            MunicipalityQuery query = new MunicipalityQuery();
            query.addKommunekodeRestriction(Integer.toString(municipalityCode));
            setQueryNow(query);
            List<GeoLocalityEntity> localities = QueryManager.getAllEntities(session, query, GeoLocalityEntity.class);

            if(localities != null && localities.size()>0) {
                geoLookupDTO.setMunicipalityName(localities.get(0).getName().iterator().next().getName());
            }
        }

        RoadQuery rq = new RoadQuery();
        rq.addKommunekodeRestriction(Integer.toString(municipalityCode));
        rq.addCode(Integer.toString(roadCode));
        setQueryNow(rq);
        List<GeoRoadEntity> roadEntities = QueryManager.getAllEntities(session, rq, GeoRoadEntity.class);
        if(roadEntities != null && roadEntities.size()>0) {
            geoLookupDTO.setRoadName(roadEntities.get(0).getName().iterator().next().getName());
            geoLookupDTO.setLocalityCode(roadEntities.get(0).getLocality().iterator().next().getCode());
        }



        AccessAddressQuery bq = new AccessAddressQuery();
        bq.addKommunekodeRestriction(Integer.toString(municipalityCode));
        bq.addHouseNumber(houseNumber);
        setQueryNow(bq);
        List<AccessAddressEntity> accAdd = QueryManager.getAllEntities(session, bq, AccessAddressEntity.class);
        if(accAdd != null && accAdd.size()>0) {
            geoLookupDTO.setbNumber(accAdd.get(0).getBnr());
            geoLookupDTO.setPostalCode(accAdd.get(0).getPostcode().iterator().next().getPostcode());
            PostcodeEntity entity = QueryManager.getEntity(session, PostcodeEntity.generateUUID(geoLookupDTO.getPostalCode()), PostcodeEntity.class);
            geoLookupDTO.setPostalDistrict(entity.getName().iterator().next().getName());
        }

        LocalityQuery lq = new LocalityQuery();//lookup.localityCode
        lq.setCode(geoLookupDTO.getLocalityCode());
        List<GeoLocalityEntity> localities = QueryManager.getAllEntities(session, lq, GeoLocalityEntity.class);

        if(localities != null && localities.size()>0) {
            geoLookupDTO.setLocalityName(localities.get(0).getName().iterator().next().getName());
            geoLookupDTO.setLocalityAbbrev(localities.get(0).getAbbreviation().iterator().next().getName());
        }


        return geoLookupDTO;
    }





    private HashMap<Integer, GeoLocalityEntity> municipalityCacheGR = new HashMap<>();

    private static void setQueryNow(BaseQuery query) {
        OffsetDateTime now = OffsetDateTime.now();
        query.setRegistrationFrom(now);
        query.setRegistrationTo(now);
        query.setEffectFrom(now);
        query.setEffectTo(now);
    }

}
