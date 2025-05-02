package com.enonic.app.snapshotter.reporter;

import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.snapshot.SnapshotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.*;

public class CachedSnapshotsResultServiceTest {

    private CachedSnapshotsResultService service;

    private SnapshotService snapshotService;

    @BeforeEach
    public void setup() {
        this.snapshotService = mock(SnapshotService.class);
        this.service = new CachedSnapshotsResultService();
        service.setSnapshotService(snapshotService);
    }

    @Test
    public void name() {
        final SnapshotResults snapshotResults = SnapshotResults.create().
                add(SnapshotResult.create().
                        name("snapshot1").
                        timestamp(Instant.now()).
                        build()).
                build();

        when(this.snapshotService.list()).thenReturn(snapshotResults);

        this.service.setDuration(1);
        this.service.setUnit(TimeUnit.SECONDS);
        this.service.activate();

        this.service.get();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.service.get();

        verify(this.snapshotService, times(2)).list();
    }
}