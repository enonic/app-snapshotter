package com.enonic.app.snapshotter.mail;

import com.enonic.app.snapshotter.model.Job;

public interface MailSender
{
    void sendSuccess( final Job job );

    void sendFailed( final Job job, final Exception e );
}
