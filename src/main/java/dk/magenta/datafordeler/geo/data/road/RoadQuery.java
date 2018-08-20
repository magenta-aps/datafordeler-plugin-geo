package dk.magenta.datafordeler.geo.data.road;

import dk.magenta.datafordeler.core.database.BaseLookupDefinition;
import dk.magenta.datafordeler.core.fapi.ParameterMap;
import dk.magenta.datafordeler.core.fapi.QueryField;
import dk.magenta.datafordeler.geo.data.SumiffiikQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lars on 19-05-17.
 */
public class RoadQuery extends SumiffiikQuery<RoadEntity> {

    public static final String CODE = RoadEntity.IO_FIELD_CODE;
    public static final String NAME = RoadEntity.IO_FIELD_NAME;
    public static final String ADDRESSING_NAME = RoadNameRecord.IO_FIELD_ADDRESSING_NAME;

    @QueryField(type = QueryField.FieldType.INT, queryName = CODE)
    private List<String> code = new ArrayList<>();

    @QueryField(type = QueryField.FieldType.STRING, queryName = NAME)
    private List<String> name = new ArrayList<>();

    @QueryField(type = QueryField.FieldType.STRING, queryName = ADDRESSING_NAME)
    private List<String> addressingName = new ArrayList<>();

    public List<String> getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code.clear();
        this.addCode(code);
    }

    public void addCode(String code) {
        if (code != null) {
            this.code.add(code);
            this.increaseDataParamCount();
        }
    }

    public List<String> getName() {
        return name;
    }

    public void setName(String name) {
        this.name.clear();
        this.addName(name);
    }

    public void addName(String name) {
        if (name != null) {
            this.name.add(name);
            this.increaseDataParamCount();
        }
    }


    public List<String> getAddressingName() {
        return addressingName;
    }

    public void setAddressingName(String addressingName) {
        this.addressingName.clear();
        this.addAddressingName(addressingName);
    }

    public void addAddressingName(String addressingName) {
        if (addressingName != null) {
            this.addressingName.add(addressingName);
            this.increaseDataParamCount();
        }
    }

    @Override
    public Map<String, Object> getSearchParameters() {
        HashMap<String, Object> map = new HashMap<>(super.getSearchParameters());
        map.put(CODE, this.code);
        map.put(NAME, this.name);
        map.put(ADDRESSING_NAME, this.addressingName);
        return map;
    }

    @Override
    public BaseLookupDefinition getLookupDefinition() {
        BaseLookupDefinition lookupDefinition = super.getLookupDefinition();
        if (this.code != null && !this.code.isEmpty()) {
            lookupDefinition.put(RoadEntity.DB_FIELD_CODE, this.code, Integer.class);
        }
        if (this.name != null && !this.name.isEmpty()) {
            lookupDefinition.put(
                    RoadEntity.DB_FIELD_NAME + BaseLookupDefinition.separator + RoadNameRecord.DB_FIELD_NAME,
                    this.name,
                    String.class
            );
        }
        if (this.addressingName != null && !this.addressingName.isEmpty()) {
            lookupDefinition.put(
                    RoadEntity.DB_FIELD_NAME + BaseLookupDefinition.separator + RoadNameRecord.DB_FIELD_ADDRESSING_NAME,
                    this.addressingName,
                    String.class
            );
        }
        return lookupDefinition;
    }

    @Override
    public void setFromParameters(ParameterMap parameters) {
        super.setFromParameters(parameters);
        this.setCode(parameters.getFirst(CODE));
        this.setName(parameters.getFirst(NAME));
        this.setAddressingName(parameters.getFirst(ADDRESSING_NAME));
    }

}