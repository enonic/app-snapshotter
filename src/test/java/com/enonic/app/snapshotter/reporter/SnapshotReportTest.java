package com.enonic.app.snapshotter.reporter;

import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SnapshotReportTest {

    @Test
    public void testSummary()
            throws Exception {
        final SnapshotResultsService resultsService = mock(SnapshotResultsService.class);

        Instant now = Instant.now();
        final SnapshotResults snapshotResults = SnapshotResults.create().
                add(SnapshotResult.create().
                        timestamp(now).
                        state(SnapshotResult.State.SUCCESS).
                        name("jobName").
                        indices(Arrays.asList("indexName1", "indexName2")).
                        build()).
                build();

        when(resultsService.get()).thenReturn(snapshotResults);

        final SnapshotReport snapshotReport = new SnapshotReport(resultsService);

        assertEquals("{" +
                        "\"indices\":[\"indexName1\",\"indexName2\"]," +
                        "\"timestamp\":\"" + now.toString() + "\"," +
                        "\"age\":0," +
                        "\"name\":\"jobName\"," +
                        "\"state\":\"SUCCESS\"" +
                        "}",
                new String(snapshotReport.summary(), StandardCharsets.UTF_8));
    }
}
