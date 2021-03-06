package dk.magenta.datafordeler.geo.data.road;

import dk.magenta.datafordeler.geo.data.GeoEntityManager;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("GeoRoadEntityManager")
public class RoadEntityManager extends GeoEntityManager<GeoRoadEntity, RoadRawData> {

    @Autowired
    private RoadService roadService;

    public RoadEntityManager() {
    }

    @Override
    protected String getBaseName() {
        return "road";
    }

    @Override
    public RoadService getEntityService() {
        return this.roadService;
    }

    @Override
    public String getDomain() {
        return "https://data.gl/geo/road/1/rest";
    }

    @Override
    public String getSchema() {
        return GeoRoadEntity.schema;
    }

    @Override
    protected Class<GeoRoadEntity> getEntityClass() {
        return GeoRoadEntity.class;
    }


    @Override
    protected Class<RoadRawData> getRawClass() {
        return RoadRawData.class;
    }

    @Override
    protected UUID generateUUID(RoadRawData rawData) {
        return rawData.properties.getUUID();
    }

    @Override
    protected GeoRoadEntity createBasicEntity(RoadRawData record, Session session) {
        return new GeoRoadEntity(record);
    }

}
