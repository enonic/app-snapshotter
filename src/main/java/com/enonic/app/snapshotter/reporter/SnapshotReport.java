package com.enonic.app.snapshotter.reporter;

import java.time.Duration;
import java.time.Instant;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.Iterables;

import com.enonic.xp.node.SnapshotResult;

class SnapshotReport
{
    private final SnapshotResultsService resultsService;

    SnapshotReport( final SnapshotResultsService resultsService )
    {
        this.resultsService = resultsService;
    }

    ObjectNode list()
    {
        final Instant now = Instant.now();
        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        final ArrayNode snapshots = JsonNodeFactory.instance.arrayNode();
        resultsService.get().forEach( result -> snapshots.add( generateResultJson( now, result ) ) );
        json.set( "snapshots", snapshots );
        return json;
    }

    ObjectNode summary()
    {
        final Instant now = Instant.now();
        return generateResultJson( now, Iterables.getLast( resultsService.get() ) );
    }

    private ObjectNode generateResultJson( final Instant now, final SnapshotResult result )
    {
        final ObjectNode snapshotNode = JsonNodeFactory.instance.objectNode();
        final ArrayNode indices = JsonNodeFactory.instance.arrayNode();
        result.getIndices().forEach( indices::add );
        snapshotNode.set( "indices", indices );
        snapshotNode.put( "timestamp", result.getTimestamp().toString() );
        snapshotNode.put( "age", Duration.between( result.getTimestamp(), now ).toMinutes() );
        snapshotNode.put( "name", result.getName() );
        snapshotNode.put( "state", result.getState().name() );
        return snapshotNode;
    }

}
