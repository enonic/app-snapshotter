const libs = {
    snapshotter: require('/lib/snapshotter'),
    scheduler: require('/lib/xp/scheduler'),
    cluster: require('/lib/xp/cluster'),
    slackNotifier: require('/lib/slack-notifier'),
    mailNotifier: require('/lib/mail-notifier'),
    notifiers: require('/lib/notifiers'),
    configParser: require('/lib/config-parser')
};

exports.run = function (params) {
    const jobName = params.name;
    const text = "Snapshot [" + jobName + "]";

    log.info('Making a snapshot with name: ' + jobName);

    try {
        libs.snapshotter.snapshot({
            scheduleName: jobName
        });

        libs.notifiers.success(text);
    } catch (e) {
        libs.notifiers.failed(text, e.message);
    }
};
