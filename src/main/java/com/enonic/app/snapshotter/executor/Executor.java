package com.enonic.app.snapshotter.executor;

import com.enonic.app.snapshotter.model.Job;

public interface Executor<T extends Job>
{
    void execute( final T job );
}
