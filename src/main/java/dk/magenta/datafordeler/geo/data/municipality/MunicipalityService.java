package dk.magenta.datafordeler.geo.data.municipality;

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

@RestController("GeoMunicipalityService")
@RequestMapping("/geo/municipality/1/rest")
public class MunicipalityService extends FapiBaseService<MunicipalityEntity, MunicipalityQuery> {

    @Autowired
    private GeoPlugin geoPlugin;

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private MunicipalityOutputWrapper municipalityOutputWrapper;

    @PostConstruct
    public void init() {
        //this.monitorService.addAccessCheckPoint("/geo/municipality/1/rest/1234");
        //this.monitorService.addAccessCheckPoint("/geo/municipality/1/rest/search?kommunekode=956");

        this.setOutputWrapper(this.municipalityOutputWrapper);
    }

    @Override
    protected OutputWrapper.Mode getDefaultMode() {
        return OutputWrapper.Mode.DRV;
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public String getServiceName() {
        return "municipality";
    }

    @Override
    protected Class<MunicipalityEntity> getEntityClass() {
        return MunicipalityEntity.class;
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
    protected MunicipalityQuery getEmptyQuery() {
        return new MunicipalityQuery();
    }

    @Override
    protected void sendAsCSV(Stream<MunicipalityEntity> stream, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws IOException, HttpNotFoundException {

    }

    @Override
    protected void applyAreaRestrictionsToQuery(MunicipalityQuery query, DafoUserDetails user) throws InvalidClientInputException {
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
