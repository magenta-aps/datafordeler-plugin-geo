package dk.magenta.datafordeler.geo.data.municipality;

import dk.magenta.datafordeler.geo.data.GeoOutputWrapper;
import dk.magenta.datafordeler.geo.data.postcode.PostcodeEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MunicipalityOutputWrapper extends GeoOutputWrapper<MunicipalityEntity> {

    @Override
    protected void fillMetadataContainer(OutputContainer container, MunicipalityEntity item) {
        container.addNontemporal("kommunekode", item.getCode());
        container.addMonotemporal("navn", item.getName());
    }

    @Override
    public List<String> getRemoveFieldNames() {
        return null;
    }
}
