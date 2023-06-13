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

function scheduleJob(params) {
    const isExistingJob = !!libs.scheduler.get({name: params.name});

    runAsAdmin(function () {
        if (isExistingJob) {
            updateScheduledJob(params)
        } else {
            createScheduledJob(params);
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

function createScheduledJob(params) {
    const createParams = {
        name: params.name,
        descriptor: params.taskDescriptor,
        enabled: params.enabled,
        user: 'user:system:su',
        schedule: {
            type: 'CRON',
            value: params.cron,
            timeZone: 'UTC',
        },
    };

    if (!!params.config) {
        createParams.config = params.config;
    }

    libs.scheduler.create(createParams);
}

function updateScheduledJob(params) {
    libs.scheduler.modify({
        name: params.name,
        editor: (edit) => {
            edit.schedule = {
                type: 'CRON',
                value: params.cron,
                timeZone: 'UTC',
            };
            edit.enabled = params.enabled;

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

        scheduleJob({
            name: snapshotJobName,
            taskDescriptor: snapshotTaskDescriptor,
            cron: snapshotConfig.cron,
            enabled: snapshotConfig.enabled,
            config: {
                name: snapshotJobName
            },
        });
    });
}

function scheduleCleanup(appConfig) {
    const cleanupCron = libs.configParser.parseCleanupCron(appConfig);
    const cleanupCronName = makeScheduledSnapshotJobName('cleanup');

    scheduleJob({
        name: cleanupCronName,
        taskDescriptor: cleanupTaskDescriptor,
        cron: cleanupCron,
        enabled: true,
    });
}
