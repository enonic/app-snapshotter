package com.enonic.app.snapshotter.model;

import com.enonic.xp.node.SnapshotParams;

public class SnapshotParamsFactory
{
    public static SnapshotParams create( final SnapshotJob snapshotJob )
    {
        return SnapshotParams.create().
            snapshotName( snapshotJob.name() ).
            repositoryId( snapshotJob.getRepositoryId() ).
            build();
    }

}
