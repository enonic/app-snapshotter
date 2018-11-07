package com.enonic.app.snapshotter.slack;

public interface SlackNotifierConfig
{
    String slackWebhook();

    String project();

    boolean reportOnSuccess();

    boolean reportOnFailure();

}
