import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.geo.AdresseService;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressEntityManager;
import dk.magenta.datafordeler.geo.data.building.BuildingEntityManager;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntityManager;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntityManager;
import dk.magenta.datafordeler.geo.data.postcode.PostcodeEntityManager;
import dk.magenta.datafordeler.geo.data.road.RoadEntityManager;
import dk.magenta.datafordeler.geo.data.unitaddress.UnitAddressEntityManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestService extends GeoTest {

    @Autowired
    private LocalityEntityManager localityEntityManager;

    @Autowired
    private RoadEntityManager roadEntityManager;

    @Autowired
    private AccessAddressEntityManager accessAddressEntityManager;

    @Autowired
    private UnitAddressEntityManager unitAddressEntityManager;


    @Autowired
    private AdresseService adresseService;


    @Test
    public void testLocality() throws DataFordelerException, IOException {
        this.load(localityEntityManager, "fixtures/Lokalitet.json");
        ResponseEntity<String> response = this.lookup("/adresse/lokalitet?kommune=955");
        Assert.assertEquals(200, response.getStatusCode().value());
        String localities = response.getBody();
        System.out.println(localities);
    }


    @Test
    public void testRoad() throws DataFordelerException, IOException {
        this.load(roadEntityManager,"fixtures/Vejmidte.json");
        ResponseEntity<String> response = this.lookup("/adresse/vej?lokalitet=1234");
        Assert.assertEquals(200, response.getStatusCode().value());
        String roads = response.getBody();
        System.out.println(roads);
    }


    @Test
    public void testAccessAddress() throws DataFordelerException, IOException {
        this.load(accessAddressEntityManager, "fixtures/Adgangsadresse.json");
        ResponseEntity<String> response = this.lookup("/adresse/hus?lokalitet=1234");
        Assert.assertEquals(200, response.getStatusCode().value());
        String buildings = adresseService.getAccessAddresses(956, 158);
        System.out.println(buildings);
    }

    @Test
    public void testUnitAddress() throws DataFordelerException, IOException {
        this.load(accessAddressEntityManager, "fixtures/Adgangsadresse.json");
        this.load(unitAddressEntityManager, "fixtures/Enhedsadresse.json");
        ResponseEntity<String> response = this.lookup("/adresse/adresse?bnr=B-1234");
        Assert.assertEquals(200, response.getStatusCode().value());
        String addresses = response.getBody();
        System.out.println(addresses);
    }

}