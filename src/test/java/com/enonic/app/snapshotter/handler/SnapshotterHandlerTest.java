package com.enonic.app.snapshotter.handler;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.SnapshotParams;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.testing.ScriptTestSupport;


public class SnapshotterHandlerTest
    extends ScriptTestSupport
{

    private SnapshotService snapshotService;

    @BeforeEach
    public void initialize()
        throws Exception
    {
        super.initialize();

        this.snapshotService = Mockito.mock( SnapshotService.class );
        addService( SnapshotService.class, this.snapshotService );
    }

    @Test
    public void testSnapshot()
    {
        final SnapshotResult snapshotResult = SnapshotResult.create().state( SnapshotResult.State.SUCCESS ).build();

        Mockito.when( snapshotService.snapshot( Mockito.any( SnapshotParams.class ) ) ).thenReturn( snapshotResult );

        runFunction( "test/SnapshotterHandlerTest.js", "snapshot" );

        Mockito.verify( snapshotService, Mockito.times( 1 ) ).snapshot( Mockito.any( SnapshotParams.class ) );
    }

    @Test
    public void testDeleteSnapshot()
    {
        final String snapshotName = "com.enonic.app.snapshotter--name_2020-01-01T00-00-00.000z";
        final String snapshotNameOldFormat = "name_2020-01-01T00-00-00.000z";
        final String snapshotWithOtherName = "com.enonic.app.snapshotter--nameother_2020-01-01T00-00-00.000z";
        final SnapshotResults snapshots = SnapshotResults.create()
            .add( SnapshotResult.create().name( snapshotName ).timestamp( Instant.now().minus( 2, ChronoUnit.HOURS ) ).build() )
            .add( SnapshotResult.create().name( snapshotNameOldFormat ).timestamp( Instant.now().minus( 2, ChronoUnit.HOURS ) ).build() )
            .add( SnapshotResult.create().name( snapshotWithOtherName ).timestamp( Instant.now().minus( 2, ChronoUnit.HOURS ) ).build() )
            .build();

        final DeleteSnapshotsResult deleteSnapshotsResult = DeleteSnapshotsResult.create().add( snapshotName ).add( snapshotNameOldFormat ).build();

        Mockito.when( snapshotService.list() ).thenReturn( snapshots );
        Mockito.when( snapshotService.delete( Mockito.any( DeleteSnapshotParams.class ) ) ).thenReturn( deleteSnapshotsResult );

        runFunction( "test/SnapshotterHandlerTest.js", "deleteSnapshot" );

        final ArgumentCaptor<DeleteSnapshotParams> captor = ArgumentCaptor.forClass( DeleteSnapshotParams.class );

        Mockito.verify( snapshotService, Mockito.times( 1 ) ).list();
        Mockito.verify( snapshotService, Mockito.times( 1 ) ).delete( captor.capture() );
        assertEquals( captor.getValue().getSnapshotNames().size(), 2 );
    }

    @Test
    public void testConfig()
    {
        runFunction( "test/SnapshotterHandlerTest.js", "getConfig" );
    }

    @Test
    public void testGetDefaultHost()
    {
        final SnapshotterHandler instance = new SnapshotterHandler();

        Assert.assertNotNull( instance.getDefaultHost() );
    }

}
