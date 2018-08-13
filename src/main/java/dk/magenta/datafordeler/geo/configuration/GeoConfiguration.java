package dk.magenta.datafordeler.geo.configuration;

import dk.magenta.datafordeler.core.configuration.Configuration;
import dk.magenta.datafordeler.geo.GeoPlugin;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="geo_config")
public class GeoConfiguration implements Configuration {

    @Id
    @Column(name = "id")
    private final String plugin = GeoPlugin.class.getName();

}
