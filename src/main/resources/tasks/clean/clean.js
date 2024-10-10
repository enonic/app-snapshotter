const libs = {
    snapshotter: require('/lib/snapshotter'),
    notifiers: require('/lib/notifiers'),
    configParser: require('/lib/config-parser'),
    snapshotJobScheduler: require('/lib/snapshot-job-scheduler'),
};

exports.run = function (params) {
    const appConfig = libs.snapshotter.getConfig();
    const scheduledSnapshotsJobs = libs.configParser.parseSnapshots(appConfig);
    const text = 'Cleanup';

    log.info('Running snapshots cleanup');

    try {
        scheduledSnapshotsJobs.forEach(function (schedule) {
            cleanScheduledJob(schedule);
        });

        libs.notifiers.success(text);
    } catch (e) {
        libs.notifiers.failed(text, e.message);
    }
};

function cleanScheduledJob(schedule) {
    const jobName = libs.snapshotJobScheduler.makeScheduledSnapshotJobName(schedule.name);

    libs.snapshotter.deleteSnapshot({
        scheduleName: jobName,
        appPrefix: libs.snapshotJobScheduler.appPrefix,
        keep: schedule.keep
    });
}