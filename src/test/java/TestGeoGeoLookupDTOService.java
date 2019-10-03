import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.geo.GeoLookupDTO;
import dk.magenta.datafordeler.geo.GeoLookupService;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressEntityManager;
import dk.magenta.datafordeler.geo.data.building.BuildingEntityManager;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntityManager;
import dk.magenta.datafordeler.geo.data.municipality.GeoMunicipalityEntity;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntityManager;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityQuery;
import dk.magenta.datafordeler.geo.data.postcode.PostcodeEntityManager;
import dk.magenta.datafordeler.geo.data.road.RoadEntityManager;
import dk.magenta.datafordeler.geo.data.unitaddress.UnitAddressEntityManager;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestGeoGeoLookupDTOService extends GeoTest {

    @Autowired
    private ObjectMapper objectMapper;


    @Before
    public void initialize() throws Exception {
        this.loadAll();
        this.loadCprAddress();
    }



    @Test
    public void testLookupService() throws IOException {

        Session session = sessionManager.getSessionFactory().openSession();
        GeoLookupService lookupService = new GeoLookupService(session);

        GeoLookupDTO geoLookupDTO = lookupService.doLookup(956, 254, "18");

        Assert.assertEquals("Kommuneqarfik Sermersooq", geoLookupDTO.getMunicipalityName());
        Assert.assertEquals("B-3197B", geoLookupDTO.getbNumber());
        Assert.assertEquals("Qarsaalik", geoLookupDTO.getRoadName());
        Assert.assertEquals("0600", geoLookupDTO.getLocalityCode());
        Assert.assertEquals("NUK", geoLookupDTO.getLocalityAbbrev());
        Assert.assertEquals("Nuuk", geoLookupDTO.getLocalityName());
    }





    @Test
    public void testLookupServiceDk() throws IOException {

        Session session = sessionManager.getSessionFactory().openSession();


        MunicipalityQuery query = new MunicipalityQuery();
        query.addKommunekodeRestriction("1234");
        List<GeoMunicipalityEntity> localities = QueryManager.getAllEntities(session, query, GeoMunicipalityEntity.class);
        GeoLookupService lookupService = new GeoLookupService(session);

        GeoLookupDTO geoLookupDTO = lookupService.doLookup(730, 1, "18");

        Assert.assertEquals("Randers Kommune", geoLookupDTO.getMunicipalityName());
        Assert.assertEquals(null, geoLookupDTO.getbNumber());
        Assert.assertEquals("Aage Beks Vej", geoLookupDTO.getRoadName());
        //Assert.assertEquals("0600", geoLookupDTO.getLocalityCode());



        geoLookupDTO = lookupService.doLookup(730, 4, "18");

        Assert.assertEquals("Randers Kommune", geoLookupDTO.getMunicipalityName());
        Assert.assertEquals(null, geoLookupDTO.getbNumber());
        Assert.assertEquals("Aalborggade", geoLookupDTO.getRoadName());
        //Assert.assertEquals("0600", geoLookupDTO.getLocalityCode());
    }

}