package dk.magenta.datafordeler.geo.data.municipality;

import dk.magenta.datafordeler.geo.data.GeoOutputWrapper;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class MunicipalityOutputWrapper extends GeoOutputWrapper<MunicipalityEntity> {

    @Override
    protected void fillContainer(OutputContainer container, MunicipalityEntity item) {
        container.addNontemporal("kommunekode", item.getCode());
        container.addMonotemporal("navn", item.getName());
        container.addNontemporal("sumiffiik", item.getSumiffiikId());
    }

}
