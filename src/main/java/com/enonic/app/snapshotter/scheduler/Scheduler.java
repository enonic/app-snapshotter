package com.enonic.app.snapshotter.scheduler;

import com.enonic.app.snapshotter.model.Job;

public interface Scheduler
{
    void schedule( final Job snapshotJob );

    void unschedule();
}
