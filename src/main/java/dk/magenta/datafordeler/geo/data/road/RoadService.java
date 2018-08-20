package dk.magenta.datafordeler.geo.data.road;

import dk.magenta.datafordeler.core.MonitorService;
import dk.magenta.datafordeler.core.arearestriction.AreaRestriction;
import dk.magenta.datafordeler.core.arearestriction.AreaRestrictionType;
import dk.magenta.datafordeler.core.exception.AccessDeniedException;
import dk.magenta.datafordeler.core.exception.AccessRequiredException;
import dk.magenta.datafordeler.core.exception.HttpNotFoundException;
import dk.magenta.datafordeler.core.exception.InvalidClientInputException;
import dk.magenta.datafordeler.core.fapi.FapiBaseService;
import dk.magenta.datafordeler.core.fapi.OutputWrapper;
import dk.magenta.datafordeler.core.plugin.AreaRestrictionDefinition;
import dk.magenta.datafordeler.core.plugin.Plugin;
import dk.magenta.datafordeler.core.user.DafoUserDetails;
import dk.magenta.datafordeler.geo.GeoAreaRestrictionDefinition;
import dk.magenta.datafordeler.geo.GeoPlugin;
import dk.magenta.datafordeler.geo.GeoRolesDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Stream;

@RestController("RoadService")
@RequestMapping("/geo/road/1/rest")
public class RoadService extends FapiBaseService<RoadEntity, RoadQuery> {

    @Autowired
    private GeoPlugin geoPlugin;

    @Autowired
    private MonitorService monitorService;

    @PostConstruct
    public void init() {
        this.monitorService.addAccessCheckPoint("/geo/road/1/rest/1234");
        this.monitorService.addAccessCheckPoint("/geo/road/1/rest/search?kode=1234");

        //this.setOutputWrapper(this.personRecordOutputWrapper);
    }

    @Override
    protected OutputWrapper.Mode getDefaultMode() {
        return OutputWrapper.Mode.LEGACY;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public String getServiceName() {
        return "road";
    }

    @Override
    protected Class<RoadEntity> getEntityClass() {
        return RoadEntity.class;
    }

    @Override
    public Plugin getPlugin() {
        return this.geoPlugin;
    }

    @Override
    protected void checkAccess(DafoUserDetails dafoUserDetails) throws AccessDeniedException, AccessRequiredException {
        // All have access
    }

    @Override
    protected RoadQuery getEmptyQuery() {
        return new RoadQuery();
    }

    @Override
    protected void sendAsCSV(Stream<RoadEntity> stream, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, HttpNotFoundException {

    }

    @Override
    protected void applyAreaRestrictionsToQuery(RoadQuery query, DafoUserDetails user) throws InvalidClientInputException {
        Collection<AreaRestriction> restrictions = user.getAreaRestrictionsForRole(GeoRolesDefinition.READ_GEO_ROLE);
        AreaRestrictionDefinition areaRestrictionDefinition = this.geoPlugin.getAreaRestrictionDefinition();
        AreaRestrictionType municipalityType = areaRestrictionDefinition.getAreaRestrictionTypeByName(GeoAreaRestrictionDefinition.RESTRICTIONTYPE_KOMMUNEKODER);
        for (AreaRestriction restriction : restrictions) {
            if (restriction.getType() == municipalityType) {
                query.addKommunekodeRestriction(restriction.getValue());
            }
        }
    }

}