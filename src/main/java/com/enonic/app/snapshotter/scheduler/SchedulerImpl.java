package com.enonic.app.snapshotter.scheduler;

import java.time.Duration;
import java.util.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.app.snapshotter.model.Job;

public class SchedulerImpl
    implements Scheduler
{
    private final Timer timer;

    private final static Logger LOG = LoggerFactory.getLogger( SchedulerImpl.class );

    public SchedulerImpl()
    {
        this.timer = new Timer( "Snapshot scheduler" );
    }

    @Override
    public void schedule( final Job job )
    {
        final Duration executionTime = job.nextExecutionTime();
        LOG.info( "Scheduling job: " + job.description() + " in " + executionTime.toString() );
        final JobTask task = new JobTask( job.executor(), job, this );
        this.timer.schedule( task, executionTime.toMillis() );
    }

    @Override
    public void unschedule()
    {
        LOG.info( "Removing all scheduled snapshots" );
        this.timer.cancel();
        this.timer.purge();
    }
}
