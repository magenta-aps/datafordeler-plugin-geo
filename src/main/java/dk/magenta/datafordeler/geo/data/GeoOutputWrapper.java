package dk.magenta.datafordeler.geo.data;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.*;
import dk.magenta.datafordeler.core.database.Identification;
import dk.magenta.datafordeler.core.fapi.BaseQuery;
import dk.magenta.datafordeler.core.fapi.OutputWrapper;
import dk.magenta.datafordeler.core.util.Bitemporality;
import dk.magenta.datafordeler.core.util.BitemporalityComparator;
import dk.magenta.datafordeler.core.util.DoubleListHashMap;
import dk.magenta.datafordeler.core.util.ListHashMap;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;
import dk.magenta.datafordeler.geo.data.common.GeoNontemporalRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

@Component
public abstract class GeoOutputWrapper<E extends GeoEntity> extends OutputWrapper<E> {

    @Autowired
    private ObjectMapper objectMapper;

    public ObjectMapper getObjectMapper() {
        return this.objectMapper;
    }

    public static final String EFFECTS = "virkninger";
    public static final String EFFECT_FROM = "virkningFra";
    public static final String EFFECT_TO = "virkningTil";

    public static final String REGISTRATIONS = "registreringer";
    public static final String REGISTRATION_FROM = "registreringFra";
    public static final String REGISTRATION_TO = "registreringTil";

    @Override
    public Object wrapResult(E input, BaseQuery query, Mode mode) {
        ObjectNode root = this.objectMapper.createObjectNode();
        root.put(Identification.IO_FIELD_UUID, input.getUUID().toString());
        root.put(Identification.IO_FIELD_DOMAIN, input.identification.getDomain());
        //root.put(PersonEntity.IO_FIELD_CPR_NUMBER, input.getPersonnummer());
        Bitemporality overlap = new Bitemporality(query.getRegistrationFrom(), query.getRegistrationTo(), query.getEffectFrom(), query.getEffectTo());
        OutputContainer outputContainer = new OutputContainer();
        this.fillMetadataContainer(outputContainer, input);
        root.setAll(outputContainer.getBase());
        switch (mode) {
            case DRV:
                root.setAll(outputContainer.getDataNodes(overlap));
                break;
            case RVD:
            case LEGACY:
                ArrayNode registrations = outputContainer.getRegistrations(overlap);
                if (registrations.size() > 0) {
                    root.set(REGISTRATIONS, registrations);
                }
                break;
            case RDV:
                break;
        }
        return root;
    }

    protected abstract void fillMetadataContainer(OutputContainer container, E item);

    public Set<String> getRemoveFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add(GeoMonotemporalRecord.IO_FIELD_EDITOR);
        return fields;
    }



    // Move this to core
    protected class OutputContainer {

        private DoubleListHashMap<Bitemporality, String, JsonNode> bitemporalData = new DoubleListHashMap<>();

        private ListHashMap<String, JsonNode> nontemporalData = new ListHashMap<>();

        private HashSet<String> forcedArrayKeys = new HashSet<>();

        public boolean isArrayForced(String key) {
            return this.forcedArrayKeys.contains(key);
        }

        public <T extends GeoMonotemporalRecord> void addMonotemporal(String key, Set<T> records) {
            this.addMonotemporal(key, records, null, false, false);
        }

        public <T extends GeoMonotemporalRecord> void addMonotemporal(String key, Set<T> records, boolean unwrapSingle) {
            this.addMonotemporal(key, records, null, unwrapSingle, false);
        }

        public <T extends GeoMonotemporalRecord> void addMonotemporal(String key, Set<T> records, Function<T, JsonNode> converter) {
            this.addMonotemporal(key, records, converter, false, false);
        }

        public <T extends GeoMonotemporalRecord> void addMonotemporal(String key, Set<T> records, Function<T, JsonNode> converter, boolean unwrapSingle, boolean forceArray) {
            this.addTemporal(key, records, converter, unwrapSingle, forceArray, t -> t.getMonotemporality().asBitemporality());
        }
        /*
        public <T extends GeoMonotemporalRecord> void addBitemporal(String key, Set<T> records) {
            this.addBitemporal(key, records, null, false, false);
        }

        public <T extends GeoMonotemporalRecord> void addBitemporal(String key, Set<T> records, boolean unwrapSingle) {
            this.addBitemporal(key, records, null, unwrapSingle, false);
        }

        public <T extends GeoMonotemporalRecord> void addBitemporal(String key, Set<T> records, Function<T, JsonNode> converter) {
            this.addBitemporal(key, records, converter, false, false);
        }

        public <T extends GeoMonotemporalRecord> void addBitemporal(String key, Set<T> records, Function<T, JsonNode> converter, boolean unwrapSingle, boolean forceArray) {
            this.addTemporal(key, records, converter, unwrapSingle, forceArray, t -> t.getMonotemporality().asBitemporality());
        }
        */

        private <T extends GeoMonotemporalRecord> void addTemporal(String key, Set<T> records, Function<T, JsonNode> converter, boolean unwrapSingle, boolean forceArray, Function<T, Bitemporality> bitemporalityExtractor) {
            ObjectMapper objectMapper = GeoOutputWrapper.this.getObjectMapper();
            for (T record : records) {
                if (record != null) {
                    JsonNode value = (converter != null) ? converter.apply(record) : objectMapper.valueToTree(record);
                    Bitemporality bitemporality = bitemporalityExtractor.apply(record);
                    if (value instanceof ObjectNode) {
                        ObjectNode oValue = (ObjectNode) value;
                        Set<String> removeFieldNames = GeoOutputWrapper.this.getRemoveFieldNames();
                        if (removeFieldNames != null) {
                            oValue.remove(removeFieldNames);
                        }
                        if (unwrapSingle && value.size() == 1) {
                            this.bitemporalData.add(bitemporality, key, oValue.get(oValue.fieldNames().next()));
                            continue;
                        }
                    }
                    this.bitemporalData.add(bitemporality, key, value);
                }
            }
            if (forceArray) {
                this.forcedArrayKeys.add(key);
            }
        }

        public <T extends GeoNontemporalRecord> void addNontemporal(String key, T record) {
            this.addNontemporal(key, Collections.singleton(record), null, false, false);
        }

        public <T extends GeoNontemporalRecord> void addNontemporal(String key, Function<T, JsonNode> converter, T record) {
            this.addNontemporal(key, Collections.singleton(record), converter, false, false);
        }

        public <T extends GeoNontemporalRecord> void addNontemporal(String key, Set<T> records) {
            this.addNontemporal(key, records, null, false, false);
        }

        public <T extends GeoNontemporalRecord> void addNontemporal(String key, Set<T> records, Function<T, JsonNode> converter, boolean unwrapSingle, boolean forceArray) {
            ObjectMapper objectMapper = GeoOutputWrapper.this.getObjectMapper();
            for (T record : records) {
                JsonNode value = (converter != null) ? converter.apply(record) : objectMapper.valueToTree(record);
                if (value instanceof ObjectNode) {
                    ObjectNode oValue = (ObjectNode) value;
                    if (unwrapSingle && value.size() == 1) {
                        this.nontemporalData.add(key, oValue.get(oValue.fieldNames().next()));
                        continue;
                    }
                }
                this.nontemporalData.add(key, value);
            }
            if (forceArray) {
                this.forcedArrayKeys.add(key);
            }
        }

        public void addNontemporal(String key, Boolean data) {
            this.nontemporalData.add(key, data != null ? (data ? BooleanNode.getTrue() : BooleanNode.getFalse()) : null);
        }

        public void addNontemporal(String key, Integer data) {
            this.nontemporalData.add(key, data != null ? new IntNode(data) : null);
        }

        public void addNontemporal(String key, Long data) {
            this.nontemporalData.add(key, data != null ? new LongNode(data) : null);
        }

        public void addNontemporal(String key, String data) {
            this.nontemporalData.add(key, data != null ? new TextNode(data) : null);
        }

        public void addNontemporal(String key, LocalDate data) {
            this.nontemporalData.add(key, data != null ? new TextNode(data.format(DateTimeFormatter.ISO_LOCAL_DATE)) : null);
        }

        public void addNontemporal(String key, OffsetDateTime data) {
            this.nontemporalData.add(key, data != null ? new TextNode(data.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)) : null);
        }

        // RVD
        private final Set<String> rvdNodeRemoveFields = new HashSet<>(Arrays.asList(new String[]{
                "registreringFra",
                "registreringTil",
                "registrationFrom",
                "registrationTo",
                "sidstOpdateret",
                "lastUpdated"
        }));

        public ArrayNode getRegistrations(Bitemporality mustOverlap) {

            ObjectMapper objectMapper = GeoOutputWrapper.this.getObjectMapper();
            ArrayNode registrationsNode = objectMapper.createArrayNode();
            ArrayList<Bitemporality> bitemporalities = new ArrayList<>(this.bitemporalData.keySet());

            ListHashMap<OffsetDateTime, Bitemporality> startTerminators = new ListHashMap<>();
            ListHashMap<OffsetDateTime, Bitemporality> endTerminators = new ListHashMap<>();

            for (Bitemporality bitemporality : bitemporalities) {
                startTerminators.add(bitemporality.registrationFrom, bitemporality);
                endTerminators.add(bitemporality.registrationTo, bitemporality);
            }

            HashSet<OffsetDateTime> allTerminators = new HashSet<>();
            allTerminators.addAll(startTerminators.keySet());
            allTerminators.addAll(endTerminators.keySet());
            // Create a sorted list of all timestamps where Bitemporalities either begin or end
            ArrayList<OffsetDateTime> terminators = new ArrayList<>(allTerminators);
            terminators.sort(Comparator.nullsFirst(OffsetDateTime::compareTo));
            terminators.add(null);

            HashSet<Bitemporality> presentBitemporalities = new HashSet<>();

            for (int i=0; i<terminators.size(); i++) {
                OffsetDateTime t = terminators.get(i);
                List<Bitemporality> startingHere = startTerminators.get(t);
                List<Bitemporality> endingHere = endTerminators.get(t);
                if (startingHere != null) {
                    presentBitemporalities.addAll(startingHere);
                }
                if (endingHere != null) {
                    presentBitemporalities.removeAll(endingHere);
                }
                if (i < terminators.size() - 1) {
                    OffsetDateTime next = terminators.get(i + 1);
                    if (!presentBitemporalities.isEmpty()) {

                        if (mustOverlap == null || mustOverlap.overlapsRegistration(t, next)) {
                            ObjectNode registrationNode = objectMapper.createObjectNode();
                            registrationsNode.add(registrationNode);
                            registrationNode.put(REGISTRATION_FROM, formatTime(t));
                            registrationNode.put(REGISTRATION_TO, formatTime(next));
                            ArrayNode effectsNode = objectMapper.createArrayNode();
                            registrationNode.set(EFFECTS, effectsNode);
                            ArrayList<Bitemporality> sortedEffects = new ArrayList<>(presentBitemporalities);
                            sortedEffects.sort(BitemporalityComparator.EFFECT);
                            Bitemporality lastEffect = null;
                            ObjectNode effectNode = null;
                            for (Bitemporality bitemporality : sortedEffects) {
                                // Implemented in Hibernate filters instead. Each stored effect can be tested against the query filter
                                // on the database level, but registrations are split here and thus cannot be tested in the database
                                // Also, they lack the range end due to the way the incoming data is formatted
                                //if (mustOverlap == null || mustOverlap.overlapsEffect(bitemporality.effectFrom, bitemporality.effectTo)) {
                                if (lastEffect == null || effectNode == null || !lastEffect.equalEffect(bitemporality)) {
                                    effectNode = objectMapper.createObjectNode();
                                    effectsNode.add(effectNode);
                                }
                                effectNode.put(EFFECT_FROM, formatTime(bitemporality.effectFrom, true));
                                effectNode.put(EFFECT_TO, formatTime(bitemporality.effectTo, true));
                                HashMap<String, ArrayList<JsonNode>> records = this.bitemporalData.get(bitemporality);
                                for (String key : records.keySet()) {
                                    List<JsonNode> nodes = records.get(key);
                                    nodes = this.filterNodes(nodes, node -> {
                                        if (node instanceof ObjectNode) {
                                            ObjectNode objectNode = (ObjectNode) node;
                                            objectNode.remove(rvdNodeRemoveFields);
                                            if (objectNode.size() == 1) {
                                                node = objectNode.get(objectNode.fieldNames().next());
                                            }
                                        }
                                        return node;
                                    });
                                    this.setValue(objectMapper, effectNode, key, nodes);
                                }
                                lastEffect = bitemporality;
                                //}
                            }
                        }
                    }
                }
            }
            return registrationsNode;
        }


        // DRV
        public ObjectNode getDataNodes(Bitemporality mustOverlap) {
            ObjectMapper objectMapper = GeoOutputWrapper.this.getObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();
            for (Bitemporality bitemporality : this.bitemporalData.keySet()) {
                if (bitemporality.overlaps(mustOverlap)) {
                    HashMap<String, ArrayList<JsonNode>> data = this.bitemporalData.get(bitemporality);
                    for (String key : data.keySet()) {
                        ArrayNode arrayNode = objectMapper.createArrayNode();
                        objectNode.set(key, arrayNode);
                        arrayNode.addAll(data.get(key));
                    }
                }
            }
            return objectNode;
        }

        public ObjectNode getBase() {
            ObjectMapper objectMapper = GeoOutputWrapper.this.getObjectMapper();
            ObjectNode objectNode = objectMapper.createObjectNode();
            for (String key : this.nontemporalData.keySet()) {
                this.setValue(objectMapper, objectNode, key, this.nontemporalData.get(key));
            }
            return objectNode;
        }

        private void setValue(ObjectMapper objectMapper, ObjectNode objectNode, String key, List<JsonNode> values) {
            if (values.size() == 1 && !this.isArrayForced(key)) {
                objectNode.set(key, values.get(0));
            } else {
                ArrayNode array = objectMapper.createArrayNode();
                objectNode.set(key, array);
                for (JsonNode value : values) {
                    array.add(value);
                }
            }
        }

        private List<JsonNode> filterNodes(List<JsonNode> nodes, Function<JsonNode, JsonNode> filterMethod) {
            List<JsonNode> outNodes = new ArrayList<>();
            for (JsonNode node : nodes) {
                outNodes.add(filterMethod.apply(node));
            }
            return outNodes;
        }
    }

    //--------------------------------------------------------------------------
    // Utility

    protected static String formatTime(OffsetDateTime time) {
        return formatTime(time, false);
    }

    protected static String formatTime(OffsetDateTime time, boolean asDateOnly) {
        if (time == null) return null;
        return time.format(asDateOnly ? DateTimeFormatter.ISO_LOCAL_DATE : DateTimeFormatter.ISO_OFFSET_DATE_TIME);
    }

    protected static String formatTime(LocalDate time) {
        if (time == null) return null;
        return time.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
}
