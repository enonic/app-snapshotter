package com.enonic.app.snapshotter.model;

import java.time.Duration;

import com.enonic.app.snapshotter.executor.Executor;

public interface Job
{
    Duration nextExecutionTime();

    Executor executor();

    String description();
}
