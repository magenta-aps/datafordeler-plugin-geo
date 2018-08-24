package dk.magenta.datafordeler.geo.data.municipality;

import dk.magenta.datafordeler.geo.data.GeoOutputWrapper;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class MunicipalityOutputWrapper extends GeoOutputWrapper<MunicipalityEntity> {

    @Override
    protected void fillMetadataContainer(OutputContainer container, MunicipalityEntity item) {
        container.addNontemporal("kommunekode", item.getCode());
        container.addMonotemporal("navn", item.getName());
    }

    @Override
    public Set<String> getRemoveFieldNames() {
        HashSet<String> fields = new HashSet<>();
        fields.add(GeoMonotemporalRecord.IO_FIELD_EDITOR);
        return fields;
    }
}
