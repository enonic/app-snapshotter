package com.enonic.app.snapshotter.model;

import java.time.Duration;

import com.enonic.app.snapshotter.executor.CleanupExecutor;
import com.enonic.app.snapshotter.executor.Executor;

public class CleanupJob
    implements Job
{
    private final CronTrigger trigger;

    private final CleanupExecutor executor;

    private final Schedules schedules;

    @Override
    public String description()
    {
        return "Cleanup";
    }

    @Override
    public Executor executor()
    {
        return this.executor;
    }

    private CleanupJob( final Builder builder )
    {
        trigger = builder.trigger;
        executor = builder.executor;
        schedules = builder.schedules;
    }

    public Schedules getSchedules()
    {
        return schedules;
    }

    @Override
    public Duration nextExecutionTime()
    {
        return trigger.nextExecution();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private CronTrigger trigger;

        private CleanupExecutor executor;

        private Schedules schedules;

        private Builder()
        {
        }

        public Builder trigger( final String val )
        {
            trigger = CronTrigger.from( val );
            return this;
        }

        public Builder executor( final CleanupExecutor val )
        {
            executor = val;
            return this;
        }

        public Builder schedules( final Schedules val )
        {
            schedules = val;
            return this;
        }

        public CleanupJob build()
        {
            return new CleanupJob( this );
        }

        public Builder trigger( final CronTrigger val )
        {
            trigger = val;
            return this;
        }
    }
}
