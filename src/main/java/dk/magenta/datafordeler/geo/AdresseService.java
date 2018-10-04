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
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.core.util.LoggerHelper;
import dk.magenta.datafordeler.geo.data.accessaddress.*;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;
import dk.magenta.datafordeler.geo.data.locality.*;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntity;
import dk.magenta.datafordeler.geo.data.road.*;
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
import java.util.*;

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
    public static final String OUTPUT_LOCALITYROADCODE = "lokalitetvejkode";
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

                LocalityNameRecord nameRecord = current(locality.getName());
                if (nameRecord != null) {
                    localityNode.put(OUTPUT_NAME, nameRecord.getName());
                }
                LocalityAbbreviationRecord abbreviationRecord = current(locality.getAbbreviation());
                if (abbreviationRecord != null) {
                    localityNode.put(OUTPUT_ABBREVIATION, abbreviationRecord.getName());
                }
                LocalityRoadcodeRecord localityRoadcodeRecord = current(locality.getLocalityRoadcode());
                if (localityRoadcodeRecord != null) {
                    localityNode.put(OUTPUT_LOCALITYROADCODE, localityRoadcodeRecord.getCode());
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
            ListHashMap<String, RoadEntity> roadMap = new ListHashMap<>();
            for (RoadEntity road : roads) {
                for (RoadNameRecord nameRecord : road.getName()) {
                    if (nameRecord.getRegistrationTo() == null) {
                        roadMap.add(nameRecord.getName() != null ? nameRecord.getName().trim() : null, road);
                    }
                }
            }

            if (roadMap.size() == 0 || (roadMap.size() == 1 && roadMap.keySet().contains(null))) {
                LocalityEntity localityEntity = QueryManager.getEntity(session, locality, LocalityEntity.class);
                if (localityEntity != null) {
                    ObjectNode roadNode = objectMapper.createObjectNode();
                    LocalityNameRecord localityNameRecord = current(localityEntity.getName());
                    if (localityNameRecord != null) {
                        roadNode.put(OUTPUT_NAME, localityNameRecord.getName());
                    }
                    LocalityRoadcodeRecord localityRoadcodeRecord = current(localityEntity.getLocalityRoadcode());
                    if (localityRoadcodeRecord != null) {
                        roadNode.put(OUTPUT_ROADCODE, localityRoadcodeRecord.getCode());
                    }
                    roadNode.put(OUTPUT_UUID, locality.toString());
                    results.add(roadNode);
                }
            }

            for (String roadName : roadMap.keySet()) {
                if (roadName != null) {
                    ObjectNode roadNode = objectMapper.createObjectNode();
                    roadNode.put(OUTPUT_NAME, roadName);

                    boolean hasUUID = false;
                    for (RoadEntity road : roadMap.get(roadName)) {
                        RoadNameRecord nameRecord = current(road.getName());
                        if (nameRecord != null) {
                            roadNode.put(OUTPUT_ALTNAME, nameRecord.getAddressingName());
                        }
                        if (road.getCode() != 0) {
                            roadNode.put(OUTPUT_ROADCODE, road.getCode());
                        }
                        RoadMunicipalityRecord municipalityRecord = current(road.getMunicipality());
                        if (municipalityRecord != null) {
                            roadNode.put(OUTPUT_MUNICIPALITYCODE, municipalityRecord.getCode());
                        }
                        if (!hasUUID && road.getUUID() != null) {
                            roadNode.put(OUTPUT_UUID, road.getUUID().toString());
                            hasUUID = true;
                        }
                    }
                    results.add(roadNode);
                }
            }
            return results.toString();
        } finally {
            session.close();
        }
    }

    private Set<UUID> getWholeRoad(Session session, UUID roadSegment) {
        HashSet<UUID> uuids = new HashSet<>();
        uuids.add(roadSegment);
        RoadEntity roadEntity = QueryManager.getEntity(session, roadSegment, RoadEntity.class);
        if (roadEntity != null) {
            String localityCode = null;
            for (RoadLocalityRecord localityRecord : roadEntity.getLocality()) {
                if (localityRecord.getRegistrationTo() == null) {
                    localityCode = localityRecord.getCode();
                    break;
                }
            }
            String roadName = null;
            for (RoadNameRecord nameRecord : roadEntity.getName()) {
                if (nameRecord.getRegistrationTo() == null) {
                    roadName = nameRecord.getName();
                    break;
                }
            }

            if (localityCode != null && roadName != null) {
                RoadQuery query = new RoadQuery();
                query.setLocality(localityCode);
                query.setName(roadName);

                for (RoadEntity roadEntity1 : QueryManager.getAllEntities(session, query, RoadEntity.class)) {
                    UUID u = roadEntity1.getUUID();
                    if (u != null) {
                        uuids.add(u);
                    }
                }
            }
        }
        return uuids;
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

        accessAddressQuery.setLocalityUUID(road);
        List<AccessAddressEntity> addressEntities = QueryManager.getAllEntities(session, accessAddressQuery, AccessAddressEntity.class);
        if (addressEntities == null || addressEntities.isEmpty()) {
            accessAddressQuery.setLocalityUUID(null);
            if (road != null) {
                for (UUID uuid : this.getWholeRoad(session, road)) {
                    accessAddressQuery.addRoadUUID(uuid);
                }
            }
            addressEntities = QueryManager.getAllEntities(session, accessAddressQuery, AccessAddressEntity.class);
        }

        try {
            ArrayNode results = objectMapper.createArrayNode();

            if (!addressEntities.isEmpty()) {

                for (AccessAddressEntity addressEntity : addressEntities) {
                    ObjectNode addressNode = objectMapper.createObjectNode();
                    addressNode.set(OUTPUT_HOUSENUMBER, null);
                    addressNode.set(OUTPUT_BNUMBER, null);
                    addressNode.set(OUTPUT_BCALLNAME, null);

                    AccessAddressHouseNumberRecord houseNumber = current(addressEntity.getHouseNumber());
                    if (houseNumber != null) {
                        addressNode.put(OUTPUT_HOUSENUMBER, houseNumber.getNumber());
                    }

                    addressNode.put(OUTPUT_BNUMBER, addressEntity.getBnr());

                    AccessAddressBlockNameRecord blockName = current(addressEntity.getBlockName());
                    if (blockName != null) {
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
        ArrayNode results = objectMapper.createArrayNode();
        try {
            AccessAddressQuery query = new AccessAddressQuery();
            setQueryNow(query);
            setQueryNoLimit(query);

            StringJoiner where = new StringJoiner(" AND ");
            where.add("unit_usage.usage = 1");

            String roadQueryPart = "";
            if (roadUUID != null) {
                for (UUID uuid : this.getWholeRoad(session, roadUUID)) {
                    query.addRoadUUID(uuid);
                }
                roadQueryPart = "LEFT JOIN access.road access_road " +
                        "LEFT JOIN access_road.reference road_identification " +
                        "LEFT JOIN access.locality access_locality " +
                        "LEFT JOIN access_locality.reference locality_identification ";
                where.add("(road_identification.uuid IN :road OR locality_identification.uuid IN :road)");
            }

            String houseNumberQueryPart = "";
            if (houseNumber != null) {
                houseNumber = houseNumber.trim();
                query.addHouseNumber(houseNumber);
                query.addHouseNumber("0" + houseNumber);
                query.addHouseNumber("00" + houseNumber);
                houseNumberQueryPart = "LEFT JOIN access.houseNumber houseNumber ";
                where.add("houseNumber.number IN :hnr");
            }
            if (buildingNumber != null) {
                query.addBnr(buildingNumber.trim());
                where.add("access.bnr IN :bnr");
            }

            org.hibernate.query.Query databaseQuery = session.createQuery(
                    "SELECT DISTINCT unit, access FROM " + UnitAddressEntity.class.getCanonicalName() + " unit " +
                       "JOIN unit.usage unit_usage " +
                       "LEFT JOIN " + AccessAddressEntity.class.getCanonicalName() + " access ON unit.accessAddress = access.identification " +
                       roadQueryPart +
                       houseNumberQueryPart +
                       "WHERE " + where.toString() + " " +
                       "order by access.bnr"
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


            for (Object result : databaseQuery.getResultList()) {
                Object[] resultItems = (Object[]) result;
                UnitAddressEntity unitAddressEntity = (UnitAddressEntity) resultItems[0];
                AccessAddressEntity accessAddressEntity = (AccessAddressEntity) resultItems[1];

                ObjectNode addressNode = objectMapper.createObjectNode();

                addressNode.put(OUTPUT_UUID, unitAddressEntity.getUUID().toString());
                AccessAddressHouseNumberRecord houseNumberRecord = current(accessAddressEntity.getHouseNumber());
                if (houseNumberRecord != null) {
                    addressNode.put(OUTPUT_HOUSENUMBER, houseNumberRecord.getNumber());
                }
                AccessAddressBlockNameRecord blockname = current(accessAddressEntity.getBlockName());
                if (blockname != null) {
                    addressNode.put(OUTPUT_BCALLNAME, blockname.getName());
                }

                addressNode.put(OUTPUT_BNUMBER, accessAddressEntity.getBnr());

                UnitAddressFloorRecord floor = current(unitAddressEntity.getFloor());
                if (floor != null) {
                    String floorValue = floor.getFloor();
                    if (floorValue != null && !floorValue.isEmpty()) {
                        addressNode.put(OUTPUT_FLOOR, floorValue);
                    }
                }
                UnitAddressDoorRecord door = current(unitAddressEntity.getDoor());
                if (door != null) {
                    String doorValue = door.getDoor();
                    if (doorValue != null && !doorValue.isEmpty()) {
                        addressNode.put(OUTPUT_DOOR, door.getDoor());
                    }
                }
                UnitAddressUsageRecord usage = current(unitAddressEntity.getUsage());
                if (usage != null) {
                    addressNode.put(OUTPUT_USAGE, usage.getUsage());
                }

                results.add(addressNode);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return results.toString();
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

                    "LEFT JOIN access.locality access_locality "+
                    "LEFT JOIN "+LocalityEntity.class.getCanonicalName()+" locality ON access_locality.reference = locality.identification "+

                    "WHERE unit_identification.uuid = :uuid order by access.bnr"
            );
            databaseQuery.setParameter("uuid", unitAddressUUID);
            databaseQuery.setFlushMode(FlushModeType.COMMIT);


            for (Object result : databaseQuery.getResultList()) {
                Object[] results = (Object[]) result;
                UnitAddressEntity unitAddress = results.length > 0 ? (UnitAddressEntity) results[0] : null;
                AccessAddressEntity accessAddress = results.length > 1 ? (AccessAddressEntity) results[1] : null;
                RoadEntity road = results.length > 2 ? (RoadEntity) results[2] : null;
                LocalityEntity locality = results.length > 3 ? (LocalityEntity) results[3] : null;

                if (unitAddress != null) {
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

                    UnitAddressFloorRecord floor = current(unitAddress.getFloor());
                    if (floor != null) {
                        String floorValue = floor.getFloor();
                        if (floorValue != null && !floorValue.isEmpty()) {
                            addressNode.put(OUTPUT_FLOOR, floorValue);
                        }
                    }
                    UnitAddressDoorRecord door = current(unitAddress.getDoor());
                    if (door != null) {
                        String doorValue = door.getDoor();
                        if (doorValue != null && !doorValue.isEmpty()) {
                            addressNode.put(OUTPUT_DOOR, doorValue);
                        }
                    }
                    UnitAddressUsageRecord usage = current(unitAddress.getUsage());
                    if (usage != null) {
                        addressNode.put(OUTPUT_USAGE, usage.getUsage());
                    }

                    if (accessAddress != null) {
                        AccessAddressHouseNumberRecord houseNumber = current(accessAddress.getHouseNumber());
                        if (houseNumber != null) {
                            addressNode.put(OUTPUT_HOUSENUMBER, houseNumber.getNumber());
                        }

                        addressNode.put(OUTPUT_BNUMBER, accessAddress.getBnr());

                        AccessAddressBlockNameRecord blockName = current(accessAddress.getBlockName());
                        if (blockName != null) {
                            addressNode.put(OUTPUT_BCALLNAME, blockName.getName());
                        }
                    }

                    if (road != null) {
                        addressNode.put(OUTPUT_ROADUUID, road.getUUID().toString());
                        RoadNameRecord roadName = current(road.getName());
                        if (roadName != null) {
                            addressNode.put(OUTPUT_ROADNAME, roadName.getName());
                        }
                        RoadMunicipalityRecord municipality = current(road.getMunicipality());
                        if (municipality != null) {
                            addressNode.put(OUTPUT_MUNICIPALITYCODE, municipality.getCode());
                        }
                    }

                    if (road != null && road.getCode() != 0) {
                        addressNode.put(OUTPUT_ROADCODE, road.getCode());
                    } else if (accessAddress != null) {
                        AccessAddressRoadRecord roadRecord = current(accessAddress.getRoad());
                        if (roadRecord != null) {
                            addressNode.put(OUTPUT_ROADCODE, roadRecord.getRoadCode());
                            addressNode.put(OUTPUT_MUNICIPALITYCODE, roadRecord.getMunicipalityCode());
                        }
                    }

                    if (locality != null) {
                        addressNode.put(OUTPUT_LOCALITYUUID, locality.getUUID().toString());
                        LocalityNameRecord localityName = current(locality.getName());
                        if (localityName != null) {
                            addressNode.put(OUTPUT_LOCALITYNAME, localityName.getName());
                        }
                        LocalityRoadcodeRecord localityRoadcode = current(locality.getLocalityRoadcode());
                        if (localityRoadcode != null && localityRoadcode.getCode() != null) {
                            addressNode.put(OUTPUT_ROADCODE, localityRoadcode.getCode());
                        }
                    }
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

    private static <T extends GeoMonotemporalRecord> T current(Collection<T> items) {
        ArrayList<T> candidates = new ArrayList<>();
        for (T item : items) {
            if (item.getRegistrationTo() == null) {
                candidates.add(item);
            }
        }
        if (candidates.size() > 1) {
            candidates.sort(Comparator.comparing(GeoMonotemporalRecord::getRegistrationFrom));
        }
        return candidates.isEmpty() ? null : candidates.get(candidates.size()-1);
    }
}
