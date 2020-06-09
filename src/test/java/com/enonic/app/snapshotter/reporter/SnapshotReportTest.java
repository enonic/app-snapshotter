package com.enonic.app.snapshotter.reporter;

import java.time.Instant;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;

public class SnapshotReportTest
{

    @Test
    public void testSummary()
    {
        final SnapshotResultsService resultsService = Mockito.mock( SnapshotResultsService.class );

        final SnapshotResults snapshotResults = SnapshotResults.create().
            add( SnapshotResult.create().
                timestamp( Instant.now() ).
                state( SnapshotResult.State.SUCCESS ).
                name( "jobName" ).
                indices( Arrays.asList( "indexName1", "indexName2" ) ).
                build() ).
            build();

        Mockito.when( resultsService.get() ).thenReturn( snapshotResults );

        final SnapshotReport snapshotReport = new SnapshotReport( resultsService );

        ObjectNode objectNode = snapshotReport.summary();

        Assertions.assertEquals( "jobName", objectNode.findValue( "name" ).asText() );
        Assertions.assertEquals( "SUCCESS", objectNode.findValue( "state" ).asText() );
        Assertions.assertEquals( 0, objectNode.findValue( "age" ).asInt() );
        Assertions.assertEquals( 2, ( (ArrayNode) objectNode.findValue( "indices" ) ).size() );
    }

}
