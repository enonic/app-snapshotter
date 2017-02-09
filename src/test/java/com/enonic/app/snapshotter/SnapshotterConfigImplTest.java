package com.enonic.app.snapshotter;

import org.junit.Test;

import com.google.common.collect.Maps;

public class SnapshotterConfigImplTest
{

    @Test
    public void name()
        throws Exception
    {
        final SnapshotterConfigImpl config = new SnapshotterConfigImpl();

        config.activate( Maps.newHashMap() );

        config.getSchedules();

    }

}