package com.enonic.app.snapshotter.executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.app.snapshotter.model.SnapshotJob;
import com.enonic.app.snapshotter.model.SnapshotParamsFactory;

public final class SnapshotExecutor
    extends AbstractExecutor<SnapshotJob>
{
    private final Logger LOG = LoggerFactory.getLogger( SnapshotExecutor.class );

    public SnapshotExecutor( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void execute( final SnapshotJob snapshotJob )
    {
        doExecute( () -> this.snapshotService.snapshot( SnapshotParamsFactory.create( snapshotJob ) ), snapshotJob );
    }

    public static class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        public SnapshotExecutor build()
        {
            return new SnapshotExecutor( this );
        }
    }
}
