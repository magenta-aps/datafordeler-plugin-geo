package dk.magenta.datafordeler.geo;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.fapi.BaseQuery;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressEntity;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressQuery;
import dk.magenta.datafordeler.geo.data.locality.GeoLocalityEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityQuery;
import dk.magenta.datafordeler.geo.data.municipality.GeoMunicipalityEntity;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityQuery;
import dk.magenta.datafordeler.geo.data.postcode.PostcodeEntity;
import dk.magenta.datafordeler.geo.data.road.GeoRoadEntity;
import dk.magenta.datafordeler.geo.data.road.RoadQuery;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;

public class GeoLookupService extends CprLookupService{

    private Logger log = LogManager.getLogger("dk.magenta.datafordeler.geo.GeoLookupService");

    public static final boolean COMPENSATE_2018_MUNICIPALITY_SPLIT = true;
    private HashMap<Integer, GeoMunicipalityEntity> municipalityCacheGR = new HashMap<>();

    public GeoLookupService(Session session) {
        super(session);
    }

    public Session getSession() {
        return super.getSession();
    }

    public GeoLookupDTO doLookup(int municipalityCode, int roadCode) {
        return this.doLookup(municipalityCode, roadCode, null);
    }

    public GeoLookupDTO doLookup(int municipalityCode, int roadCode, String houseNumber) {

        OffsetDateTime now = OffsetDateTime.now();

        if (municipalityCode < 950) {
            return super.doLookup(municipalityCode, roadCode, houseNumber);
        } else {
            GeoLookupDTO geoLookupDTO = new GeoLookupDTO();
            GeoMunicipalityEntity municipalityEntity = null;
            if (municipalityCacheGR.containsKey(municipalityCode)) {
                municipalityEntity = municipalityCacheGR.get(municipalityCode);
            } else {
                MunicipalityQuery query = new MunicipalityQuery();
                query.addKommunekodeRestriction(Integer.toString(municipalityCode));
                setQueryNow(query);
                List<GeoMunicipalityEntity> municipilalicities = QueryManager.getAllEntities(super.getSession(), query, GeoMunicipalityEntity.class);
                for(GeoMunicipalityEntity municipilalicity : municipilalicities) {
                    municipalityCacheGR.put(municipilalicity.getCode(), municipilalicity);
                }
                municipalityEntity = municipalityCacheGR.get(municipalityCode);
            }
            if (municipalityEntity != null) {
                geoLookupDTO.setMunicipalityName(municipalityEntity.getName().iterator().next().getName());
            }


            RoadQuery rq = new RoadQuery();
            rq.setMunicipality(Integer.toString(municipalityCode));
            rq.setCode(Integer.toString(roadCode));
            setQueryNow(rq);
            List<GeoRoadEntity> roadEntities = QueryManager.getAllEntities(super.getSession(), rq, GeoRoadEntity.class);

            log.error("GeoRoadEntitySize " + roadEntities.size());
            if (roadEntities != null && roadEntities.size() > 0) {
                geoLookupDTO.setRoadName(roadEntities.get(0).getName().iterator().next().getName());
                geoLookupDTO.setLocalityCode(roadEntities.get(0).getLocality().iterator().next().getCode());
            }


            AccessAddressQuery bq = new AccessAddressQuery();
            bq.setMunicipality(Integer.toString(municipalityCode));
            bq.setHouseNumber(houseNumber);
            bq.setRoad(roadCode);
            setQueryNow(bq);
            List<AccessAddressEntity> accessAddress = QueryManager.getAllEntities(super.getSession(), bq, AccessAddressEntity.class);
            log.info("AccessAddressEntitySize " + accessAddress.size());
            if (accessAddress != null && accessAddress.size() > 0) {
                geoLookupDTO.setbNumber(accessAddress.get(0).getBnr());
                geoLookupDTO.setPostalCode(accessAddress.get(0).getPostcode().iterator().next().getPostcode());
                PostcodeEntity entity = QueryManager.getEntity(super.getSession(), PostcodeEntity.generateUUID(geoLookupDTO.getPostalCode()), PostcodeEntity.class);
                geoLookupDTO.setPostalDistrict(entity.getName().iterator().next().getName());
            }

            LocalityQuery localityQuery = new LocalityQuery();
            localityQuery.setCode(geoLookupDTO.getLocalityCode());
            localityQuery.setMunicipality(Integer.toString(municipalityCode));
            List<GeoLocalityEntity> localities = QueryManager.getAllEntities(super.getSession(), localityQuery, GeoLocalityEntity.class);
            log.error("GeoLocalityEntitySize " + localities.size());
            if (localities != null && localities.size() > 0) {
                geoLookupDTO.setLocalityName(localities.get(0).getName().iterator().next().getName());
                geoLookupDTO.setLocalityAbbrev(localities.get(0).getAbbreviation().iterator().next().getName());
            }
            return geoLookupDTO;
        }

    }


    public String getPostalCodeDistrict(int code) {
        PostcodeEntity entity = QueryManager.getEntity(super.getSession(), PostcodeEntity.generateUUID(code), PostcodeEntity.class);
        return entity.getName().iterator().next().getName();
    }

    private static void setQueryNow(BaseQuery query) {
        OffsetDateTime now = OffsetDateTime.now();
        query.setRegistrationFrom(now);
        query.setRegistrationTo(now);
        query.setEffectFrom(now);
        query.setEffectTo(now);
    }

}
