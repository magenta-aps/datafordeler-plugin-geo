package dk.magenta.datafordeler.geo.data.road;

import dk.magenta.datafordeler.geo.data.GeoOutputWrapper;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class RoadOutputWrapper extends GeoOutputWrapper<RoadEntity> {

    @Override
    protected void fillContainer(OutputContainer container, RoadEntity item) {
        container.addNontemporal("vejkode", item.getCode());
        container.addMonotemporal("navn", item.getName());
        container.addMonotemporal("lokalitet", item.getLocality());
        container.addMonotemporal("kommune", item.getMunicipality());
        container.addNontemporal("sumiffiik", item.getSumiffiikId());
    }

}
