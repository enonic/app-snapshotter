package com.enonic.app.snapshotter.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.app.snapshotter.SnapshotterConfig;
import com.enonic.app.snapshotter.mail.MailSender;
import com.enonic.app.snapshotter.model.Job;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;

public abstract class AbstractExecutor<T extends Job>
    implements Executor<T>
{
    protected final SnapshotterConfig config;

    protected final NodeService nodeService;

    protected final MailSender mailSender;

    protected final IndexService indexService;

    private final Logger LOG = LoggerFactory.getLogger( getClass() );

    protected AbstractExecutor( final Builder builder )
    {
        config = builder.config;
        nodeService = builder.nodeService;
        mailSender = builder.mailSender;
        indexService = builder.indexService;
    }

    protected void doExecute( final Runnable runnable, final Job job )
    {

        if ( !this.indexService.isMaster() )
        {
            return;
        }

        try
        {
            runnable.run();

            if ( this.config.mailOnSuccess() && this.config.mailIsConfigured() )
            {
                this.mailSender.sendSuccess( job );
            }

        }
        catch ( Exception e )
        {
            LOG.error( "Snapshotter job [" + job.description() + "] failed", e );

            if ( this.config.mailOnFailure() && this.config.mailIsConfigured() )
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

        private IndexService indexService;

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

        @SuppressWarnings("unchecked")
        public B indexService( final IndexService val )
        {
            this.indexService = val;
            return (B) this;
        }
    }
}
