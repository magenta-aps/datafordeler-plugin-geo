package dk.magenta.datafordeler.geo.data.municipality;

import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.fapi.FapiService;
import dk.magenta.datafordeler.geo.data.GeoEntityManager;
import dk.magenta.datafordeler.geo.data.RawData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class MunicipalityEntityManager extends GeoEntityManager<MunicipalityEntity, MunicipalityRawData> {

    //@Autowired
    //private PersonEntityService personEntityService;

    @Autowired
    private SessionManager sessionManager;

    public MunicipalityEntityManager() {
    }

    @Override
    protected String getBaseName() {
        return "municipality";
    }

    /*@Override
    public PersonEntityService getEntityService() {
        //return this.personEntityService;
        return null;
    }*/
    @Override
    public FapiService getEntityService() {
        return null;
    }

    @Override
    public String getDomain() {
        return "https://data.gl/geo/municipality/1/rest";
    }

    @Override
    public String getSchema() {
        return MunicipalityEntity.schema;
    }

    @Override
    protected SessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Override
    protected Class<MunicipalityEntity> getEntityClass() {
        return MunicipalityEntity.class;
    }


    @Override
    protected Class<MunicipalityRawData> getRawClass() {
        return MunicipalityRawData.class;
    }

    @Override
    protected UUID generateUUID(MunicipalityRawData rawData) {
        return rawData.properties.getSumiffiikAsUUID();
    }

    @Override
    protected MunicipalityEntity createBasicEntity(MunicipalityRawData record) {
        return new MunicipalityEntity(record);
    }
/*
    @Override
    protected void parseAlternate(MunicipalityEntity entity, Collection<PersonDataRecord> records, ImportMetadata importMetadata) {
        OffsetDateTime updateTime = importMetadata.getImportTime();
        for (PersonDataRecord record : records) {
            for (CprBitemporalRecord bitemporalRecord : record.getBitemporalRecords()) {
                bitemporalRecord.setDafoUpdated(updateTime);
                entity.addBitemporalRecord((CprBitemporalPersonRecord) bitemporalRecord, importMetadata.getSession());
            }
        }
    }
*/
}
