package com.enonic.app.snapshotter.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.app.snapshotter.SnapshotterConfig;
import com.enonic.app.snapshotter.model.Job;
import com.enonic.app.snapshotter.notifier.Notifiers;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.snapshot.SnapshotService;

public abstract class AbstractExecutor<T extends Job>
    implements Executor<T>
{
    protected final SnapshotterConfig config;

    protected final SnapshotService snapshotService;

    protected final Notifiers notifiers;

    protected final IndexService indexService;

    private final Logger LOG = LoggerFactory.getLogger( getClass() );

    protected AbstractExecutor( final Builder builder )
    {
        config = builder.config;
        snapshotService = builder.snapshotService;
        notifiers = builder.notifiers;
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
            notifiers.success( job );
        }
        catch ( Exception e )
        {
            LOG.error( "Snapshotter job [" + job.description() + "] failed", e );
            notifiers.failed( job, e );
        }
    }

    public static class Builder<B extends Builder>
    {
        private SnapshotterConfig config;

        private SnapshotService snapshotService;

        private Notifiers notifiers;

        private IndexService indexService;

        @SuppressWarnings("unchecked")
        public B config( final SnapshotterConfig val )
        {
            config = val;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B notifiers( final Notifiers notifiers )
        {
            this.notifiers = notifiers;
            return (B) this;
        }

        @SuppressWarnings("unchecked")
        public B snapshotService( final SnapshotService val )
        {
            snapshotService = val;
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
