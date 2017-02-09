package com.enonic.app.snapshotter.mapper;

import java.time.Duration;
import java.time.Instant;

import com.enonic.app.snapshotter.model.Schedule;
import com.enonic.app.snapshotter.model.Schedules;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class SchedulesMapper
    implements MapSerializable
{
    private final Schedules schedules;

    public SchedulesMapper( final Schedules schedules )
    {
        this.schedules = schedules;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.array( "schedules" );
        this.schedules.forEach( ( schedule -> mapSchedule( gen, schedule ) ) );
        gen.end();
    }

    private void mapSchedule( final MapGenerator gen, final Schedule schedule )
    {
        gen.map();
        gen.value( "name", schedule.getName() );
        gen.value( "keep", schedule.getKeep() );
        gen.value( "cron", schedule.getTrigger() );
        final Duration nextExecution = schedule.getTrigger().nextExecution();
        gen.value( "nextExecTime", Instant.now().plus( nextExecution ) );
        gen.end();
    }

}
