package dk.magenta.datafordeler.geo.data;

import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.geo.data.building.BuildingEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityQuery;
import dk.magenta.datafordeler.geo.data.road.RoadEntity;
import dk.magenta.datafordeler.geo.data.road.RoadQuery;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class WireCache {

    public ListHashMap<String, LocalityEntity> localityCacheByCode = new ListHashMap<>();

    public List<LocalityEntity> getLocality(Session session, String code) {
        if (!this.localityCacheByCode.containsKey(code)) {
            LocalityQuery query = new LocalityQuery();
            query.setCode(code);
            List<LocalityEntity> list = QueryManager.getAllEntities(session, query, LocalityEntity.class);
            System.out.println("adding to localityCacheByCode");
            this.localityCacheByCode.add(code, list);
        }
        return this.localityCacheByCode.get(code);
    }


    public HashMap<UUID, LocalityEntity> localityCacheByUUID = new HashMap<>();

    public LocalityEntity getLocality(Session session, UUID uuid) {
        if (!this.localityCacheByUUID.containsKey(uuid)) {
            System.out.println("does not contain uuid "+uuid+" in "+this.localityCacheByUUID.size()+" keys, looking up");
            LocalityEntity entity = QueryManager.getEntity(session, uuid, LocalityEntity.class);
            System.out.println("adding to localityCacheByUUID");
            this.localityCacheByUUID.put(uuid, entity);
        } else {
            System.out.println("does contain uuid "+uuid+" in "+this.localityCacheByUUID.size()+" keys");
        }
        return this.localityCacheByUUID.get(uuid);
    }


    public HashMap<UUID, BuildingEntity> buildingCacheByUUID = new HashMap<>();

    public BuildingEntity getBuilding(Session session, UUID uuid) {
        if (!this.buildingCacheByUUID.containsKey(uuid)) {
            BuildingEntity entity = QueryManager.getEntity(session, uuid, BuildingEntity.class);
            System.out.println("adding to buildingCacheByUUID");
            this.buildingCacheByUUID.put(uuid, entity);
        }
        return this.buildingCacheByUUID.get(uuid);
    }


    public ListHashMap<Integer, RoadEntity> roadCacheByCode = new ListHashMap<>();

    public List<RoadEntity> getRoad(Session session, int municipalityCode, int roadCode) {
        Integer code = 10000 * municipalityCode + roadCode;
        if (!this.roadCacheByCode.containsKey(code)) {
            RoadQuery query = new RoadQuery();
            query.setMunicipality(Integer.toString(municipalityCode));
            query.setCode(Integer.toString(roadCode));
            List<RoadEntity> list = QueryManager.getAllEntities(session, query, RoadEntity.class);
            System.out.println("adding to roadCacheByCode");
            this.roadCacheByCode.add(code, list);
        }
        return this.roadCacheByCode.get(code);
    }

}
