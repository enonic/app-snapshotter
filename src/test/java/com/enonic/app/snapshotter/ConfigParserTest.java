package com.enonic.app.snapshotter;

import org.junit.jupiter.api.Test;

import com.enonic.xp.testing.ScriptTestSupport;

public class ConfigParserTest
    extends ScriptTestSupport
{

    @Test
    public void testParseSnapshots()
    {
        runFunction( "test/config-parser-test.js", "testParseSnapshots" );
    }

    @Test
    public void testParseNotifiers()
    {
        runFunction( "test/config-parser-test.js", "testParseNotifiers" );
    }

    @Test
    public void testParseCleanupCron()
    {
        runFunction( "test/config-parser-test.js", "testParseCleanupCron" );
    }

}
