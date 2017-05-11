package com.enonic.app.snapshotter;

import com.enonic.app.snapshotter.mapper.SchedulesMapper;
import com.enonic.app.snapshotter.model.Schedules;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.snapshot.SnapshotService;

public class SnapshotterBean
    implements ScriptBean
{
    private SnapshotService snapshotService;

    private SnapshotterConfig snapshotterConfig;

    @Override
    public void initialize( final BeanContext context )
    {
        this.snapshotService = context.getService( SnapshotService.class ).get();
        this.snapshotterConfig = context.getService( SnapshotterConfig.class ).get();
    }

    public Object getSchedules()
    {
        final Schedules schedules = this.snapshotterConfig.getSchedules();
        return new SchedulesMapper( schedules );
    }

}
