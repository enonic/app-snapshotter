package com.enonic.app.snapshotter.executor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.app.snapshotter.SnapshotterConfig;
import com.enonic.app.snapshotter.mail.MailSender;
import com.enonic.app.snapshotter.model.CleanupJob;
import com.enonic.app.snapshotter.model.Schedule;
import com.enonic.app.snapshotter.model.Schedules;
import com.enonic.xp.node.DeleteSnapshotParams;
import com.enonic.xp.node.DeleteSnapshotsResult;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;

public class CleanupExecutorTest
{
    private NodeService nodeService;

    private MailSender mailSender;

    @Before
    public void setUp()
        throws Exception
    {
        this.nodeService = Mockito.mock( NodeService.class );
        this.mailSender = Mockito.mock( MailSender.class );
    }

    @Test
    public void name()
        throws Exception
    {
        Mockito.when( this.nodeService.listSnapshots() ).
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

        Mockito.when( this.nodeService.deleteSnapshot( Mockito.isA( DeleteSnapshotParams.class ) ) ).
            thenReturn( DeleteSnapshotsResult.create().
                add( "myCleanTest-1" ).
                add( "myCleanTest-2" ).
                build() );

        final SnapshotterConfig config = createConfig();

        final CleanupExecutor executor = CleanupExecutor.create().
            nodeService( this.nodeService ).
            mailSender( this.mailSender ).
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
            public Boolean mailOnSuccess()
            {
                return true;
            }

            @Override
            public Boolean mailOnFailure()
            {
                return true;
            }

            @Override
            public List<String> from()
            {
                return null;
            }

            @Override
            public List<String> to()
            {
                return null;
            }

            @Override
            public String hostname()
            {
                return null;
            }
        };
    }
}