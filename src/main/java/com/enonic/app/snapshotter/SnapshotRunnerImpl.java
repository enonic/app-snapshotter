package com.enonic.app.snapshotter;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import com.enonic.app.snapshotter.executor.CleanupExecutor;
import com.enonic.app.snapshotter.executor.SnapshotExecutor;
import com.enonic.app.snapshotter.mail.MailSender;
import com.enonic.app.snapshotter.model.CleanupJob;
import com.enonic.app.snapshotter.model.Schedule;
import com.enonic.app.snapshotter.model.Schedules;
import com.enonic.app.snapshotter.model.SnapshotJob;
import com.enonic.app.snapshotter.scheduler.Scheduler;
import com.enonic.app.snapshotter.scheduler.SchedulerImpl;
import com.enonic.xp.index.IndexService;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.snapshot.SnapshotService;

@Component(immediate = true)
public class SnapshotRunnerImpl
    implements SnapshotRunner
{
    private Scheduler scheduler;

    private SnapshotExecutor snapshotExecutor;

    private CleanupExecutor cleanupExecutor;

    private SnapshotterConfig config;

    private SnapshotService snapshotService;

    private MailSender mailSender;

    private IndexService indexService;

    @Activate
    public void activate()
    {
        this.scheduler = new SchedulerImpl();
        this.snapshotExecutor = SnapshotExecutor.create().
            snapshotService( this.snapshotService ).
            config( this.config ).
            mailSender( this.mailSender ).
            indexService( this.indexService ).
            build();

        this.cleanupExecutor = CleanupExecutor.create().
            snapshotService( this.snapshotService ).
            config( this.config ).
            mailSender( this.mailSender ).
            indexService( this.indexService ).
            build();

        final Schedules schedules = this.config.getSchedules();

        scheduleSnapshotting( schedules );
        scheduleCleanup( schedules );
    }

    private void scheduleSnapshotting( final Schedules schedules )
    {

        for ( final Schedule schedule : schedules )
        {
            this.scheduler.schedule( SnapshotJob.create().
                schedule( schedule ).
                executor( this.snapshotExecutor ).
                build() );
        }
    }

    private void scheduleCleanup( final Schedules schedules )
    {
        this.scheduler.schedule( CleanupJob.create().
            trigger( config.cleanCron() ).
            executor( this.cleanupExecutor ).
            schedules( schedules ).
            build() );
    }

    @Deactivate
    public void deactivate()
    {
        this.scheduler.unschedule();
    }

    @Reference
    public void setSnapshotService( final SnapshotService snapshotService )
    {
        this.snapshotService = snapshotService;
    }

    @Reference
    public void setConfig( final SnapshotterConfig config )
    {
        this.config = config;
    }

    @Reference
    public void setMailSender( final MailSender mailSender )
    {
        this.mailSender = mailSender;
    }

    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}

