package com.enonic.app.snapshotter.slack;

import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.app.snapshotter.AbstractBaseConfig;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(configurationPid = "com.enonic.app.snapshotter.slack")
public class SlackNotifierConfigImpl
    extends AbstractBaseConfig
    implements SlackNotifierConfig
{
    private Configuration config;

    @SuppressWarnings("unused")
    @Activate
    public void activate( final Map<String, String> map )
    {
        this.config = ConfigBuilder.create().
            addAll( map ).
            build();

        this.config = new ConfigInterpolator().interpolate( this.config );
    }

    public Configuration getConfig()
    {
        return this.config;
    }

    @Override
    public String slackWebhook()
    {
        return this.config.getOrDefault( "slackWebhook", null );
    }

    @Override
    public String project()
    {
        return this.config.getOrDefault( "project", "UNKNOWN" );
    }

    @Override
    public boolean reportOnSuccess()
    {
        return this.config.getOrDefault( "reportOnSuccess", Boolean.class, false );
    }

    @Override
    public boolean reportOnFailure()
    {
        return this.config.getOrDefault( "reportOnFailure", Boolean.class, true );
    }
}

