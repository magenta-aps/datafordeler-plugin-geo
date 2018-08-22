import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.Engine;
import dk.magenta.datafordeler.core.Pull;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressEntity;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressEntityManager;
import dk.magenta.datafordeler.geo.data.building.BuildingEntity;
import dk.magenta.datafordeler.geo.data.building.BuildingEntityManager;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntityManager;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntity;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntityManager;
import dk.magenta.datafordeler.geo.data.postcode.PostcodeEntity;
import dk.magenta.datafordeler.geo.data.postcode.PostcodeEntityManager;
import dk.magenta.datafordeler.geo.data.road.RoadEntity;
import dk.magenta.datafordeler.geo.data.road.RoadEntityManager;
import dk.magenta.datafordeler.geo.data.unitaddress.UnitAddressEntity;
import dk.magenta.datafordeler.geo.data.unitaddress.UnitAddressEntityManager;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.UUID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TestParse extends GeoTest {

    @Autowired
    private Engine engine;

    @Autowired
    private GeoPlugin plugin;

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

    private ResponseEntity<String> restSearch(ParameterMap parameters, String type) {
        return this.lookup("/geo/"+type+"/1/rest/search?" + parameters.asUrlParams());
    }

    private ResponseEntity<String> uuidSearch(String id, String type) {
        return this.lookup("/geo/"+type+"/1/rest/" + id);
    }

    @Test
    public void testMunicipality() throws DataFordelerException, IOException {
        this.load(municipalityEntityManager, "fixtures/Kommune.json");
        this.load(municipalityEntityManager, "fixtures/Kommune.json");

        Session session = sessionManager.getSessionFactory().openSession();
        try {
            MunicipalityEntity entity = QueryManager.getEntity(session, UUID.fromString("96C57A43-5761-45E6-83D0-F329A10B0AEC"), MunicipalityEntity.class);
            Assert.assertNotNull(entity);
            Assert.assertEquals(955, entity.getCode());
            Assert.assertTrue(OffsetDateTime.parse("2018-07-19T11:11:05Z").isEqual(entity.getCreationDate()));
            Assert.assertEquals("GREENADMIN", entity.getCreator());
            Assert.assertEquals(1, entity.getName().size());
            Assert.assertEquals(1, entity.getShape().size());
            Assert.assertEquals("Kommune Kujalleq", entity.getName().iterator().next().getName());
            Assert.assertTrue(OffsetDateTime.parse("2018-07-19T11:11:05Z").isEqual(entity.getName().iterator().next().getRegistrationFrom()));
        } finally {
            session.close();
        }

        ResponseEntity<String> response = this.uuidSearch("96C57A43-5761-45E6-83D0-F329A10B0AEC", "municipality");
        Assert.assertEquals(200, response.getStatusCode().value());
        System.out.println(response.getBody());

    }


    @Test
    public void testLocality() throws DataFordelerException, IOException {
        this.load(localityEntityManager, "fixtures/Lokalitet.json");
        this.load(localityEntityManager, "fixtures/Lokalitet.json");

        Session session = sessionManager.getSessionFactory().openSession();
        try {
            LocalityEntity entity = QueryManager.getEntity(session, UUID.fromString("32C1849A-6AB6-4358-B293-7D5EC69C3A19"), LocalityEntity.class);
            Assert.assertNotNull(entity);
            Assert.assertEquals(null, entity.getCode());
            Assert.assertTrue(OffsetDateTime.parse("2018-07-19T10:57:39Z").isEqual(entity.getCreationDate()));
            Assert.assertEquals("GREENADMIN", entity.getCreator());
            Assert.assertEquals(1, entity.getName().size());
            Assert.assertEquals(1, entity.getShape().size());
            Assert.assertEquals("Aadarujuup Aqquserna nord", entity.getName().iterator().next().getName());
            Assert.assertTrue(OffsetDateTime.parse("2018-07-19T10:57:39Z").isEqual(entity.getName().iterator().next().getRegistrationFrom()));
        } finally {
            session.close();
        }

        ResponseEntity<String> response = this.uuidSearch("32C1849A-6AB6-4358-B293-7D5EC69C3A19", "locality");
        Assert.assertEquals(200, response.getStatusCode().value());
    }


    @Test
    public void testRoad() throws DataFordelerException, IOException {
        this.load(roadEntityManager, "fixtures/Vejmidte.json");
        this.load(roadEntityManager, "fixtures/Vejmidte.json");

        Session session = sessionManager.getSessionFactory().openSession();
        try {
            RoadEntity entity = QueryManager.getEntity(session, UUID.fromString("DDF9075A-0B47-442B-BC0C-EFC296F67417"), RoadEntity.class);
            Assert.assertNotNull(entity);
            Assert.assertEquals(0, entity.getCode());
            Assert.assertTrue(OffsetDateTime.parse("2018-07-23T06:25:18Z").isEqual(entity.getCreationDate()));
            Assert.assertEquals("GREENADMIN", entity.getCreator());
            Assert.assertEquals(1, entity.getName().size());
            Assert.assertEquals(1, entity.getShape().size());
            Assert.assertEquals("Pujooriarfik", entity.getName().iterator().next().getName());
            Assert.assertTrue(OffsetDateTime.parse("2018-07-23T06:25:18Z").isEqual(entity.getName().iterator().next().getRegistrationFrom()));
        } finally {
            session.close();
        }

        ResponseEntity<String> response = this.uuidSearch("DDF9075A-0B47-442B-BC0C-EFC296F67417", "locality");
        Assert.assertEquals(200, response.getStatusCode().value());
    }


    @Test
    public void testBuilding() throws DataFordelerException, IOException {
        this.load(buildingEntityManager, "fixtures/Bygning.json");
        this.load(buildingEntityManager, "fixtures/Bygning.json");

        Session session = sessionManager.getSessionFactory().openSession();
        try {
            BuildingEntity entity = QueryManager.getEntity(session, UUID.fromString("3250B104-5F67-43A5-B6A8-1BEC88476C26"), BuildingEntity.class);
            Assert.assertNotNull(entity);
            System.out.println(entity.getAnr());
            Assert.assertEquals(null, entity.getAnr());
            Assert.assertEquals("B-1025", entity.getBnr());
            Assert.assertTrue(OffsetDateTime.parse("2018-07-19T07:21:03Z").isEqual(entity.getCreationDate()));
            Assert.assertEquals("IRKS", entity.getCreator());
            Assert.assertEquals(1, entity.getUsage().size());
            Assert.assertEquals(1, entity.getShape().size());
            Assert.assertEquals(Integer.valueOf(0), entity.getUsage().iterator().next().getUsage());
            Assert.assertTrue(OffsetDateTime.parse("2018-07-19T07:23:27Z"). isEqual(entity.getUsage().iterator().next().getRegistrationFrom()));
        } finally {
            session.close();
        }

        ResponseEntity<String> response = this.uuidSearch("3250B104-5F67-43A5-B6A8-1BEC88476C26", "building");
        Assert.assertEquals(200, response.getStatusCode().value());
    }


    @Test
    public void testAccessAddress() throws DataFordelerException, IOException {
        this.load(accessAddressEntityManager, "fixtures/Adgangsadresse.json");
        this.load(accessAddressEntityManager, "fixtures/Adgangsadresse.json");

        Session session = sessionManager.getSessionFactory().openSession();
        try {
            AccessAddressEntity entity = QueryManager.getEntity(session, UUID.fromString("FA17D08C-D51C-4CE5-8036-D24C06DAE5C6"), AccessAddressEntity.class);
            Assert.assertNotNull(entity);
            Assert.assertEquals("B-0000", entity.getBnr());
            Assert.assertTrue(OffsetDateTime.parse("2018-07-19T10:15:31Z").isEqual(entity.getCreationDate()));
            Assert.assertEquals("IRKS", entity.getCreator());
            Assert.assertEquals(1, entity.getShape().size());
            Assert.assertEquals(1, entity.getHouseNumber().size());
            Assert.assertEquals(1, entity.getBlockName().size());
            Assert.assertEquals(1, entity.getLocality().size());
            Assert.assertEquals(1, entity.getStatus().size());
            Assert.assertEquals(1, entity.getImportStatus().size());
            Assert.assertEquals(1, entity.getRoad().size());
        } finally {
            session.close();
        }

        ResponseEntity<String> response = this.uuidSearch("FA17D08C-D51C-4CE5-8036-D24C06DAE5C6", "accessaddress");
        Assert.assertEquals(200, response.getStatusCode().value());
    }


    @Test
    public void testUnitAddress() throws DataFordelerException, IOException {
        this.load(unitAddressEntityManager, "fixtures/Enhedsadresse.json");
        this.load(unitAddressEntityManager, "fixtures/Enhedsadresse.json");

        Session session = sessionManager.getSessionFactory().openSession();
        try {
            UnitAddressEntity entity = QueryManager.getEntity(session, UUID.fromString("A77B5AD0-D54F-46D6-8641-2BF47EA1C9D6"), UnitAddressEntity.class);
            Assert.assertNotNull(entity);
            Assert.assertTrue(OffsetDateTime.parse("2018-07-19T10:09:13Z").isEqual(entity.getCreationDate()));
            Assert.assertEquals("IRKS", entity.getCreator());
            Assert.assertEquals(1, entity.getUsage().size());
            Assert.assertEquals(1, entity.getFloor().size());
            Assert.assertEquals(1, entity.getDoor().size());
            Assert.assertEquals(1, entity.getImportStatus().size());
            Assert.assertEquals(1, entity.getNumber().size());
            Assert.assertEquals(1, entity.getSource().size());
            Assert.assertEquals(1, entity.getStatus().size());
        } finally {
            session.close();
        }

        ResponseEntity<String> response = this.uuidSearch("A77B5AD0-D54F-46D6-8641-2BF47EA1C9D6", "accessaddress");
        Assert.assertEquals(200, response.getStatusCode().value());
    }

    @Test
    public void testPostcode() throws DataFordelerException, IOException {
        this.load(postcodeEntityManager, "fixtures/Postnummer.json");
        this.load(postcodeEntityManager, "fixtures/Postnummer.json");

        Session session = sessionManager.getSessionFactory().openSession();
        try {
            PostcodeEntity entity = QueryManager.getEntity(session, PostcodeEntity.generateUUID(2412), PostcodeEntity.class);
            Assert.assertNotNull(entity);
            Assert.assertEquals(2412, entity.getCode());
            Assert.assertEquals("Santa Claus/Julemanden", entity.getName());
        } finally {
            session.close();
        }

        ResponseEntity<String> response = this.uuidSearch(PostcodeEntity.generateUUID(2412).toString(), "postcode");
        Assert.assertEquals(200, response.getStatusCode().value());
        System.out.println(response.getBody());
    }

    @Test
    public void pull() {
        Pull pull = new Pull(engine, plugin);
        pull.run();
    }

}