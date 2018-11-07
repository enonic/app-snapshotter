package com.enonic.app.snapshotter;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.app.snapshotter.model.Schedules;
import com.enonic.app.snapshotter.model.SchedulesParser;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(configurationPid = "com.enonic.app.snapshotter")
public class SnapshotterConfigImpl
    extends AbstractBaseConfig
    implements SnapshotterConfig
{
    private Configuration config;

    @Activate
    public void activate( final Map<String, String> map )
    {
        this.config = ConfigBuilder.create().
            load( getClass(), "default.properties" ).
            addAll( map ).
            build();

        this.config = new ConfigInterpolator().interpolate( this.config );
    }

    public Schedules getSchedules()
    {
        return doGetSchedules();
    }

    private Schedules doGetSchedules()
    {
        return SchedulesParser.parse( this.config );
    }

    @Override
    public Configuration getConfig()
    {
        return config;
    }

    @Override
    public String cleanCron()
    {
        return this.config.get( "cleanup.cron" );
    }

    @Override
    public List<String> notifiers()
    {
        return doGetCommaSeparated( "notifiers" );
    }


}
