var libs = {
    snapshotter: require('/lib/snapshotter'),
    cron: require('/lib/cron'),
    cluster: require('/lib/xp/cluster'),
    slackNotifier: require('/lib/slack-notifier'),
    mailNotifier: require('/lib/mail-notifier'),
    notifiers: require('/lib/notifiers'),
    configParser: require('/lib/config-parser')
};

var appConfig = libs.snapshotter.getConfig();

libs.configParser.parseNotifiers(appConfig).forEach(function (notifier) {
    if (notifier.name === 'slack') {
        libs.notifiers.register(libs.slackNotifier.newInstance(notifier.config));
    } else if (notifier.name === 'mail') {
        libs.notifiers.register(libs.mailNotifier.newInstance(notifier.config));
    } else {
        // do nothing
    }
});

var schedulesResult = libs.configParser.parseSnapshots(appConfig);

schedulesResult.forEach(function (schedule) {
    libs.cron.reschedule({
        name: schedule.name,
        cron: schedule.cron,
        callback: function () {
            var text = "Snapshot [" + schedule.name + "]";

            try {
                if (libs.cluster.isMaster()) {
                    libs.snapshotter.snapshot({
                        scheduleName: schedule.name
                    });

                    libs.notifiers.success(text);
                }
            } catch (e) {
                libs.notifiers.failed(text, e.message);
            }
        }
    });
});


libs.cron.schedule({
    name: 'cleanupCron',
    cron: libs.configParser.parseCleanupCron(appConfig),
    callback: function () {
        var text = 'Cleanup';
        try {
            schedulesResult.forEach(function (schedule) {
                if (libs.cluster.isMaster()) {
                    libs.snapshotter.deleteSnapshot({
                        scheduleName: schedule.name,
                        keep: schedule.keep
                    });
                }
            });

            libs.notifiers.success(text);
        } catch (e) {
            libs.notifiers.failed(text, e.message);
        }
    }
});
