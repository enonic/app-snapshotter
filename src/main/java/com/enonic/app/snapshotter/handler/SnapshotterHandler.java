package com.enonic.app.snapshotter.handler;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.snapshot.SnapshotService;

public class SnapshotterHandler
    implements ScriptBean
{
    private final Logger LOG = LoggerFactory.getLogger( SnapshotterHandler.class );

    private Supplier<SnapshotService> snapshotServiceSupplier;

    @Override
    public void initialize( final BeanContext context )
    {
        this.snapshotServiceSupplier = context.getService( SnapshotService.class );
    }

    public String snapshot( final String scheduleName, final String repositoryId )
    {
        final SnapshotParams.Builder paramsBuilder = SnapshotParams.create().
            snapshotName( scheduleName + "_" + createTimestampString() );

        if ( !Strings.isNullOrEmpty( repositoryId ) )
        {
            paramsBuilder.repositoryId( RepositoryId.from( repositoryId ) );
        }

        final SnapshotResult snapshotResult = snapshotServiceSupplier.get().snapshot( paramsBuilder.build() );

        return snapshotResult.getState().name();
    }

    public void deleteSnapshot( final String scheduleName, final String keep )
    {
        deleteSnapshot( scheduleName, getThreshold( Duration.parse( keep ) ) );
    }

    public String getDefaultHost()
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

    private void deleteSnapshot( final String scheduleName, final Instant threshold )
    {
        LOG.debug( "Deleting snapshots of type [" + scheduleName + "] older than [" + threshold + "]" );

        final SnapshotResults snapshots = snapshotServiceSupplier.get().list();

        final List<String> toBeDeleted = snapshots.stream().
            filter( ( snapshot ) -> snapshot.getTimestamp().isBefore( threshold ) ).
            filter( ( snapshot ) -> snapshot.getName().startsWith( scheduleName ) ).
            map( SnapshotResult::getName ).
            collect( Collectors.toList() );

        if ( !toBeDeleted.isEmpty() )
        {
            snapshotServiceSupplier.get().delete( DeleteSnapshotParams.create().
                addAll( toBeDeleted ).
                build() );
        }
    }

    private Instant getThreshold( final Duration dailyKeep )
    {
        return Instant.now().minus( dailyKeep );
    }

    private String createTimestampString()
    {
        return Instant.now().toString().toLowerCase().replace( ":", "_" );
    }

}
