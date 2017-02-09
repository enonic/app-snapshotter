package com.enonic.app.snapshotter.model;

import java.time.Duration;
import java.time.Instant;

import com.enonic.app.snapshotter.executor.Executor;
import com.enonic.app.snapshotter.executor.SnapshotExecutor;
import com.enonic.xp.repository.RepositoryId;

public class SnapshotJob
    implements Job
{
    private final RepositoryId repositoryId;

    private final Schedule schedule;

    private final SnapshotExecutor executor;

    public RepositoryId getRepositoryId()
    {
        return repositoryId;
    }

    @Override
    public String description()
    {
        return "Snapshot [" + this.schedule.getName() + "]";
    }

    @Override
    public Executor executor()
    {
        return this.executor;
    }

    private SnapshotJob( final Builder builder )
    {
        this.schedule = builder.schedule;
        this.repositoryId = builder.repositoryId;
        this.executor = builder.executor;
    }

    public static Builder create()
    {
        return new Builder();
    }

    public SnapshotExecutor getExecutor()
    {
        return executor;
    }

    public Duration nextExecutionTime()
    {
        return this.schedule.getTrigger().nextExecution();
    }

    public String name()
    {
        return this.schedule.getName() + "_" + Instant.now().toString().toLowerCase();
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private SnapshotExecutor executor;

        private Schedule schedule;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId repositoryId )
        {
            this.repositoryId = repositoryId;
            return this;
        }

        public Builder schedule( final Schedule schedule )
        {
            this.schedule = schedule;
            return this;
        }

        public Builder executor( final SnapshotExecutor val )
        {
            executor = val;
            return this;
        }

        public SnapshotJob build()
        {
            return new SnapshotJob( this );
        }
    }
}
