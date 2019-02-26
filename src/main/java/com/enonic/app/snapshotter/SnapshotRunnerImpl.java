package com.enonic.app.snapshotter;

import java.util.List;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.app.snapshotter.executor.CleanupExecutor;
import com.enonic.app.snapshotter.executor.SnapshotExecutor;
import com.enonic.app.snapshotter.model.CleanupJob;
import com.enonic.app.snapshotter.model.Schedule;
import com.enonic.app.snapshotter.model.Schedules;
import com.enonic.app.snapshotter.model.SnapshotJob;
import com.enonic.app.snapshotter.notifier.Notifier;
import com.enonic.app.snapshotter.notifier.Notifiers;
import com.enonic.app.snapshotter.scheduler.Scheduler;
import com.enonic.app.snapshotter.scheduler.SchedulerImpl;
import com.enonic.xp.index.IndexService;
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

    private Notifiers notifiers;

    private IndexService indexService;

    private List<String> configuredNotifiers;

    private final static Logger LOG = LoggerFactory.getLogger( SnapshotRunnerImpl.class );

    @Activate
    public void activate()
    {
        this.notifiers = new Notifiers();
        this.configuredNotifiers = this.config.notifiers();
        doStart();
    }

    public Notifiers getNotifiers()
    {
        return this.notifiers;
    }

    private synchronized void doStart()
    {
        if ( this.scheduler != null )
        {
            LOG.info( "Scheduler already started" );
            return;
        }

        if ( !this.notifiers.hasAll( this.configuredNotifiers ) )
        {
            LOG.info( "Waiting for notifiers: " + this.configuredNotifiers );
            return;
        }

        LOG.info( "All notifiers configured, starting schedules" );

        this.scheduler = new SchedulerImpl();
        this.snapshotExecutor = SnapshotExecutor.create().
            snapshotService( this.snapshotService ).
            config( this.config ).
            notifiers( this.notifiers ).
            indexService( this.indexService ).
            build();

        this.cleanupExecutor = CleanupExecutor.create().
            snapshotService( this.snapshotService ).
            config( this.config ).
            notifiers( this.notifiers ).
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

    @SuppressWarnings("unused")
    @Deactivate
    public void deactivate()
    {
        if ( this.scheduler != null )
        {
            this.scheduler.unschedule();
        }
    }

    @SuppressWarnings("unused")
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

    @SuppressWarnings("unused")
    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addNotifier( final Notifier notifier )
    {
        if ( this.configuredNotifiers.contains( notifier.name() ) )
        {
            this.notifiers.add( notifier );
        }

        doStart();
    }

    @SuppressWarnings("unused")
    public void removeNotifier( final Notifier notifier )
    {
        this.notifiers.remove( notifier );
        // TODO STOP IF NOT ENOUGHT NOTIFIERS
    }

    @Reference
    public void setIndexService( final IndexService indexService )
    {
        this.indexService = indexService;
    }
}

