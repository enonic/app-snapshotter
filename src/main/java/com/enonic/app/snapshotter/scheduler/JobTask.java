package com.enonic.app.snapshotter.scheduler;

import java.util.TimerTask;

import com.enonic.app.snapshotter.executor.Executor;
import com.enonic.app.snapshotter.model.Job;

public class JobTask
    extends TimerTask
{
    private final Executor executor;

    private final Job job;

    private final Scheduler scheduler;

    public JobTask( final Executor executor, final Job job, final Scheduler scheduler )
    {
        this.executor = executor;
        this.job = job;
        this.scheduler = scheduler;
    }

    @Override
    public void run()
    {
        this.executor.execute( this.job );
        this.scheduler.schedule( this.job );
    }
}
