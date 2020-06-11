package com.enonic.app.snapshotter.reporter;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.snapshot.SnapshotService;

public class CachedSnapshotsResultServiceTest
{

    private CachedSnapshotsResultService service;

    private SnapshotService snapshotService;

    @BeforeEach
    public void setup()
    {
        this.snapshotService = Mockito.mock( SnapshotService.class );
        this.service = new CachedSnapshotsResultService();
        service.setSnapshotService( snapshotService );
    }

    @Test
    public void name()
    {
        final SnapshotResults snapshotResults = SnapshotResults.create().
            add( SnapshotResult.create().
                name( "snapshot1" ).
                timestamp( Instant.now() ).
                build() ).
            build();

        Mockito.when( this.snapshotService.list() ).thenReturn( snapshotResults );

        this.service.setDuration( 1 );
        this.service.setUnit( TimeUnit.SECONDS );
        this.service.activate();

        this.service.get();

        try
        {
            Thread.sleep( 1000 );
        }
        catch ( InterruptedException e )
        {
            e.printStackTrace();
        }

        this.service.get();

        Mockito.verify( this.snapshotService, Mockito.times( 2 ) ).list();

    }
}