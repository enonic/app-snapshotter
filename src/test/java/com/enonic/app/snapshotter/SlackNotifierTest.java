package com.enonic.app.snapshotter;

import org.junit.jupiter.api.Test;

import com.enonic.xp.testing.ScriptTestSupport;

public class SlackNotifierTest
    extends ScriptTestSupport
{

    @Test
    public void testNotify()
    {
        runFunction( "test/slack-notifier-test.js", "testNotify" );
    }

    @Test
    public void testSuccess()
    {
        runFunction( "test/slack-notifier-test.js", "testSuccess" );
    }

    @Test
    public void testFailed()
    {
        runFunction( "test/slack-notifier-test.js", "testFailed" );
    }

}
