package dk.magenta.datafordeler.geo;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.ConfigurationException;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.DataStreamException;
import dk.magenta.datafordeler.core.exception.ParseException;
import dk.magenta.datafordeler.core.io.ImportInputStream;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.core.io.PluginSourceData;
import dk.magenta.datafordeler.core.plugin.*;
import dk.magenta.datafordeler.core.util.InputStreamReader;
import dk.magenta.datafordeler.core.util.ItemInputStream;
import dk.magenta.datafordeler.geo.configuration.GeoConfiguration;
import dk.magenta.datafordeler.geo.configuration.GeoConfigurationManager;
import dk.magenta.datafordeler.geo.data.GeoEntityManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Component
public class GeoRegisterManager extends RegisterManager {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GeoConfigurationManager configurationManager;

    @Autowired
    private GeoPlugin plugin;

    @Autowired
    private SessionManager sessionManager;

    private Logger log = LogManager.getLogger("dk.magenta.datafordeler.geo.GeoRegisterManager");

    @Value("${dafo.geo.proxy-url:}")
    private String proxyString;

    @Value("${dafo.geo.local-copy-folder:}")
    private String localCopyFolder;

    public GeoRegisterManager() {

    }

    /**
    * RegisterManager initialization; set up dk.magenta.datafordeler.geo.configuration and source fetcher.
    * We store fetched data in a local cache, so create a random folder for that.
    */
    @PostConstruct
    public void init() throws IOException {
        if (this.localCopyFolder == null || this.localCopyFolder.isEmpty()) {
            File temp = File.createTempFile("datafordeler-cache","");
            temp.delete();
            temp.mkdir();
            this.localCopyFolder = temp.getAbsolutePath();
        }
    }

    public GeoConfigurationManager getConfigurationManager() {
        return this.configurationManager;
    }

    @Override
    protected Logger getLog() {
        return this.log;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public SessionManager getSessionManager() {
        return this.sessionManager;
    }

    @Override
    public URI getBaseEndpoint() {
        return null;
    }

    @Override
    protected Communicator getEventFetcher() {
        return null;
    }

    @Override
    protected ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Override
    public URI getEventInterface(EntityManager entityManager) throws DataFordelerException {
        Session session = this.sessionManager.getSessionFactory().openSession();
        OffsetDateTime lastUpdateTime = entityManager.getLastUpdated(session);
        session.close();

        if (lastUpdateTime == null) {
            lastUpdateTime = OffsetDateTime.parse("1900-01-01T00:00:00Z");
            log.info("Last update time not found");
        } else {
            log.info("Last update time: "+lastUpdateTime.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        String address = this.configurationManager.getConfiguration().getURL(entityManager.getSchema());
        if (address == null || address.isEmpty()) {
            throw new ConfigurationException("Invalid URL for schema "+entityManager.getSchema()+": "+address);
        }
        address = address.replace("%{editDate}", lastUpdateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        try {
            URL url = new URL(address);
            return new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
        } catch (URISyntaxException | MalformedURLException e) {
            throw new ConfigurationException("Invalid URL for schema "+entityManager.getSchema()+": "+address, e);
        }
    }

    @Override
    protected Communicator getChecksumFetcher() {
        return null;
    }

    @Override
    public URI getListChecksumInterface(String schema, OffsetDateTime from) {
        return null;
    }

    @Override
    public boolean pullsEventsCommonly() {
        return false;
    }

    @Override
    public ImportInputStream pullRawData(URI eventInterface, EntityManager entityManager, ImportMetadata importMetadata) throws DataFordelerException {
        this.log.info("eventInterface: "+eventInterface);
        GeoConfiguration configuration = this.configurationManager.getConfiguration();
        GeoConfiguration.RegisterType registerType = configuration.getRegisterType(entityManager.getSchema());
        if (registerType == null) {
            registerType = GeoConfiguration.RegisterType.DISABLED;
        }
        switch (registerType) {
            case LOCAL_FILE:
                try {
                    File file = new File(eventInterface);
                    ImportInputStream response = new ImportInputStream(new FileInputStream(file));
                    response.addCacheFile(file);
                    return response;
                } catch (FileNotFoundException e) {
                    this.log.error(e);
                    throw new DataStreamException(e);
                }
            case REMOTE_HTTP:
                final ArrayList<Throwable> errors = new ArrayList<>();
                InputStream responseBody;
                HttpCommunicator communicator = new HttpCommunicator();

                Session session = this.sessionManager.getSessionFactory().openSession();
                session.close();
                File cacheFile = new File("local/geo/" + entityManager.getSchema() + "_" + LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE));
                try {
                    if (!cacheFile.exists()) {
                        log.info("Cache file "+cacheFile.getAbsolutePath()+" doesn't exist. Creating new and filling from source");

                        cacheFile.createNewFile();
                        FileWriter fileWriter = new FileWriter(cacheFile);
                        int count = 1000;

                        String query = eventInterface.getQuery();
                        query = query.replace("%{count}", Integer.toString(count));
                        fileWriter.append("[\n");
                        for (int offset = 0; offset < 1000000; offset += count) {
                            String offsetQuery = query.replace("%{offset}", Integer.toString(offset));
                            eventInterface = new URI(eventInterface.getScheme(), eventInterface.getUserInfo(), eventInterface.getHost(), eventInterface.getPort(), eventInterface.getPath(), offsetQuery, eventInterface.getFragment());
                            responseBody = communicator.fetch(eventInterface);
                            try {
                                String data = InputStreamReader.readInputStream(responseBody);
                                long responseCount = GeoEntityManager.parseJsonStream(data, "features", this.objectMapper, null);
                                if (responseCount == 0) {
                                    break;
                                }
                                if (offset > 0) {
                                    fileWriter.append(",\n");
                                }
                                fileWriter.append(data);
                                if (responseCount < count) {
                                    break;
                                }
                            } finally {
                                responseBody.close();
                            }
                        }
                        fileWriter.append("\n]");
                        fileWriter.close();
                        log.info("Loaded into cache file");
                    }
                } catch (URISyntaxException | IOException e) {
                    throw new DataStreamException(e);
                }

                if (!errors.isEmpty()) {
                    throw new ParseException("Error while loading data for "+entityManager.getSchema(), errors.get(0));
                }

                try {
                    return new ImportInputStream(new FileInputStream(cacheFile), cacheFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }
        return null;
    }

    @Override
    protected ItemInputStream<? extends PluginSourceData> parseEventResponse(InputStream rawData, EntityManager entityManager) throws DataFordelerException {
        return null;
    }

    @Override
    public String getPullCronSchedule() {
        return this.configurationManager.getConfiguration().getPullCronSchedule();
    }

}
