import dk.magenta.datafordeler.core.Application;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntityManager;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
public class TestParse {

    @Autowired
    private MunicipalityEntityManager entityManager;

    @Autowired
    private SessionManager sessionManager;

    @Test
    public void testParse() throws DataFordelerException, IOException {
        /*String json = "{\"features\": [{\n" +
                "\t\t\t\"attributes\": {\n" +
                "\t\t\t\t\"OBJECTID\": 1,\n" +
                "\t\t\t\t\"Postdistrikt\": \"Santa Claus/Julemanden\",\n" +
                "\t\t\t\t\"Postnummer\": 2412\n" +
                "\t\t\t}\n" +
                "\t\t}, {\n" +
                "\t\t\t\"attributes\": {\n" +
                "\t\t\t\t\"OBJECTID\": 2,\n" +
                "\t\t\t\t\"Postdistrikt\": \"Nuuk\",\n" +
                "\t\t\t\t\"Postnummer\": 3900\n" +
                "\t\t\t}\n" +
                "\t\t}],\"data\":{\"features\":1}}";
        ByteArrayInputStream data = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        */
        FileInputStream data = new FileInputStream(new File("/home/lars/Projekt/datafordeler/plugin/adresse/fixtures/Kommune.json"));
        ImportMetadata importMetadata = new ImportMetadata();
        Session session = sessionManager.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        importMetadata.setSession(session);
        try {
            entityManager.parseData(data, importMetadata);
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

}