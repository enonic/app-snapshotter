package com.enonic.app.snapshotter.slack;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webcerebrium.slack.Notification;
import com.webcerebrium.slack.NotificationException;
import com.webcerebrium.slack.SlackMessage;

import com.enonic.app.snapshotter.model.Job;
import com.enonic.app.snapshotter.notifier.Notifier;

@Component(immediate = true)
public class SlackNotifier
    implements Notifier
{
    private SlackNotifierConfig config;

    private static final Logger LOG = LoggerFactory.getLogger( SlackNotifier.class );

    private Notification notification;

    @Activate
    public void activate()
    {
        notification = new Notification( this.config.slackWebhook() );
    }

    @Override
    public String name()
    {
        return "slack";
    }

    @Override
    public void success( final Job job )
    {
        if ( this.config.reportOnSuccess() )
        {
            sendSlackMessage( SlackMessageBuilder.create().
                project( config.project() ).
                failed( false ).
                job( job ).
                build().
                execute() );
        }
    }

    @Override
    public void failed( final Job job, final Exception e )
    {
        if ( this.config.reportOnFailure() )
        {
            sendSlackMessage( SlackMessageBuilder.create().
                project( config.project() ).
                failed( true ).
                job( job ).
                build().
                execute() );
        }
    }

    private void sendSlackMessage( final SlackMessage message )
    {
        try
        {
            notification.send( message );
        }
        catch ( NotificationException e )
        {
            LOG.error( "Failed to send slack-notification ", e );
        }
    }

    @Reference
    public void setConfig( final SlackNotifierConfig config )
    {
        this.config = config;
    }
}
