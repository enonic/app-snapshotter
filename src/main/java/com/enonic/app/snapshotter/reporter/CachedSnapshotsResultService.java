package com.enonic.app.snapshotter.reporter;


import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.snapshot.SnapshotService;

@Component(immediate = true)
public class CachedSnapshotsResultService
    implements SnapshotResultsService
{
    private SnapshotService snapshotService;

    private final Cache<String, SnapshotResults> snapshotCache =
        CacheBuilder.newBuilder().maximumSize( 1 ).expireAfterAccess( 2, TimeUnit.MINUTES ).build();

    public SnapshotResults get()
    {
        try
        {
            return snapshotCache.get( "results", () -> snapshotService.list() );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException( "cannot get snapshots", e );
        }
    }
    
    @SuppressWarnings("unused")
    @Reference
    public void setSnapshotService( final SnapshotService snapshotService )
    {
        this.snapshotService = snapshotService;
    }
}
