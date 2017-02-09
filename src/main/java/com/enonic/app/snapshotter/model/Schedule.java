package com.enonic.app.snapshotter.model;

import java.time.Duration;

public class Schedule
{
    private final String name;

    private final CronTrigger trigger;

    private final Duration keep;

    private Schedule( final Builder builder )
    {
        name = builder.name;
        trigger = builder.trigger;
        keep = builder.keep;
    }

    public String getName()
    {
        return name;
    }

    public CronTrigger getTrigger()
    {
        return trigger;
    }

    public Duration getKeep()
    {
        return keep;
    }

    public static Builder create()
    {
        return new Builder();
    }


    public static final class Builder
    {
        private String name;

        private CronTrigger trigger;

        private Duration keep;

        private Builder()
        {
        }

        public Builder name( final String val )
        {
            name = val;
            return this;
        }

        public Builder trigger( final String val )
        {
            trigger = CronTrigger.from( val );
            return this;
        }

        public Builder keep( final String val )
        {
            keep = Duration.parse( val );
            return this;
        }

        public Schedule build()
        {
            return new Schedule( this );
        }
    }
}
