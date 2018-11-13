package com.enonic.app.snapshotter;

import com.enonic.app.snapshotter.notifier.Notifiers;

public interface SnapshotRunner
{
    Notifiers getNotifiers();
}
