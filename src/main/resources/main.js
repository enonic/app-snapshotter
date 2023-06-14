const libs = {
    snapshotter: require('/lib/snapshotter'),
    snapshotJobScheduler: require('/lib/snapshot-job-scheduler'),
    notifiers: require('/lib/notifiers'),
};

const appConfig = libs.snapshotter.getConfig();

libs.notifiers.initNotifiers(appConfig);
libs.snapshotJobScheduler.scheduleSnapshotsJobs(appConfig);

__.disposer(function() {
    libs.snapshotJobScheduler.disableScheduledJobs(appConfig);
});
