import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.database.QueryManager;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntity;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntityManager;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityNameRecord;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityService;
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
    private MunicipalityEntityManager entityManager;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private MunicipalityService municipalityService;

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
            entityManager.parseData(data, importMetadata);

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

}