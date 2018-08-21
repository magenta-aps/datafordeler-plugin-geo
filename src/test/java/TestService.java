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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestService {


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MunicipalityEntityManager municipalityEntityManager;

    @Autowired
    private LocalityEntityManager localityEntityManager;

    @Autowired
    private RoadEntityManager roadEntityManager;

    @Autowired
    private BuildingEntityManager buildingEntityManager;

    @Autowired
    private AccessAddressEntityManager accessAddressEntityManager;

    @Autowired
    private UnitAddressEntityManager unitAddressEntityManager;

    @Autowired
    private PostcodeEntityManager postcodeEntityManager;


    @Autowired
    private AdresseService adresseService;

    private ResponseEntity<String> restSearch(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", headers);
        return this.restTemplate.exchange(url, HttpMethod.GET, httpEntity, String.class);
    }
/*
    @Test
    public void testMunicipality() throws DataFordelerException, IOException {
        FileInputStream data = new FileInputStream(new File("fixtures/Kommune.json"));
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        importMetadata.setSession(session);
        try {
            municipalityEntityManager.parseData(data, importMetadata);

            MunicipalityEntity kujalleq = QueryManager.getEntity(session, UUID.fromString("96C57A43-5761-45E6-83D0-F329A10B0AEC"), MunicipalityEntity.class);
            Assert.assertNotNull(kujalleq);
            Assert.assertEquals(955, kujalleq.getCode());
            Assert.assertEquals(OffsetDateTime.parse("2018-07-19T11:11:05Z"), kujalleq.getCreationDate());
            Assert.assertEquals("GREENADMIN", kujalleq.getCreator());
            Assert.assertEquals(1, kujalleq.getName().size());
            Assert.assertEquals("Kommune Kujalleq", kujalleq.getName().iterator().next().getName());

            ResponseEntity<String> kujalleqResponse = this.uuidSearch("96C57A43-5761-45E6-83D0-F329A10B0AEC", "municipality");
            Assert.assertEquals(200, kujalleqResponse.getStatusCode().value());

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            importMetadata.setTransactionInProgress(false);
            session.close();
        }
    }
*/
    @Test
    public void testLocality() throws DataFordelerException, IOException {
        FileInputStream data = new FileInputStream(new File("fixtures/Lokalitet.json"));
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        importMetadata.setSession(session);
        try {
            localityEntityManager.parseData(data, importMetadata);

            transaction.commit();

            ResponseEntity<String> response = this.restSearch("/adresse/foo");
            Assert.assertEquals(200, response.getStatusCode().value());
            String localities = adresseService.getLocalities("955");

            System.out.println(localities);

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            data.close();
            importMetadata.setTransactionInProgress(false);
            session.close();
        }
    }


    @Test
    public void testRoad() throws DataFordelerException, IOException {
        FileInputStream data = new FileInputStream(new File("fixtures/Vejmidte.json"));
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        importMetadata.setSession(session);
        try {
            roadEntityManager.parseData(data, importMetadata);

            transaction.commit();

            String roads = adresseService.getRoads("1234");

            System.out.println(roads);

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            data.close();
            importMetadata.setTransactionInProgress(false);
            session.close();
        }
    }


    @Test
    public void testAccessAddress() throws DataFordelerException, IOException {
        FileInputStream data = new FileInputStream(new File("fixtures/Adgangsadresse.json"));
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        importMetadata.setSession(session);
        try {
            accessAddressEntityManager.parseData(data, importMetadata);

            transaction.commit();

            String buildings = adresseService.getAccessAddresses(956, 158);

            System.out.println(buildings);

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            data.close();
            importMetadata.setTransactionInProgress(false);
            session.close();
        }
    }

    @Test
    public void testUnitAddress() throws DataFordelerException, IOException {
        FileInputStream accessData = new FileInputStream(new File("fixtures/Adgangsadresse.json"));
        FileInputStream unitData = new FileInputStream(new File("fixtures/Enhedsadresse.json"));
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        importMetadata.setSession(session);
        try {
            accessAddressEntityManager.parseData(accessData, importMetadata);
            unitAddressEntityManager.parseData(unitData, importMetadata);

            transaction.commit();

            String buildings = adresseService.getUnitAddresses(956, 158, "5", null);

            System.out.println(buildings);

        } catch (Exception e) {
            e.printStackTrace();
            transaction.rollback();
        } finally {
            accessData.close();
            unitData.close();
            importMetadata.setTransactionInProgress(false);
            session.close();
        }
    }
/*

    @Test
    public void testAccessAddress() throws DataFordelerException, IOException {
        FileInputStream data = new FileInputStream(new File("fixtures/Bygning.json"));
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        importMetadata.setSession(session);
        try {
            buildingEntityManager.parseData(data, importMetadata);

            BuildingEntity b1025 = QueryManager.getEntity(session, UUID.fromString("3250B104-5F67-43A5-B6A8-1BEC88476C26"), BuildingEntity.class);
            Assert.assertNotNull(b1025);
            System.out.println(b1025.getAnr());
            Assert.assertEquals(null, b1025.getAnr());
            Assert.assertEquals("B-1025", b1025.getBnr());
            Assert.assertEquals(OffsetDateTime.parse("2018-07-19T07:21:03Z"), b1025.getCreationDate());
            Assert.assertEquals("IRKS", b1025.getCreator());
            Assert.assertEquals(1, b1025.getUsage().size());
            Assert.assertEquals(Integer.valueOf(0), b1025.getUsage().iterator().next().getUsage());
            Assert.assertEquals(OffsetDateTime.parse("2018-07-19T07:23:27Z"), b1025.getUsage().iterator().next().getRegistrationFrom());

            ResponseEntity<String> b1025Response = this.uuidSearch("3250B104-5F67-43A5-B6A8-1BEC88476C26", "building");
            Assert.assertEquals(200, b1025Response.getStatusCode().value());

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            importMetadata.setTransactionInProgress(false);
            session.close();
        }
    }


    @Test
    public void testAccessAddress() throws DataFordelerException, IOException {
        FileInputStream data = new FileInputStream(new File("fixtures/Adgangsadresse.json"));
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        importMetadata.setSession(session);
        try {
            accessAddressEntityManager.parseData(data, importMetadata);

            AccessAddressEntity b841 = QueryManager.getEntity(session, UUID.fromString("3E2C0668-E4C3-46A5-AEE8-AFEE74158DBE"), AccessAddressEntity.class);
            Assert.assertNotNull(b841);
            Assert.assertEquals("B-841", b841.getBnr());
            Assert.assertEquals(OffsetDateTime.parse("2018-07-19T10:15:31Z"), b841.getCreationDate());
            Assert.assertEquals("IRKS", b841.getCreator());

            ResponseEntity<String> b841Response = this.uuidSearch("3E2C0668-E4C3-46A5-AEE8-AFEE74158DBE", "accessaddress");
            Assert.assertEquals(200, b841Response.getStatusCode().value());

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            importMetadata.setTransactionInProgress(false);
            session.close();
        }
    }


    @Test
    public void testUnitAddress() throws DataFordelerException, IOException {
        FileInputStream data = new FileInputStream(new File("fixtures/Enhedsadresse.json"));
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        importMetadata.setSession(session);
        try {
            unitAddressEntityManager.parseData(data, importMetadata);

            UnitAddressEntity unit = QueryManager.getEntity(session, UUID.fromString("A77B5AD0-D54F-46D6-8641-2BF47EA1C9D6"), UnitAddressEntity.class);
            Assert.assertNotNull(unit);
            Assert.assertEquals(OffsetDateTime.parse("2018-07-19T10:09:13Z"), unit.getCreationDate());
            Assert.assertEquals("IRKS", unit.getCreator());

            ResponseEntity<String> unitResponse = this.uuidSearch("A77B5AD0-D54F-46D6-8641-2BF47EA1C9D6", "accessaddress");
            Assert.assertEquals(200, unitResponse.getStatusCode().value());

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            importMetadata.setTransactionInProgress(false);
            session.close();
        }
    }

    @Test
    public void testPostcode() throws DataFordelerException, IOException {
        FileInputStream data = new FileInputStream(new File("fixtures/Postnummer.json"));
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        importMetadata.setSession(session);
        try {
            postcodeEntityManager.parseData(data, importMetadata);

            PostcodeEntity santa = QueryManager.getEntity(session, PostcodeEntity.generateUUID(2412), PostcodeEntity.class);
            Assert.assertNotNull(santa);
            Assert.assertEquals(2412, santa.getCode());

            ResponseEntity<String> unitResponse = this.uuidSearch(PostcodeEntity.generateUUID(2412).toString(), "postcode");
            Assert.assertEquals(200, unitResponse.getStatusCode().value());

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            importMetadata.setTransactionInProgress(false);
            session.close();
        }
    }
*/
}