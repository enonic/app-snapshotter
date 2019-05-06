package com.enonic.app.snapshotter.mail;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.app.snapshotter.model.Job;
import com.enonic.app.snapshotter.notifier.Notifier;
import com.enonic.xp.mail.MailService;

@Component(immediate = true)
public class MailNotifier
    implements Notifier
{
    private MailService mailService;

    private MailNotifierConfig config;

    private final static Logger LOG = LoggerFactory.getLogger( MailNotifier.class );

    @Override
    public String name()
    {
        return "mail";
    }

    @Override
    public void failed( final Job job, final Exception e )
    {
        this.mailService.send( SnapshotMail.create().
            body( getStackTrace( e ) ).
            from( config.from() ).
            to( config.to() ).
            subject( SubjectFactory.createSubject( job, config, "FAILED" ) ).
            build() );
    }

    @Override
    public void success( final Job job )
    {
        try
        {
            this.mailService.send( SnapshotMail.create().
                from( config.from() ).
                to( config.to() ).
                subject( SubjectFactory.createSubject( job, config, "OK" ) ).
                build() );
        }
        catch ( Exception e )
        {
            LOG.warn( "Mail could not be sent", e );
        }
    }

    @Override
    public void test( final String message )
    {
        try
        {
            this.mailService.send( SnapshotMail.create().
                from( config.from() ).
                to( config.to() ).
                subject( message ).
                build() );
        }
        catch ( Exception e )
        {
            LOG.warn( "Mail could not be sent", e );
        }
    }

    private String getStackTrace( final Exception e )
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        e.printStackTrace( pw );
        return sw.toString();
    }

    @Override
    public Map<String, Object> getPublicConfig()
    {
        final HashMap<String, Object> configMap = Maps.newHashMap();
        configMap.put( "mailOnFailure", this.config.mailOnFailure() );
        configMap.put( "mailOnSuccess", this.config.mailOnSuccess() );
        configMap.put( "from", this.config.from() );
        configMap.put( "hostname", this.config.hostname() );
        configMap.put( "to", this.config.to() );
        return configMap;
    }

    @Reference
    public void setMailService( final MailService mailService )
    {
        this.mailService = mailService;
    }

    @Reference
    public void setConfig( final MailNotifierConfig config )
    {
        this.config = config;
    }
}