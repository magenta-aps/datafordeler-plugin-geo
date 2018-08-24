package dk.magenta.datafordeler.geo.data.locality;

import dk.magenta.datafordeler.geo.data.GeoOutputWrapper;
import dk.magenta.datafordeler.geo.data.common.GeoMonotemporalRecord;
import dk.magenta.datafordeler.geo.data.road.RoadEntity;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class LocalityOutputWrapper extends GeoOutputWrapper<LocalityEntity> {

    @Override
    protected void fillMetadataContainer(OutputContainer container, LocalityEntity item) {
        container.addNontemporal("lokalitetskode", item.getCode());
        System.out.println(item.getName());
        container.addMonotemporal("navn", item.getName());
        container.addMonotemporal("kommune", item.getMunicipality());
    }

}
