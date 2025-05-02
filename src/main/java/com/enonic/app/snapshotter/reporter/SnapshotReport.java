package com.enonic.app.snapshotter.reporter;

import com.enonic.xp.node.SnapshotResult;
import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONComposer;
import com.fasterxml.jackson.jr.ob.comp.ArrayComposer;
import com.fasterxml.jackson.jr.ob.comp.ObjectComposer;
import com.google.common.collect.Iterables;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

class SnapshotReport {
    private final SnapshotResultsService resultsService;

    SnapshotReport(final SnapshotResultsService resultsService) {
        this.resultsService = resultsService;
    }

    byte[] list() throws IOException {
        final Instant now = Instant.now();
        var snapshots = JSON.std.composeBytes().startObject().startArrayField("snapshots");
        for (SnapshotResult result : resultsService.get()) {
            ObjectComposer<ArrayComposer<ObjectComposer<JSONComposer<byte[]>>>> composer = snapshots.startObject();
            var indices = composer.startArrayField("indices");
            for (String index : result.getIndices()) {
                indices.add(index);
            }
            indices.end()
                    .put("timestamp", result.getTimestamp().toString())
                    .put("age", Duration.between(result.getTimestamp(), now).toMinutes())
                    .put("name", result.getName())
                    .put("state", result.getState().name())
                    .end();
        }
        return snapshots.end().end().finish();
    }

    byte[] summary() throws IOException {
        final Instant now = Instant.now();
        var composer = JSON.std.composeBytes().startObject();
        SnapshotResult result = Iterables.getLast(resultsService.get());
        var indices = composer.startArrayField("indices");
        for (String index : result.getIndices()) {
            indices.add(index);
        }
        indices.end()
                .put("timestamp", result.getTimestamp().toString())
                .put("age", Duration.between(result.getTimestamp(), now).toMinutes())
                .put("name", result.getName())
                .put("state", result.getState().name())
                .end();
        return composer.end().finish();
    }
}
