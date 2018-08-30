package dk.magenta.datafordeler.geo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.InvalidClientInputException;
import dk.magenta.datafordeler.core.exception.MissingParameterException;
import dk.magenta.datafordeler.core.fapi.BaseQuery;
import dk.magenta.datafordeler.core.user.DafoUserDetails;
import dk.magenta.datafordeler.core.user.DafoUserManager;
import dk.magenta.datafordeler.core.util.LoggerHelper;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressBlockNameRecord;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressEntity;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressHouseNumberRecord;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressQuery;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityNameRecord;
import dk.magenta.datafordeler.geo.data.locality.LocalityQuery;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntity;
import dk.magenta.datafordeler.geo.data.road.RoadEntity;
import dk.magenta.datafordeler.geo.data.road.RoadMunicipalityRecord;
import dk.magenta.datafordeler.geo.data.road.RoadNameRecord;
import dk.magenta.datafordeler.geo.data.road.RoadQuery;
import dk.magenta.datafordeler.geo.data.unitaddress.UnitAddressDoorRecord;
import dk.magenta.datafordeler.geo.data.unitaddress.UnitAddressEntity;
import dk.magenta.datafordeler.geo.data.unitaddress.UnitAddressFloorRecord;
import dk.magenta.datafordeler.geo.data.unitaddress.UnitAddressUsageRecord;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.persistence.FlushModeType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

@RestController("GeoAdresseService")
@RequestMapping("/geo/adresse")
public class AdresseService {

    @Autowired
    SessionManager sessionManager;

    @Autowired
    private DafoUserManager dafoUserManager;

    @Autowired
    private ObjectMapper objectMapper;

    private Logger log = LoggerFactory.getLogger(AdresseService.class);

    public static final String PARAM_MUNICIPALITY = "kommune";
    public static final String PARAM_LOCALITY = "lokalitet";
    public static final String PARAM_ROAD = "vej";
    public static final String PARAM_HOUSE = "husnr";
    public static final String PARAM_BNR = "b_nummer";
    public static final String PARAM_ADDRESS = "adresse";

    public static final String OUTPUT_UUID = "uuid";
    public static final String OUTPUT_NAME = "navn";
    public static final String OUTPUT_ABBREVIATION = "forkortelse";
    public static final String OUTPUT_MUNICIPALITYCODE = "kommunekode";
    public static final String OUTPUT_LOCALITYUUID = "lokalitet";
    public static final String OUTPUT_LOCALITYNAME = "lokalitetsnavn";
    public static final String OUTPUT_ROADUUID = "vej_uuid";
    public static final String OUTPUT_ROADCODE = "vejkode";
    public static final String OUTPUT_ROADNAME = "vejnavn";
    public static final String OUTPUT_ALTNAME = "andet_navn";
    public static final String OUTPUT_CPRNAME = "cpr_navn";
    public static final String OUTPUT_SHORTNAME = "forkortet_navn";
    public static final String OUTPUT_BNUMBER = "b_nummer";
    public static final String OUTPUT_BCALLNAME = "b_kaldenavn";
    public static final String OUTPUT_HOUSENUMBER = "husnummer";
    public static final String OUTPUT_FLOOR = "etage";
    public static final String OUTPUT_DOOR = "doer";
    public static final String OUTPUT_USAGE = "anvendelse";



    HashMap<Integer, UUID> municipalities = new HashMap<>();

    /**
     * Load known municipalities into a local map of municipalityCode: UUID
     */
    @PostConstruct
    public void loadMunicipalities() {
        Session session = sessionManager.getSessionFactory().openSession();
        try {
            List<MunicipalityEntity> municipalities = QueryManager.getAllEntities(session, MunicipalityEntity.class);
            for (MunicipalityEntity municipality : municipalities) {
                this.municipalities.put(municipality.getCode(), municipality.getIdentification().getUuid());
            }
        } finally {
            session.close();
        }
    }


    /**
     * Finds all localities in a municipality. Only current data is included.
     * @param request HTTP request containing a municipality parameter
     * @return Json-formatted string containing a list of found objects
     */
    @RequestMapping("/lokalitet")
    public void getLocalities(HttpServletRequest request, HttpServletResponse response) throws DataFordelerException, IOException {
        String payload = this.getLocalities(request);
        this.setHeaders(response);
        response.getWriter().write(payload);
    }

    public String getLocalities(HttpServletRequest request) throws DataFordelerException {
        String municipalityCode = request.getParameter(PARAM_MUNICIPALITY);
        DafoUserDetails user = dafoUserManager.getUserFromRequest(request);
        LoggerHelper loggerHelper = new LoggerHelper(log, request, user);
        loggerHelper.info(
                "Incoming REST request for AddressService.locality with municipality {}", municipalityCode
        );
        checkParameterExistence(PARAM_MUNICIPALITY, municipalityCode);
        int code = parameterAsInt(PARAM_MUNICIPALITY, municipalityCode);
        return this.getLocalities(Integer.toString(code));
    }

    public String getLocalities(String municipality) {
        LocalityQuery query = new LocalityQuery();
        setQueryNow(query);
        setQueryNoLimit(query);
        query.setMunicipality(municipality);
        Session session = sessionManager.getSessionFactory().openSession();
        try {
            List<LocalityEntity> localities = QueryManager.getAllEntities(session, query, LocalityEntity.class);
            ArrayNode results = objectMapper.createArrayNode();
            for (LocalityEntity locality : localities) {
                ObjectNode localityNode = objectMapper.createObjectNode();
                localityNode.put(OUTPUT_UUID, locality.getUUID().toString());
                for (LocalityNameRecord nameRecord : locality.getName()) {
                    localityNode.put(OUTPUT_NAME, nameRecord.getName());
                }
                results.add(localityNode);
            }
            return results.toString();
        } finally {
            session.close();
        }
    }

    /**
     * Finds all roads in a locality. Only current data is included.
     * @param request HTTP request containing a locality parameter
     * @return Json-formatted string containing a list of found objects
     */
    @RequestMapping("/vej")
    public void getRoads(HttpServletRequest request, HttpServletResponse response) throws DataFordelerException, IOException {
        String payload = this.getRoads(request);
        setHeaders(response);
        response.getWriter().write(payload);
    }

    public String getRoads(HttpServletRequest request) throws DataFordelerException {
        String locality = request.getParameter(PARAM_LOCALITY);
        DafoUserDetails user = dafoUserManager.getUserFromRequest(request);
        LoggerHelper loggerHelper = new LoggerHelper(log, request, user);
        loggerHelper.info(
                "Incoming REST request for AddressService.road with locality {}", locality
        );
        checkParameterExistence(PARAM_LOCALITY, locality);
        return this.getRoads(parameterAsUUID(PARAM_LOCALITY, locality));
    }

    public String getRoads(UUID locality) throws DataFordelerException {

        RoadQuery query = new RoadQuery();
        setQueryNow(query);
        setQueryNoLimit(query);
        query.setLocalityUUID(locality);
        Session session = sessionManager.getSessionFactory().openSession();
        try {
            List<RoadEntity> roads = QueryManager.getAllEntities(session, query, RoadEntity.class);
            ArrayNode results = objectMapper.createArrayNode();
            for (RoadEntity road : roads) {
                ObjectNode roadNode = objectMapper.createObjectNode();

                roadNode.put(OUTPUT_UUID, road.getUUID().toString());
                roadNode.put(OUTPUT_ROADCODE, road.getCode());
                roadNode.set(OUTPUT_NAME, null);

                for (RoadNameRecord nameRecord : road.getName()) {
                    roadNode.put(OUTPUT_NAME, nameRecord.getName());
                    roadNode.put(OUTPUT_ALTNAME, nameRecord.getAddressingName());
                }

                for (RoadMunicipalityRecord municipality : road.getMunicipality()) {
                    roadNode.put(OUTPUT_MUNICIPALITYCODE, municipality.getCode());
                }

                results.add(roadNode);
            }
            return results.toString();
        } finally {
            session.close();
        }
    }

    /**
     * Finds all buildings on a road. Only current data is included.
     * @param request HTTP request containing a road parameter
     * @return Json-formatted string containing a list of found objects
     */
    @RequestMapping("/hus")
    public void getAccessAddresses(HttpServletRequest request, HttpServletResponse response) throws DataFordelerException, IOException {
        String payload = this.getAccessAddresses(request);
        setHeaders(response);
        response.getWriter().write(payload);
    }

    public String getAccessAddresses(HttpServletRequest request) throws DataFordelerException {
        String roadUUID = request.getParameter(PARAM_ROAD);
        DafoUserDetails user = dafoUserManager.getUserFromRequest(request);
        LoggerHelper loggerHelper = new LoggerHelper(log, request, user);
        loggerHelper.info(
                "Incoming REST request for AddressService.building with road {}", roadUUID
        );
        checkParameterExistence(PARAM_ROAD, roadUUID);
        UUID road = parameterAsUUID(PARAM_ROAD, roadUUID);
        return this.getAccessAddresses(road);
    }

    public String getAccessAddresses(UUID road) throws DataFordelerException {
        Session session = sessionManager.getSessionFactory().openSession();
        AccessAddressQuery accessAddressQuery = new AccessAddressQuery();
        setQueryNow(accessAddressQuery);
        setQueryNoLimit(accessAddressQuery);
        accessAddressQuery.setRoadUUID(road);

        try {
            ArrayNode results = objectMapper.createArrayNode();

            List<AccessAddressEntity> addressEntities = QueryManager.getAllEntities(session, accessAddressQuery, AccessAddressEntity.class);
            if (!addressEntities.isEmpty()) {

                for (AccessAddressEntity addressEntity : addressEntities) {
                    ObjectNode addressNode = objectMapper.createObjectNode();
                    addressNode.set(OUTPUT_HOUSENUMBER, null);
                    addressNode.set(OUTPUT_BNUMBER, null);
                    addressNode.set(OUTPUT_BCALLNAME, null);

                    for (AccessAddressHouseNumberRecord houseNumber : addressEntity.getHouseNumber()) {
                        addressNode.put(OUTPUT_HOUSENUMBER, houseNumber.getNumber());
                    }

                    addressNode.put(OUTPUT_BNUMBER, addressEntity.getBnr());

                    for (AccessAddressBlockNameRecord blockName : addressEntity.getBlockName()) {
                        addressNode.put(OUTPUT_BCALLNAME, blockName.getName());
                    }

                    results.add(addressNode);
                }
            }
            return results.toString();
        } finally {
            session.close();
        }
    }

    /**
     * Finds all addreses on a road, filtered by housenumber or bnumber.
     * Only current data is included.
     * @param request HTTP request containing a road parameter,
     *                and optionally a house parameter or bnr parameter
     * @return Json-formatted string containing a list of found objects
     */
    @RequestMapping("/adresse")
    public void getUnitAddresses(HttpServletRequest request, HttpServletResponse response) throws DataFordelerException, IOException {
        String payload = this.getUnitAddresses(request);
        setHeaders(response);
        response.getWriter().write(payload);
    }

    public String getUnitAddresses(HttpServletRequest request) throws DataFordelerException {
        String roadUUID = request.getParameter(PARAM_ROAD);
        String houseNumber = request.getParameter(PARAM_HOUSE);
        String buildingNumber = request.getParameter(PARAM_BNR);
        DafoUserDetails user = dafoUserManager.getUserFromRequest(request);
        LoggerHelper loggerHelper = new LoggerHelper(log, request, user);
        loggerHelper.info(
                "Incoming REST request for AddressService.address with road {}, houseNumber {}, bNumber {}", roadUUID, houseNumber, buildingNumber
        );
        checkParameterExistence(PARAM_ROAD, roadUUID);
        UUID road = parameterAsUUID(PARAM_ROAD, roadUUID);
        return this.getUnitAddresses(road, houseNumber, buildingNumber);
    }

    public String getUnitAddresses(UUID roadUUID, String houseNumber, String buildingNumber) throws DataFordelerException {
        if (houseNumber != null && houseNumber.trim().isEmpty()) {
            houseNumber = null;
        }
        if (buildingNumber != null && buildingNumber.trim().isEmpty()) {
            buildingNumber = null;
        }
        Session session = sessionManager.getSessionFactory().openSession();
        try {

            AccessAddressQuery query = new AccessAddressQuery();
            setQueryNow(query);
            setQueryNoLimit(query);

            StringJoiner where = new StringJoiner(" AND ");

            if (roadUUID != null) {
                query.setRoadUUID(roadUUID);
                where.add("road_identification.uuid IN :road");
            }
            if (houseNumber != null) {
                houseNumber = houseNumber.trim();
                query.addHouseNumber(houseNumber);
                query.addHouseNumber("0" + houseNumber);
                query.addHouseNumber("00" + houseNumber);
                where.add("houseNumber.number IN :hnr");
            }
            if (buildingNumber != null) {
                query.addBnr(buildingNumber.trim());
                where.add("access.bnr IN :bnr");
            }

            org.hibernate.query.Query databaseQuery = session.createQuery(
                    "SELECT DISTINCT unit, access FROM " + UnitAddressEntity.class.getCanonicalName() + " unit " +
                       "JOIN " + AccessAddressEntity.class.getCanonicalName() + " access ON unit.accessAddress = access.identification " +
                       "JOIN access.road access_road " +
                       "JOIN access_road.reference road_identification " +
                       (houseNumber != null ? "JOIN access.houseNumber houseNumber " : "") +
                       "WHERE " + where.toString()
            );

            if (roadUUID != null) {
                databaseQuery.setParameterList("road", query.getRoadUUID());
            }
            if (houseNumber != null) {
                databaseQuery.setParameterList("hnr", query.getHouseNumber());
            }
            if (buildingNumber != null) {
                databaseQuery.setParameterList("bnr", query.getBnr());
            }
            databaseQuery.setFlushMode(FlushModeType.COMMIT);


            ArrayNode results = objectMapper.createArrayNode();

            for (Object result : databaseQuery.getResultList()) {
                Object[] resultItems = (Object[]) result;
                UnitAddressEntity unitAddressEntity = (UnitAddressEntity) resultItems[0];
                AccessAddressEntity accessAddressEntity = (AccessAddressEntity) resultItems[1];

                ObjectNode addressNode = objectMapper.createObjectNode();

                addressNode.put(OUTPUT_UUID, unitAddressEntity.getUUID().toString());
                for (AccessAddressHouseNumberRecord houseNumberRecord : accessAddressEntity.getHouseNumber()) {
                    addressNode.put(OUTPUT_HOUSENUMBER, houseNumberRecord.getNumber());
                }
                for (AccessAddressBlockNameRecord blockname : accessAddressEntity.getBlockName()) {
                    addressNode.put(OUTPUT_BCALLNAME, blockname.getName());
                }

                addressNode.put(OUTPUT_BNUMBER, accessAddressEntity.getBnr());

                for (UnitAddressFloorRecord floor : unitAddressEntity.getFloor()) {
                    addressNode.put(OUTPUT_FLOOR, floor.getFloor());
                }
                for (UnitAddressDoorRecord door : unitAddressEntity.getDoor()) {
                    addressNode.put(OUTPUT_DOOR, door.getDoor());
                }
                for (UnitAddressUsageRecord usage : unitAddressEntity.getUsage()) {
                    addressNode.put(OUTPUT_USAGE, usage.getUsage());
                }

                results.add(addressNode);
            }

            return results.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return null;
    }

    /**
     * Finds all addreses on a road, filtered by housenumber or bnumber.
     * Only current data is included.
     * @param request HTTP request containing a road parameter,
     *                and optionally a house parameter or bnr parameter
     * @return Json-formatted string containing a list of found objects
     */
    @RequestMapping("/adresseoplysninger")
    public void getAddressData(HttpServletRequest request, HttpServletResponse response) throws DataFordelerException, IOException {
        String payload = this.getAddressData(request);
        setHeaders(response);
        response.getWriter().write(payload);
    }

    public String getAddressData(HttpServletRequest request) throws DataFordelerException {
        String addressUUID = request.getParameter(PARAM_ADDRESS);
        DafoUserDetails user = dafoUserManager.getUserFromRequest(request);
        LoggerHelper loggerHelper = new LoggerHelper(log, request, user);
        loggerHelper.info(
                "Incoming REST request for AddressService.addressdata with address {}", addressUUID
        );
        checkParameterExistence(PARAM_ADDRESS, addressUUID);
        UUID address = parameterAsUUID(PARAM_ADDRESS, addressUUID);
        return this.getAddressData(address);
    }

    public String getAddressData(UUID unitAddressUUID) throws DataFordelerException {

        Session session = sessionManager.getSessionFactory().openSession();
        try {
            // We only get bnumber references here, and must look them up in the bnumber table

            ObjectNode addressNode = objectMapper.createObjectNode();


            org.hibernate.query.Query databaseQuery = session.createQuery(
                    "SELECT DISTINCT unit, access, road, locality "+
                    "FROM "+UnitAddressEntity.class.getCanonicalName()+" unit "+
                    "JOIN unit.identification unit_identification "+

                    "LEFT JOIN "+AccessAddressEntity.class.getCanonicalName()+" access ON unit.accessAddress = access.identification "+

                    "LEFT JOIN access.road access_road "+
                    "LEFT JOIN "+RoadEntity.class.getCanonicalName()+" road ON access_road.reference = road.identification "+

                    "LEFT JOIN road.locality road_locality "+
                    "LEFT JOIN "+LocalityEntity.class.getCanonicalName()+" locality ON road_locality.reference = locality.identification "+

                    "WHERE unit_identification.uuid = :uuid "
            );
            databaseQuery.setParameter("uuid", unitAddressUUID);
            databaseQuery.setFlushMode(FlushModeType.COMMIT);


            for (Object result : databaseQuery.getResultList()) {
                Object[] results = (Object[]) result;
                UnitAddressEntity unitAddress = results.length > 0 ? (UnitAddressEntity) results[0] : null;
                AccessAddressEntity accessAddress = results.length > 1 ? (AccessAddressEntity) results[1] : null;
                RoadEntity road = results.length > 2 ? (RoadEntity) results[2] : null;
                LocalityEntity locality = results.length > 3 ? (LocalityEntity) results[3] : null;

                addressNode.put(OUTPUT_UUID, unitAddress.getUUID().toString());
                addressNode.set(OUTPUT_HOUSENUMBER, null);
                addressNode.set(OUTPUT_FLOOR, null);
                addressNode.set(OUTPUT_DOOR, null);
                addressNode.set(OUTPUT_BNUMBER, null);
                addressNode.set(OUTPUT_ROADUUID, null);
                addressNode.set(OUTPUT_ROADCODE, null);
                addressNode.set(OUTPUT_ROADNAME, null);
                addressNode.set(OUTPUT_LOCALITYUUID, null);
                addressNode.set(OUTPUT_LOCALITYNAME, null);
                addressNode.set(OUTPUT_MUNICIPALITYCODE, null);
                addressNode.set(OUTPUT_USAGE, null);

                for (UnitAddressFloorRecord floor : unitAddress.getFloor()) {
                    addressNode.put(OUTPUT_FLOOR, floor.getFloor());
                }
                for (UnitAddressDoorRecord door : unitAddress.getDoor()) {
                    addressNode.put(OUTPUT_DOOR, door.getDoor());
                }
                for (UnitAddressUsageRecord usage : unitAddress.getUsage()) {
                    addressNode.put(OUTPUT_USAGE, usage.getUsage());
                }

                for (AccessAddressHouseNumberRecord houseNumber : accessAddress.getHouseNumber()) {
                    addressNode.put(OUTPUT_HOUSENUMBER, houseNumber.getNumber());
                }

                addressNode.put(OUTPUT_BNUMBER, accessAddress.getBnr());

                for (AccessAddressBlockNameRecord blockName : accessAddress.getBlockName()) {
                    addressNode.put(OUTPUT_BCALLNAME, blockName.getName());
                }

                addressNode.put(OUTPUT_ROADUUID, road.getUUID().toString());

                addressNode.put(OUTPUT_ROADCODE, road.getCode());

                for (RoadNameRecord roadName : road.getName()) {
                    addressNode.put(OUTPUT_ROADNAME, roadName.getName());
                }

                addressNode.put(OUTPUT_LOCALITYUUID, locality.getUUID().toString());

                for (LocalityNameRecord localityName : locality.getName()) {
                    addressNode.put(OUTPUT_LOCALITYNAME, localityName.getName());
                }

                for (RoadMunicipalityRecord municipality : road.getMunicipality()) {
                    addressNode.put(OUTPUT_MUNICIPALITYCODE, municipality.getCode());
                }

            }
            return addressNode.toString();
        } finally {
            session.close();
        }
    }



    private static void checkParameterExistence(String name, String value) throws MissingParameterException {
        if (value == null || value.trim().isEmpty()) {
            throw new MissingParameterException(name);
        }
    }

    private static int parameterAsInt(String name, String value) throws InvalidClientInputException {
        try {
            return Integer.parseInt(value, 10);
        } catch (NumberFormatException e) {
            throw new InvalidClientInputException("Parameter "+name+" must be a number", e);
        }
    }

    private static UUID parameterAsUUID(String name, String value) throws InvalidClientInputException {
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidClientInputException("Parameter "+name+" must be a uuid", e);
        }
    }

    private static void setQueryNow(BaseQuery query) {
        OffsetDateTime now = OffsetDateTime.now();
        query.setRegistrationFrom(now);
        query.setRegistrationTo(now);
        query.setEffectFrom(now);
        query.setEffectTo(now);
    }
    private static void setQueryNoLimit(BaseQuery query) {
        query.setPage(1);
        query.setPageSize(Integer.MAX_VALUE);
    }
    private static void setHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-Type", "application/json; charset=utf-8");
    }
}
