package dk.magenta.datafordeler.geo.data.postcode;

import dk.magenta.datafordeler.geo.data.GeoOutputWrapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PostcodeOutputWrapper extends GeoOutputWrapper<PostcodeEntity> {

    @Override
    protected void fillMetadataContainer(OutputContainer container, PostcodeEntity item) {
        container.addNontemporal("postnummer", item.getCode());
        container.addNontemporal("postdistrikt", item.getName());
    }

    @Override
    public List<String> getRemoveFieldNames() {
        return null;
    }
}
