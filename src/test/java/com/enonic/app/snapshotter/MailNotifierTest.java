package com.enonic.app.snapshotter;

import org.junit.jupiter.api.Test;

import com.enonic.xp.testing.ScriptTestSupport;

public class MailNotifierTest
    extends ScriptTestSupport
{

    @Test
    public void testNotify()
    {
        runFunction( "test/mail-notifier-test.js", "testNotify" );
    }

    @Test
    public void testSuccess()
    {
        runFunction( "test/mail-notifier-test.js", "testSuccess" );
    }

    @Test
    public void testFailed()
    {
        runFunction( "test/mail-notifier-test.js", "testFailed" );
    }

}
