package com.enonic.app.snapshotter.mail;

import java.util.List;
import java.util.Map;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import com.enonic.app.snapshotter.AbstractBaseConfig;
import com.enonic.xp.config.ConfigBuilder;
import com.enonic.xp.config.ConfigInterpolator;
import com.enonic.xp.config.Configuration;

@Component(configurationPid = "com.enonic.app.snapshotter.mail")
public class MailNotifierConfigImpl
    extends AbstractBaseConfig
    implements MailNotifierConfig
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
        return this.config.getOrDefault( "mail.slackWebhook", null );
    }

    @Override
    public boolean mailIsConfigured()
    {
        return !this.to().isEmpty() && !this.from().isEmpty();
    }

}
