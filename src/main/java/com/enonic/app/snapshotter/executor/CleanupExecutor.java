package com.enonic.app.snapshotter.executor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.app.snapshotter.model.CleanupJob;
import com.enonic.app.snapshotter.model.Schedules;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;

public class CleanupExecutor
    extends AbstractExecutor<CleanupJob>
{
    private final Logger LOG = LoggerFactory.getLogger( CleanupExecutor.class );

    public CleanupExecutor( final Builder builder )
    {
        super( builder );
    }

    public static Builder create()
    {
        return new Builder();
    }

    public void execute( final CleanupJob job )
    {
        doExecute( () -> deleteSnapshots( job.getSchedules() ), job );
    }

    private Instant getThreshold( final Duration dailyKeep )
    {
        return Instant.now().minus( dailyKeep );
    }

    private void deleteSnapshots( final Schedules schedules )
    {
        schedules.forEach( ( schedule ) -> deleteSnapshot( getThreshold( schedule.getKeep() ), schedule.getName() ) );
    }

    private void deleteSnapshot( final Instant threshold, final String prefix )
    {
        LOG.debug( "Deleting snapshots of type [" + prefix + "] older than [" + threshold + "]" );

        final SnapshotResults snapshots = this.nodeService.listSnapshots();

        final List<String> doBeDeleted = snapshots.stream().
            filter( ( snapshot ) -> snapshot.getTimestamp().isBefore( threshold ) ).
            filter( ( snapshot ) -> snapshot.getName().startsWith( prefix ) ).
            map( SnapshotResult::getName ).
            collect( Collectors.toList() );

        this.nodeService.deleteSnapshot( DeleteSnapshotParams.create().
            addAll( doBeDeleted ).
            build() );
    }

    public static class Builder
        extends AbstractExecutor.Builder<Builder>
    {
        public Builder()
        {
            super();
        }

        public CleanupExecutor build()
        {
            return new CleanupExecutor( this );
        }
    }

}
