package com.enonic.app.snapshotter.reporter;

import com.enonic.xp.status.StatusReporter;
import com.google.common.net.MediaType;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.io.OutputStream;

@Component(immediate = true, service = StatusReporter.class)
public class SnapshotSummaryReporter
        implements StatusReporter {
    private SnapshotResultsService resultsService;

    @Override
    public MediaType getMediaType() {
        return MediaType.JSON_UTF_8;
    }

    @Override
    public void report(final OutputStream outputStream)
            throws IOException {
        outputStream.write(new SnapshotReport(resultsService).summary());
    }

    @Override
    public String getName() {
        return "com.enonic.app.snapshotter.latest";
    }

    @SuppressWarnings("unused")
    @Reference
    public void setSnapshotResultsService(final SnapshotResultsService resultsService) {
        this.resultsService = resultsService;
    }
}
