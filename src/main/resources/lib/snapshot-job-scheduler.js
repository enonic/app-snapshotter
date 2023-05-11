const libs = {
    scheduler: require('/lib/xp/scheduler'),
    context: require('/lib/xp/context'),
    cluster: require('/lib/xp/cluster'),
    configParser: require('/lib/config-parser'),
};

const appName = 'com.enonic.app.snapshotter';
const scheduledSnapshotJobPrefix = appName + '--'
const snapshotTaskDescriptor = appName + ':snapshot';
const cleanupTaskDescriptor = appName + ':clean';

exports.list = function () {
    return libs.scheduler.list();
}

exports.makeScheduledSnapshotJobName = makeScheduledSnapshotJobName;

exports.scheduleSnapshotsJobs = scheduleSnapshotsJobs;


function makeScheduledSnapshotJobName(name) {
    return scheduledSnapshotJobPrefix + name;
}

function scheduleJob(name, cron, taskDescriptor, config) {
    const isExistingJob = !!libs.scheduler.get({name});

    runAsAdmin(function () {
        if (isExistingJob) {
            updateScheduledJob(name, cron)
        } else {
            createScheduledJob(name, cron, taskDescriptor, config);
        }
    });
}

function runAsAdmin(callback) {
    libs.context.run({
        repository: 'system.scheduler',
        branch: 'master',
        principals: ['role:system.admin'],
    }, callback);
}

function createScheduledJob(name, cron, taskDescriptor, config) {
    const createParams = {
        name: name,
        descriptor: taskDescriptor,
        enabled: true,
        user: 'user:system:su',
        schedule: {
            type: 'CRON',
            value: cron,
            timeZone: 'UTC',
        },
    };

    if (!!config) {
        createParams.config = config;
    }

    libs.scheduler.create(createParams);
}

function updateScheduledJob(name, cron) {
    libs.scheduler.modify({
        name: name,
        editor: (edit) => {
            edit.schedule = {
                type: 'CRON',
                value: cron,
                timeZone: 'UTC',
            };

            return edit;
        }
    });
}

function scheduleSnapshotsJobs(appConfig) {
    if (libs.cluster.isMaster()) {
        scheduleSnapshots(appConfig);
        scheduleCleanup(appConfig);
    }
}

function scheduleSnapshots(appConfig) {
    const snapshotsToSchedule = libs.configParser.parseSnapshots(appConfig);

    snapshotsToSchedule.forEach(function (snapshotConfig) {
        const snapshotJobName = makeScheduledSnapshotJobName(snapshotConfig.name);
        scheduleJob(snapshotJobName, snapshotConfig.cron, snapshotTaskDescriptor, {name: snapshotJobName});
    });
}

function scheduleCleanup(appConfig) {
    const cleanupCron = libs.configParser.parseCleanupCron(appConfig);
    const cleanupCronName = makeScheduledSnapshotJobName('cleanup');

    scheduleJob(cleanupCronName, cleanupCron, cleanupTaskDescriptor);
}
