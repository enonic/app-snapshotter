package com.enonic.app.snapshotter.handler;

import com.enonic.xp.node.*;
import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.testing.ScriptTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


public class SnapshotterHandlerTest
        extends ScriptTestSupport {

    private SnapshotService snapshotService;

    @BeforeEach
    public void initialize()
            throws Exception {
        super.initialize();

        this.snapshotService = mock(SnapshotService.class);
        addService(SnapshotService.class, this.snapshotService);
    }

    @Test
    public void testSnapshot() {
        final SnapshotResult snapshotResult = SnapshotResult.create().state(SnapshotResult.State.SUCCESS).build();

        when(snapshotService.snapshot(any(SnapshotParams.class))).thenReturn(snapshotResult);

        runFunction("test/SnapshotterHandlerTest.js", "snapshot");

        verify(snapshotService, times(1)).snapshot(any(SnapshotParams.class));
    }

    @Test
    public void testDeleteSnapshot() {
        final String snapshotName = "com.enonic.app.snapshotter--name_2020-01-01T00-00-00.000z";
        final String snapshotNameOldFormat = "name_2020-01-01T00-00-00.000z";
        final String snapshotWithOtherName = "com.enonic.app.snapshotter--nameother_2020-01-01T00-00-00.000z";
        final SnapshotResults snapshots = SnapshotResults.create()
                .add(SnapshotResult.create().name(snapshotName).timestamp(Instant.now().minus(2, ChronoUnit.HOURS)).build())
                .add(SnapshotResult.create().name(snapshotNameOldFormat).timestamp(Instant.now().minus(2, ChronoUnit.HOURS)).build())
                .add(SnapshotResult.create().name(snapshotWithOtherName).timestamp(Instant.now().minus(2, ChronoUnit.HOURS)).build())
                .build();

        final DeleteSnapshotsResult deleteSnapshotsResult = DeleteSnapshotsResult.create().add(snapshotName).add(snapshotNameOldFormat).build();

        when(snapshotService.list()).thenReturn(snapshots);
        when(snapshotService.delete(any(DeleteSnapshotParams.class))).thenReturn(deleteSnapshotsResult);

        runFunction("test/SnapshotterHandlerTest.js", "deleteSnapshot");

        final ArgumentCaptor<DeleteSnapshotParams> captor = ArgumentCaptor.forClass(DeleteSnapshotParams.class);

        verify(snapshotService, times(1)).list();
        verify(snapshotService, times(1)).delete(captor.capture());
        assertEquals(2, captor.getValue().getSnapshotNames().size());
    }

    @Test
    public void testConfig() {
        runFunction("test/SnapshotterHandlerTest.js", "getConfig");
    }

    @Test
    public void testGetDefaultHost() {
        final SnapshotterHandler instance = new SnapshotterHandler();

        assertNotNull(instance.getDefaultHost());
    }

    @Test
    public void testDeleteSnapshotMultipleSchedules() {
        // Test that different schedule types (hourly, daily, weekly) are properly filtered
        final String hourlySnapshot = "com.enonic.app.snapshotter--hourly_2020-01-01T00-00-00.000z";
        final String dailySnapshot = "com.enonic.app.snapshotter--daily_2020-01-01T00-00-00.000z";
        final String weeklySnapshot = "com.enonic.app.snapshotter--weekly_2020-01-01T00-00-00.000z";
        final String hourlySnapshotOldFormat = "hourly_2020-01-01T00-00-00.000z";
        final String dailySnapshotOldFormat = "daily_2020-01-01T00-00-00.000z";
        
        final SnapshotResults snapshots = SnapshotResults.create()
                .add(SnapshotResult.create().name(hourlySnapshot).timestamp(Instant.now().minus(2, ChronoUnit.HOURS)).build())
                .add(SnapshotResult.create().name(dailySnapshot).timestamp(Instant.now().minus(2, ChronoUnit.HOURS)).build())
                .add(SnapshotResult.create().name(weeklySnapshot).timestamp(Instant.now().minus(2, ChronoUnit.HOURS)).build())
                .add(SnapshotResult.create().name(hourlySnapshotOldFormat).timestamp(Instant.now().minus(2, ChronoUnit.HOURS)).build())
                .add(SnapshotResult.create().name(dailySnapshotOldFormat).timestamp(Instant.now().minus(2, ChronoUnit.HOURS)).build())
                .build();

        final DeleteSnapshotsResult deleteSnapshotsResult = DeleteSnapshotsResult.create()
                .add(hourlySnapshot)
                .add(hourlySnapshotOldFormat)
                .build();

        when(snapshotService.list()).thenReturn(snapshots);
        when(snapshotService.delete(any(DeleteSnapshotParams.class))).thenReturn(deleteSnapshotsResult);

        runFunction("test/SnapshotterHandlerTest.js", "deleteSnapshotHourly");

        final ArgumentCaptor<DeleteSnapshotParams> captor = ArgumentCaptor.forClass(DeleteSnapshotParams.class);

        verify(snapshotService, times(1)).list();
        verify(snapshotService, times(1)).delete(captor.capture());
        // Only hourly snapshots should be deleted, not daily or weekly
        assertEquals(2, captor.getValue().getSnapshotNames().size());
    }
}
