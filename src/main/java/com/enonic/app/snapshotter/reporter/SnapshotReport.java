package com.enonic.app.snapshotter.reporter;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;

import com.enonic.xp.node.SnapshotResult;
import com.enonic.xp.node.SnapshotResults;
import com.enonic.xp.snapshot.SnapshotService;

public class SnapshotReport
{
    private SnapshotService snapshotService;

    private final Cache<String, SnapshotResults> snapshotCache =
        CacheBuilder.newBuilder().maximumSize( 1 ).expireAfterAccess( 2, TimeUnit.MINUTES ).build();

    public SnapshotReport( final SnapshotService snapshotResults )
    {
        this.snapshotService = snapshotResults;
    }

    ObjectNode list()
    {
        final Instant now = Instant.now();

        final ObjectNode json = JsonNodeFactory.instance.objectNode();
        final ArrayNode snapshots = JsonNodeFactory.instance.arrayNode();

        SnapshotResults results = getResults();

        results.forEach( result -> snapshots.add( generateResultJson( now, result ) ) );

        json.set( "snapshots", snapshots );
        return json;
    }

    ObjectNode summary()
    {
        final Instant now = Instant.now();

        final ObjectNode json = JsonNodeFactory.instance.objectNode();

        SnapshotResults results = getResults();
        json.set( "newest", generateResultJson( now, Iterables.getLast( results ) ) );

        return json;
    }

    private SnapshotResults getResults()
    {
        try
        {
            return snapshotCache.get( "results", () -> snapshotService.list() );
        }
        catch ( ExecutionException e )
        {
            throw new RuntimeException( "cannot get snapshots", e );
        }
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
        return snapshotNode;
    }

}
