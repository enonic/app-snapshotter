package com.enonic.app.snapshotter.notifier;

import java.util.Map;

import com.enonic.app.snapshotter.model.Job;

public interface Notifier
{
    String name();

    void success( final Job job );

    void failed( final Job job, final Exception e );

    void test( final String message);

    Map<String, Object> getPublicConfig();
}
