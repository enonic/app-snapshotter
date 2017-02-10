package com.enonic.app.snapshotter;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.enonic.app.snapshotter.model.Schedules;
import com.enonic.app.snapshotter.model.SchedulesParser;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(configurationPid = "com.enonic.app.snapshotter")
public class SnapshotterConfigImpl
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
    public String cleanCron()
    {
        return this.config.get( "cleanup.cron" );
    }

    @Override
    public Boolean mailOnSuccess()
    {
        return Boolean.valueOf( this.config.get( "mail.onSuccess" ) );
    }

    @Override
    public Boolean mailOnFailure()
    {
        return Boolean.valueOf( this.config.get( "mail.onFailure" ) );
    }

    @Override
    public List<String> from()
    {
        return doGetCommaSeparated( "mail.from" );
    }

    @Override
    public List<String> to()
    {
        return doGetCommaSeparated( "mail.to" );
    }

    @Override
    public String hostname()
    {
        return this.config.getOrDefault( "mail.hostname", null );
    }

    @Override
    public boolean mailIsConfigured()
    {
        return !this.to().isEmpty() && !this.from().isEmpty();
    }

    private List<String> doGetCommaSeparated( final String property )
    {
        final List<String> entries = Lists.newArrayList();

        if ( this.config.exists( property ) )
        {
            final String[] split = this.config.get( property ).split( "," );

            for ( final String entry : split )
            {
                if ( !Strings.isNullOrEmpty( entry ) )
                {
                    entries.add( entry );
                }
            }
        }
        return entries;
    }
}
