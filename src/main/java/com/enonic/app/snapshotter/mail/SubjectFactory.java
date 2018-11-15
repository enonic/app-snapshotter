package com.enonic.app.snapshotter.mail;

import java.io.IOException;
import java.net.InetAddress;

import com.enonic.app.snapshotter.model.Job;

class SubjectFactory
{
    static String createSubject( final Job job, final MailNotifierConfig config, final String state )
    {
        StringBuilder builder = new StringBuilder();
        builder.append( "com.enonic.app.snapshotter" );
        builder.append( " - " );
        builder.append( "Host: " + ( config.hostname() != null ? config.hostname() : getHost() ) );
        builder.append( " - " );
        builder.append( "job: " + job.description() );
        builder.append( " - " );
        builder.append( " Status: " + state );
        return builder.toString();
    }

    private static String getHost()
    {
        try
        {
            return InetAddress.getLocalHost().getHostName();
        }
        catch ( IOException e )
        {
            return "UNKNOWN";
        }
    }
}
