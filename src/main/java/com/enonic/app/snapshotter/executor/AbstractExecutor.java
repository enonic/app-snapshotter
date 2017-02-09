package com.enonic.app.snapshotter.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.app.snapshotter.SnapshotterConfig;
import com.enonic.app.snapshotter.mail.MailSender;
import com.enonic.app.snapshotter.model.Job;
import com.enonic.xp.node.NodeService;

public abstract class AbstractExecutor<T extends Job>
    implements Executor<T>
{
    protected final SnapshotterConfig config;

    protected final NodeService nodeService;

    protected final MailSender mailSender;

    private final Logger LOG = LoggerFactory.getLogger( getClass() );

    protected AbstractExecutor( final Builder builder )
    {
        config = builder.config;
        nodeService = builder.nodeService;
        mailSender = builder.mailSender;
    }

    protected void doExecute( final Runnable runnable, final Job job )
    {
        try
        {
            runnable.run();

            if ( this.config.mailOnSuccess() )
            {
                this.mailSender.sendSuccess( job );
            }

        }
        catch ( Exception e )
        {
            LOG.warn( "Snapshot job [" + job.description() + "] failed", e );

            if ( this.config.mailOnFailure() )
            {
                this.mailSender.sendFailed( job, e );
            }
        }
    }

    public static class Builder<B extends Builder>
    {
        private SnapshotterConfig config;

        private NodeService nodeService;

        private MailSender mailSender;

        @SuppressWarnings("unchecked")
        public B config( final SnapshotterConfig val )
        {
            config = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B mailSender( final MailSender mailSender )
        {
            this.mailSender = mailSender;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B nodeService( final NodeService val )
        {
            nodeService = val;
            return (B) this;
        }
    }
}
