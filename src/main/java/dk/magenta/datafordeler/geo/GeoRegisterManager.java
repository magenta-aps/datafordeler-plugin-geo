package dk.magenta.datafordeler.geo;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.magenta.datafordeler.core.database.SessionManager;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.DataStreamException;
import dk.magenta.datafordeler.core.io.ImportInputStream;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.core.io.PluginSourceData;
import dk.magenta.datafordeler.core.plugin.Communicator;
import dk.magenta.datafordeler.core.plugin.EntityManager;
import dk.magenta.datafordeler.core.plugin.Plugin;
import dk.magenta.datafordeler.core.plugin.RegisterManager;
import dk.magenta.datafordeler.core.util.ItemInputStream;
import dk.magenta.datafordeler.geo.configuration.GeoConfigurationManager;
import dk.magenta.datafordeler.geo.data.accessaddress.AccessAddressEntity;
import dk.magenta.datafordeler.geo.data.building.BuildingEntity;
import dk.magenta.datafordeler.geo.data.locality.LocalityEntity;
import dk.magenta.datafordeler.geo.data.municipality.MunicipalityEntity;
import dk.magenta.datafordeler.geo.data.postcode.PostcodeEntity;
import dk.magenta.datafordeler.geo.data.road.RoadEntity;
import dk.magenta.datafordeler.geo.data.unitaddress.UnitAddressEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.OffsetDateTime;

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
        // Hardcoded loading from fixtures
        switch (entityManager.getSchema()) {

            case AccessAddressEntity.schema:
                return new File("fixtures/Adgangsadresse.json").toURI();
            case BuildingEntity.schema:
                return new File("fixtures/Bygning.json").toURI();
            case LocalityEntity.schema:
                return new File("fixtures/Lokalitet.json").toURI();
            case MunicipalityEntity.schema:
                return new File("fixtures/Kommune.json").toURI();
            case PostcodeEntity.schema:
                return new File("fixtures/Postnummer.json").toURI();
            case RoadEntity.schema:
                return new File("fixtures/Vejmidte.json").toURI();
            case UnitAddressEntity.schema:
                return new File("fixtures/Enhedsadresse.json").toURI();

        }
        return null;
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
        String scheme = eventInterface.getScheme();
        this.log.info("scheme: "+scheme);
        this.log.info("eventInterface: "+eventInterface);
        ImportInputStream responseBody = null;
        switch (scheme) {
            case "file":
                try {
                    File file = new File(eventInterface);
                    responseBody = new ImportInputStream(new FileInputStream(file));
                    responseBody.addCacheFile(file);
                } catch (FileNotFoundException e) {
                    this.log.error(e);
                    throw new DataStreamException(e);
                }
                break;
        }
        return responseBody;
    }

    @Override
    protected ItemInputStream<? extends PluginSourceData> parseEventResponse(InputStream rawData, EntityManager entityManager) throws DataFordelerException {
        return null;
    }

}
