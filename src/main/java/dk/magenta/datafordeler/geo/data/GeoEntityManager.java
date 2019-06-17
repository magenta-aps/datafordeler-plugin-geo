package dk.magenta.datafordeler.geo.data;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dk.magenta.datafordeler.core.database.*;
import dk.magenta.datafordeler.core.exception.DataFordelerException;
import dk.magenta.datafordeler.core.exception.DataStreamException;
import dk.magenta.datafordeler.core.exception.ImportInterruptedException;
import dk.magenta.datafordeler.core.exception.WrongSubclassException;
import dk.magenta.datafordeler.core.io.ImportMetadata;
import dk.magenta.datafordeler.core.io.Receipt;
import dk.magenta.datafordeler.core.plugin.Communicator;
import dk.magenta.datafordeler.core.plugin.EntityManager;
import dk.magenta.datafordeler.core.plugin.HttpCommunicator;
import dk.magenta.datafordeler.core.plugin.RegisterManager;
import dk.magenta.datafordeler.core.util.ItemInputStream;
import dk.magenta.datafordeler.core.util.Stopwatch;
import dk.magenta.datafordeler.geo.GeoRegisterManager;
import dk.magenta.datafordeler.geo.configuration.GeoConfiguration;
import dk.magenta.datafordeler.geo.configuration.GeoConfigurationManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.function.Consumer;

@Component
public abstract class GeoEntityManager<E extends GeoEntity, T extends RawData> extends EntityManager {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    GeoConfigurationManager geoConfigurationManager;

    @Autowired
    Stopwatch timer;

    @Autowired
    private ConfigurationSessionManager configurationSessionManager;

    @Autowired
    private SessionManager sessionManager;

    private HttpCommunicator commonFetcher;

    protected Logger log = LogManager.getLogger(this.getClass().getSimpleName());

    private Collection<String> handledURISubstrings;

    protected abstract String getBaseName();

    public GeoEntityManager() {
        this.commonFetcher = new HttpCommunicator();
        this.handledURISubstrings = new ArrayList<>();
    }

    @Override
    public void setRegisterManager(RegisterManager registerManager) {
        super.setRegisterManager(registerManager);
        //this.handledURISubstrings.add(expandBaseURI(this.getBaseEndpoint(), "/" + this.getBaseName(), null, null).toString());
        //this.handledURISubstrings.add(expandBaseURI(this.getBaseEndpoint(), "/get/" + this.getBaseName(), null, null).toString());
    }

    public abstract String getDomain();

    @Override
    public Collection<String> getHandledURISubstrings() {
        return this.handledURISubstrings;
    }

    @Override
    protected ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    @Override
    protected Communicator getRegistrationFetcher() {
        return this.commonFetcher;
    }

    @Override
    protected Communicator getReceiptSender() {
        return this.commonFetcher;
    }

    @Override
    public URI getBaseEndpoint() {
        return this.getRegisterManager().getBaseEndpoint();
    }

    @Override
    protected URI getReceiptEndpoint(Receipt receipt) {
        return null;
    }

    @Override
    public RegistrationReference parseReference(InputStream referenceData) throws IOException {
        return this.getObjectMapper().readValue(referenceData, this.managedRegistrationReferenceClass);
    }

    @Override
    public RegistrationReference parseReference(String referenceData, String charsetName) throws IOException {
        return this.getObjectMapper().readValue(referenceData.getBytes(charsetName), this.managedRegistrationReferenceClass);
    }

    @Override
    public RegistrationReference parseReference(URI uri) {
        try {
            return this.parseReference(uri.toString(),"utf-8");
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    protected URI getListChecksumInterface(OffsetDateTime offsetDateTime) {
        return null;
    }

    @Override
    public URI getRegistrationInterface(RegistrationReference reference) throws WrongSubclassException {
        if (!this.managedRegistrationReferenceClass.isInstance(reference)) {
            throw new WrongSubclassException(this.managedRegistrationReferenceClass, reference);
        }
        if (reference.getURI() != null) {
            return reference.getURI();
        }
        return EntityManager.expandBaseURI(this.getBaseEndpoint(), "/get/"+this.getBaseName()+"/"+reference.getChecksum());
    }

    @Override
    protected ItemInputStream<? extends EntityReference> parseChecksumResponse(InputStream responseContent) {
        return ItemInputStream.parseJsonStream(responseContent, this.managedEntityReferenceClass, "items", this.getObjectMapper());
    }

    @Override
    protected Logger getLog() {
        return this.log;
    }

    protected GeoConfiguration getConfiguration() {
        return this.geoConfigurationManager.getConfiguration();
    }

    public GeoRegisterManager getRegisterManager() {
        return (GeoRegisterManager) super.getRegisterManager();
    }

    private static final String TASK_PARSE = "GeoParse";
    private static final String TASK_FIND_ENTITY = "GeoFindEntity";
    private static final String TASK_POPULATE_DATA = "GeoPopulateData";
    private static final String TASK_SAVE = "GeoSave";
    private static final String TASK_COMMIT = "GeoCommit";
    private static final String TASK_CHUNK_HANDLE = "GeoChunk";


    protected abstract Class<E> getEntityClass();
    protected abstract Class<T> getRawClass();
    protected abstract UUID generateUUID(T rawData);
    protected abstract E createBasicEntity(T record, Session session);

    @Override
    public List<? extends Registration> parseData(InputStream jsonData, ImportMetadata importMetadata) throws DataFordelerException {
        HashMap<UUID, E> entityCache = new HashMap<>();
        Session session = importMetadata.getSession();
        boolean wrappedInTransaction = importMetadata.isTransactionInProgress();
        if (!wrappedInTransaction) {
            session.beginTransaction();
            importMetadata.setTransactionInProgress(true);
        }
        timer.clear();
        final WireCache wireCache = new WireCache();
        Charset charset = this.geoConfigurationManager.getConfiguration().getCharset();

        GeoEntityManager.parseJsonStream(jsonData, charset, "features", this.objectMapper, jsonNode -> {
            try {
                timer.start(TASK_PARSE);
                T rawData = objectMapper.readerFor(this.getRawClass()).readValue(jsonNode);
                timer.measure(TASK_PARSE);

                timer.start(TASK_FIND_ENTITY);
                UUID uuid = this.generateUUID(rawData);
                E entity = entityCache.get(uuid);
                if (entity == null) {
                    Identification identification = QueryManager.getOrCreateIdentification(session, uuid, this.getDomain());
                    entity = QueryManager.getEntity(session, identification, this.getEntityClass());
                    if (entity == null) {
                        entity = this.createBasicEntity(rawData, session);
                        entity.setIdentification(identification);
                    }
                    entityCache.put(uuid, entity);
                }
                timer.measure(TASK_FIND_ENTITY);

                timer.start(TASK_POPULATE_DATA);
                this.updateEntity(entity, rawData, importMetadata);
                entity.wire(session, wireCache);
                timer.measure(TASK_POPULATE_DATA);

                timer.start(TASK_SAVE);
                session.save(entity);
                timer.measure(TASK_SAVE);

            } catch (IOException e) {
                log.error("Error importing "+this.getEntityClass().getSimpleName()+": "+jsonNode.toString(), e);
            }
        });

        if (!wrappedInTransaction) {
            session.getTransaction().commit();
            importMetadata.setTransactionInProgress(false);
        }
        log.info(timer.formatAllTotal());
        return null;
    }

    public static long parseJsonStream(String jsonData, String searchKey, ObjectMapper objectMapper, Consumer<JsonNode> callback) throws DataStreamException {
        Charset charset = Charset.forName("utf-8");
        return parseJsonStream(new ByteArrayInputStream(jsonData.getBytes(charset)), charset, searchKey, objectMapper, callback);
    }

    public static long parseJsonStream(InputStream jsonData, Charset charset, String searchKey, ObjectMapper objectMapper, Consumer<JsonNode> callback) throws DataStreamException {
        JsonFactory factory = objectMapper.getFactory();
        long count = 0;

        InputStreamReader reader = new InputStreamReader(jsonData, charset);
        try {
            JsonParser parser = factory.createParser(reader);
            JsonToken token;
            boolean found;
            while ((token = parser.nextToken()) != null) {
                if (searchKey == null) {
                    found = true;
                } else if (token == JsonToken.FIELD_NAME && searchKey.equals(parser.getCurrentName())) {
                    token = parser.nextToken();
                    found = true;
                } else {
                    found = false;
                }

                if (found) {
                    if (token == JsonToken.START_ARRAY) {
                        if (parser.nextToken() != JsonToken.END_ARRAY) {
                            Iterator<JsonNode> nodeIterator = parser.readValuesAs(JsonNode.class);
                            while (nodeIterator.hasNext()) {
                                JsonNode item = nodeIterator.next();
                                if (callback != null) {
                                    callback.accept(item);
                                }
                                count++;
                            }
                        }
                    } else {
                        JsonNode item = parser.readValueAs(JsonNode.class);
                        if (callback != null) {
                            callback.accept(item);
                        }
                        count++;
                    }
                }
            }

        } catch (IOException e) {
            throw new DataStreamException(e);
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                throw new DataStreamException(e);
            }
        }
        return count;
    }


    public void parseDeletionData(InputStream jsonData) throws DataStreamException {
        Charset charset = this.geoConfigurationManager.getConfiguration().getCharset();
        Session session = sessionManager.getSessionFactory().openSession();
        try {
            GeoEntityManager.parseJsonStream(jsonData, charset, "features", this.objectMapper, jsonNode -> {
                String globalId = jsonNode.get("GlobalID").asText();
                //long deletedDate = jsonNode.get("DeletedDate").asLong();
                //Instant deletionTime = Instant.ofEpochMilli(deletedDate);
                UUID uuid = SumiffiikRawData.getSumiffiikAsUUID(globalId);
                E entity = QueryManager.getEntity(session, uuid, this.getEntityClass());
                if (entity != null) {
                    session.delete(entity);
                }
            });
        } finally {
            session.close();
        }
    }


    protected void updateEntity(E entity, T rawData, ImportMetadata importMetadata) {
        entity.update(rawData, importMetadata.getImportTime());
    }

    protected boolean filter(JsonNode record, ObjectNode importConfiguration) {
        return true;
    }

    @Override
    public boolean handlesOwnSaves() {
        return true;
    }

    private void checkInterrupt(ImportMetadata importMetadata) throws ImportInterruptedException {
        if (importMetadata.getStop()) {
            throw new ImportInterruptedException(new InterruptedException());
        }
    }

    public int getJobId() {
        return 0;
    }

    public int getCustomerId() {
        return 0;
    }

    public String getLocalSubscriptionFolder() {
        return null;
    }

    public boolean isSetupSubscriptionEnabled() {
        return false;
    }

    /**
     * Should return whether the configuration is set so that pulls are enabled for this entitymanager
     */
    @Override
    public boolean pullEnabled() {
        GeoConfiguration configuration = this.getRegisterManager().getConfigurationManager().getConfiguration();
        GeoConfiguration.RegisterType registerType = configuration.getRegisterType(this.getSchema());
        return (registerType != null && registerType != GeoConfiguration.RegisterType.DISABLED);
    }
}
