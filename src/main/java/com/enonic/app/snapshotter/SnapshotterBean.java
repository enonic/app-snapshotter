package com.enonic.app.snapshotter;

import com.enonic.app.snapshotter.mapper.SchedulesMapper;
import com.enonic.app.snapshotter.model.Schedules;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;

public class SnapshotterBean
    implements ScriptBean
{
    private NodeService nodeService;

    private SnapshotterConfig snapshotterConfig;

    @Override
    public void initialize( final BeanContext context )
    {
        this.nodeService = context.getService( NodeService.class ).get();
        this.snapshotterConfig = context.getService( SnapshotterConfig.class ).get();
    }

    public Object getSchedules()
    {
        final Schedules schedules = this.snapshotterConfig.getSchedules();
        return new SchedulesMapper( schedules );
    }

}
