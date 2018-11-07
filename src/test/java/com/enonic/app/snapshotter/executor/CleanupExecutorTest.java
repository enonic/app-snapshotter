package com.enonic.app.snapshotter.executor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.app.snapshotter.SnapshotterConfig;
import com.enonic.app.snapshotter.model.CleanupJob;
import com.enonic.app.snapshotter.model.Schedule;
import com.enonic.app.snapshotter.model.Schedules;
import com.enonic.app.snapshotter.notifier.Notifiers;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.snapshot.SnapshotService;

public class CleanupExecutorTest
{
    private SnapshotService snapshotService;

    private Notifiers mailSender;

    private IndexService indexService;

    @Before
    public void setUp()
        throws Exception
    {
        this.snapshotService = Mockito.mock( SnapshotService.class );
        this.mailSender = Mockito.mock( Notifiers.class );
        this.indexService = Mockito.mock( IndexService.class );
    }

    @Test
    public void execute()
        throws Exception
    {
        Mockito.when( this.indexService.isMaster() ).
            thenReturn( true );

        Mockito.when( this.snapshotService.list() ).
            thenReturn( SnapshotResults.create().
                add( SnapshotResult.create().
                    name( "myCleanTest-1" ).
                    timestamp( Instant.now().minus( Duration.parse( "PT10S" ) ) ).
                    build() ).
                add( SnapshotResult.create().
                    name( "myCleanTest-2" ).
                    timestamp( Instant.now().minus( Duration.parse( "PT15S" ) ) ).
                    build() ).
                build() );

        Mockito.when( this.snapshotService.delete( Mockito.isA( DeleteSnapshotParams.class ) ) ).
            thenReturn( DeleteSnapshotsResult.create().
                add( "myCleanTest-1" ).
                add( "myCleanTest-2" ).
                build() );

        final SnapshotterConfig config = createConfig();

        final CleanupExecutor executor = CleanupExecutor.create().
            snapshotService( this.snapshotService ).
            notifiers( this.mailSender ).
            indexService( this.indexService ).
            config( config ).
            build();

        executor.execute( CleanupJob.create().
            trigger( "* * * * *" ).
            executor( executor ).
            schedules( config.getSchedules() ).
            build() );
    }

    private SnapshotterConfig createConfig()
    {
        return new SnapshotterConfig()
        {
            @Override
            public Schedules getSchedules()
            {
                return Schedules.create().
                    add( Schedule.create().
                        keep( "PT1S" ).
                        trigger( "* * * * *" ).
                        name( "myCleanTest" ).
                        build() ).
                    build();
            }

            @Override
            public String cleanCron()
            {
                return "* * * * *";
            }

            @Override
            public List<String> notifiers()
            {
                return Lists.newArrayList();
            }
        };
    }
}