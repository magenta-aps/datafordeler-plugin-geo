import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.geo.GeoLookupDTO;
import dk.magenta.datafordeler.geo.GeoLookupService;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressEntityManager;
import dk.magenta.datafordeler.geo.data.building.BuildingEntityManager;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntityManager;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntityManager;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestGeoGeoLookupDTOService extends GeoTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LocalityEntityManager localityEntityManager;

    @Autowired
    private RoadEntityManager roadEntityManager;

    @Autowired
    private BuildingEntityManager buildingEntityManager;

    @Autowired
    private MunicipalityEntityManager municipalityEntityManager;

    @Autowired
    private PostcodeEntityManager postcodeEntityManager;

    @Autowired
    private AccessAddressEntityManager accessAddressEntityManager;

    @Autowired
    private UnitAddressEntityManager unitAddressEntityManager;

    @Before
    public void initialize() throws Exception {
        this.load(localityEntityManager, "/locality.json");
        this.load(roadEntityManager,"/road.json");
        this.load(unitAddressEntityManager, "/unit.json");
        this.load(municipalityEntityManager, "/municipality.json");
        this.load(postcodeEntityManager, "/post.json");
        this.load(buildingEntityManager, "/building.json");
        this.load(accessAddressEntityManager, "/access.json");
    }



    @Test
    public void testLookupService() throws IOException {

        Session session = sessionManager.getSessionFactory().openSession();
        GeoLookupService ll = new GeoLookupService(session);

        GeoLookupDTO geoLookupDTO = ll.doLookup(956, 254, "18");

        Assert.assertEquals("Nuuk", geoLookupDTO.getMunicipalityName());
        Assert.assertEquals("B-3197B", geoLookupDTO.getbNumber());
        Assert.assertEquals("Qarsaalik", geoLookupDTO.getRoadName());
        Assert.assertEquals("0600", geoLookupDTO.getLocalityCode());
    }

}