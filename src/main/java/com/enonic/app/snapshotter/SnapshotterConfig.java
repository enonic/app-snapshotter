package com.enonic.app.snapshotter;

import java.util.List;

import com.enonic.app.snapshotter.model.Schedules;

public interface SnapshotterConfig
{
    Schedules getSchedules();

    String cleanCron();

    Boolean mailOnSuccess();

    Boolean mailOnFailure();

    List<String> from();

    List<String> to();

    String hostname();
}
