package com.enonic.app.snapshotter.model;

import java.util.Set;

import com.google.common.collect.Sets;

import com.enonic.xp.config.Configuration;

public class SchedulesParser
{
    private static final String SNAPSHOTS_PARENT_KEY = "snapshot";

    private static final String SCHEDULE_ENABLED_KEY = "enabled";

    private static final String SCHEDULE_KEEP_KEY = "keep";

    private static final String SCHEDULE_TRIGGER_KEY = "cron";

    public static Schedules parse( final Configuration config )
    {
        final Configuration snapshots = config.subConfig( SNAPSHOTS_PARENT_KEY );

        final Schedules.Builder builder = Schedules.create();

        Set<String> scheduleNames = Sets.newHashSet();

        snapshots.asMap().keySet().stream().forEach( ( configElement ) -> {
            scheduleNames.add( configElement.substring( 1, configElement.indexOf( ".", 1 ) ) );
        } );

        scheduleNames.stream().
            filter( ( name ) -> Boolean.valueOf( getProperty( snapshots, name, SCHEDULE_ENABLED_KEY ) ) ).
            forEach( ( name ) -> builder.add( Schedule.create().
                keep( getProperty( snapshots, name, SCHEDULE_KEEP_KEY ) ).
                trigger( getProperty( snapshots, name, SCHEDULE_TRIGGER_KEY ) ).
                name( name ).
                build() ) );

        return builder.build();
    }


    private static String getProperty( final Configuration config, final String scheduleName, final String key )
    {
        final String propertyName = "." + scheduleName + "." + key;

        if ( !config.exists( propertyName ) )
        {
            throw new IllegalArgumentException( "Missing property: [" + key + "] for schedule [" + scheduleName + "]" );
        }
        return config.get( propertyName );
    }

}
