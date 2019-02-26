package com.enonic.app.snapshotter.reporter;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.snapshot.SnapshotService;
import com.enonic.xp.status.JsonStatusReporter;
import com.enonic.xp.status.StatusReporter;

@Component(immediate = true, service = StatusReporter.class)
public class SnapshotSummaryReporter
    extends JsonStatusReporter
{
    private SnapshotService snapshotService;

    private SnapshotReport snapshotReport;

    @Activate
    public void activate()
    {
        this.snapshotReport = new SnapshotReport( snapshotService );
    }

    @Override
    public JsonNode getReport()
    {
        try
        {
            return snapshotReport.summary();
        }
        catch ( Exception e )
        {
            throw new RuntimeException( "cannot get snapshot", e.getCause() );
        }
    }

    @Override
    public String getName()
    {
        return "com.enonic.app.snapshotter.summary";
    }

    @SuppressWarnings("unused")
    @Reference
    public void setSnapshotService( final SnapshotService snapshotService )
    {
        this.snapshotService = snapshotService;
    }
}
