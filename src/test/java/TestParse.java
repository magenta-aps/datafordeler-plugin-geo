import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.geo.data.building.BuildingEntity;
import dk.magenta.datafordeler.geo.data.building.BuildingEntityManager;
import dk.magenta.datafordeler.geo.data.building.BuildingService;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntityManager;
import dk.magenta.datafordeler.geo.data.locality.LocalityService;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntity;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntityManager;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityService;
import dk.magenta.datafordeler.geo.data.road.RoadEntity;
import dk.magenta.datafordeler.geo.data.road.RoadEntityManager;
import dk.magenta.datafordeler.geo.data.road.RoadService;
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
import java.time.OffsetDateTime;
import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestParse {


    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MunicipalityService municipalityService;

    @Autowired
    private MunicipalityEntityManager municipalityEntityManager;

    @Autowired
    private LocalityService localityService;

    @Autowired
    private LocalityEntityManager localityEntityManager;

    @Autowired
    private RoadService roadService;

    @Autowired
    private RoadEntityManager roadEntityManager;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private BuildingEntityManager buildingEntityManager;

    private ResponseEntity<String> restSearch(ParameterMap parameters, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", headers);
        return this.restTemplate.exchange("/geo/"+type+"/1/rest/search?" + parameters.asUrlParams(), HttpMethod.GET, httpEntity, String.class);
    }

    private ResponseEntity<String> uuidSearch(String id, String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");
        HttpEntity<String> httpEntity = new HttpEntity<String>("", headers);
        return this.restTemplate.exchange("/geo/"+type+"/1/rest/" + id, HttpMethod.GET, httpEntity, String.class);
    }

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

            LocalityEntity aadarujuupAqquserna = QueryManager.getEntity(session, UUID.fromString("32C1849A-6AB6-4358-B293-7D5EC69C3A19"), LocalityEntity.class);
            Assert.assertNotNull(aadarujuupAqquserna);
            Assert.assertEquals(null, aadarujuupAqquserna.getCode());
            Assert.assertEquals(OffsetDateTime.parse("2018-07-19T10:57:39Z"), aadarujuupAqquserna.getCreationDate());
            Assert.assertEquals("GREENADMIN", aadarujuupAqquserna.getCreator());
            Assert.assertEquals(1, aadarujuupAqquserna.getName().size());
            Assert.assertEquals("Aadarujuup Aqquserna nord", aadarujuupAqquserna.getName().iterator().next().getName());

            ResponseEntity<String> aadarujuupAqqusernaResponse = this.uuidSearch("32C1849A-6AB6-4358-B293-7D5EC69C3A19", "locality");
            Assert.assertEquals(200, aadarujuupAqqusernaResponse.getStatusCode().value());

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
    public void testRoad() throws DataFordelerException, IOException {
        FileInputStream data = new FileInputStream(new File("fixtures/Vejmidte.json"));
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        importMetadata.setTransactionInProgress(true);
        importMetadata.setSession(session);
        try {
            roadEntityManager.parseData(data, importMetadata);

            RoadEntity pujooriarfik = QueryManager.getEntity(session, UUID.fromString("DDF9075A-0B47-442B-BC0C-EFC296F67417"), RoadEntity.class);
            Assert.assertNotNull(pujooriarfik);
            Assert.assertEquals(0, pujooriarfik.getCode());
            Assert.assertEquals(OffsetDateTime.parse("2018-07-23T06:25:18Z"), pujooriarfik.getCreationDate());
            Assert.assertEquals("GREENADMIN", pujooriarfik.getCreator());
            Assert.assertEquals(1, pujooriarfik.getName().size());
            Assert.assertEquals("Pujooriarfik", pujooriarfik.getName().iterator().next().getName());
            Assert.assertEquals(OffsetDateTime.parse("2018-07-23T06:25:18Z"), pujooriarfik.getName().iterator().next().getRegistrationFrom());

            ResponseEntity<String> aadarujuupAqqusernaResponse = this.uuidSearch("DDF9075A-0B47-442B-BC0C-EFC296F67417", "locality");
            Assert.assertEquals(200, aadarujuupAqqusernaResponse.getStatusCode().value());

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
    public void testBuilding() throws DataFordelerException, IOException {
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
            Assert.assertEquals(null, b1025.getAnr());
            Assert.assertEquals("B-1025", b1025.getBnr());
            Assert.assertEquals(OffsetDateTime.parse("2018-07-19T07:21:03Z"), b1025.getCreationDate());
            Assert.assertEquals("IRKS", b1025.getCreator());
            Assert.assertEquals(1, b1025.getUsage().size());
            Assert.assertEquals(Integer.valueOf(0), b1025.getUsage().iterator().next().getUsage());
            Assert.assertEquals(OffsetDateTime.parse("2018-07-19T07:23:27Z"), b1025.getUsage().iterator().next().getRegistrationFrom());

            ResponseEntity<String> aadarujuupAqqusernaResponse = this.uuidSearch("3250B104-5F67-43A5-B6A8-1BEC88476C26", "locality");
            Assert.assertEquals(200, aadarujuupAqqusernaResponse.getStatusCode().value());

            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            importMetadata.setTransactionInProgress(false);
            session.close();
        }
    }

}