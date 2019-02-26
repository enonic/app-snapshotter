package com.enonic.app.snapshotter.reporter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public class SnapshotListReporter
    extends JsonStatusReporter
{
    private SnapshotResultsService resultsService;

    @Override
    public JsonNode getReport()
    {
        try
        {
            return new SnapshotReport( resultsService ).list();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "cannot get snapshot", e.getCause() );
        }
    }

    @Override
    public String getName()
    {
        return "com.enonic.app.snapshotter.list";
    }

    @SuppressWarnings("unused")
    @Reference
    public void setSnapshotResultsService( final SnapshotResultsService resultsService )
    {
        this.resultsService = resultsService;
    }
}
