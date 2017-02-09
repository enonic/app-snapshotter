package com.enonic.app.snapshotter.model;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Lists;

public class Schedules
    implements Iterable<Schedule>
{
    private final List<Schedule> schedules;

    private Schedules( final Builder builder )
    {
        schedules = builder.schedules;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public Iterator<Schedule> iterator()
    {
        return this.schedules.iterator();
    }

    public static final class Builder
    {
        private List<Schedule> schedules = Lists.newArrayList();

        private Builder()
        {
        }

        public Builder add( final Schedule val )
        {
            schedules.add( val );
            return this;
        }

        public Schedules build()
        {
            return new Schedules( this );
        }
    }
}
