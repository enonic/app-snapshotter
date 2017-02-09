package com.enonic.app.snapshotter.mail;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.app.snapshotter.SnapshotterConfig;
import com.enonic.app.snapshotter.model.Job;
import com.enonic.xp.mail.MailService;

@Component(immediate = true)
public class MailSenderImpl
    implements MailSender
{
    private MailService mailService;

    private SnapshotterConfig config;

    private final static Logger LOG = LoggerFactory.getLogger( MailSenderImpl.class );

    public void sendFailed( final Job job, final Exception e )
    {
        this.mailService.send( SnapshotMail.create().
            body( getStackTrace( e ) ).
            from( config.from() ).
            to( config.to() ).
            subject( SubjectFactory.createSubject( job, config, "FAILED" ) ).
            build() );
    }

    public void sendSuccess( final Job job )
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


    private String getStackTrace( final Exception e )
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw );
        e.printStackTrace( pw );
        return sw.toString();
    }

    @Reference
    public void setMailService( final MailService mailService )
    {
        this.mailService = mailService;
    }

    @Reference
    public void setConfig( final SnapshotterConfig config )
    {
        this.config = config;
    }
}
