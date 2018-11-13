package com.enonic.app.snapshotter;

import com.enonic.app.snapshotter.mapper.NotifiersMapper;
import com.enonic.app.snapshotter.mapper.SchedulesMapper;
import com.enonic.app.snapshotter.model.Schedules;
import com.enonic.app.snapshotter.notifier.Notifier;
import com.enonic.app.snapshotter.notifier.Notifiers;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.snapshot.SnapshotService;

public class SnapshotterBean
    implements ScriptBean
{
    private SnapshotService snapshotService;

    private SnapshotterConfig snapshotterConfig;

    private SnapshotRunner snapshotRunner;

    @Override
    public void initialize( final BeanContext context )
    {
        this.snapshotService = context.getService( SnapshotService.class ).get();
        this.snapshotterConfig = context.getService( SnapshotterConfig.class ).get();
        this.snapshotRunner = context.getService( SnapshotRunner.class ).get();
    }

    public Object getSchedules()
    {
        final Schedules schedules = this.snapshotterConfig.getSchedules();
        return new SchedulesMapper( schedules );
    }

    public Object getNotifiers()
    {
        final Notifiers notifiers = this.snapshotRunner.getNotifiers();
        return new NotifiersMapper( notifiers );
    }


    public void notify( final String notifierName )
    {
        final Notifiers notifiers = this.snapshotRunner.getNotifiers();
        Notifier notifier = notifiers.get( notifierName );
        if ( notifier != null )
        {
            notifier.test( "testing notifier" );
        }
    }
}


