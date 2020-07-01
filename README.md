# app-snapshotter

**app-snapshotter** enables automatic snapshotting of your Enonic XP repository indexes - see https://xp.readthedocs.io/en/stable/operations/backup.html#backup

Doing a snapshot will store only the changes from the last snapshot, so normally it's a very lightweight operation.

The application is cluster-safe; only the master node will trigger snapshots and cleanups.

NOTE: You will still need a way to do backups of the blobs (files) - this is just to automate the snapshots of the indexes.


## Config

This application is shipped with a default schedule (see **Schedules** section below).
If you want to override this schedule, this can be done with config file ``$XP_HOME/config/com.enonic.app.snapshotter.cfg``.  
[Sample config file](https://github.com/enonic/app-snapshotter/blob/master/com.enonic.app.snapshotter.cfg) is shipped with the application - 
just copy it over into `config` folder of your Enonic XP instance and adjust the settings (note that email and Slack notifications are
commented out by default).

### Schedules

Three schedules (hourly, daily and weekly) are configured by default: 

    snapshot.hourly.cron=1 * * * *
    snapshot.hourly.keep=PT48H
    snapshot.hourly.enabled=true

    snapshot.daily.cron=0 1 * * *
    snapshot.daily.keep=P7D
    snapshot.daily.enabled=true

    snapshot.weekly.cron=0 4 * * 1
    snapshot.weekly.keep=P30D
    snapshot.weekly.enabled=true

    cleanup.cron=0 * * * *

  
The ``cron``-property is a string in Unix cron format (http://www.nncron.ru/help/EN/working/cron-format.htm)

The ``keep``-property is given as a java Duration parsable string (https://en.wikipedia.org/wiki/ISO_8601#Durations) - snapshots from the schedule older than this will be automatically deleted

You can add new named schedules if needed:

    snapshot.every-ten-minutes.cron=*/10 * * * *
    snapshot.every-ten-minutes.keep=PT2H
    snapshot.every-ten-minutes.enabled=true

To disable a default schedule, just set `enabled` to `false`, e.g ``snapshot.hourly.enabled=false``

### Cleanup

The cleanup schedule is configured to run every hour by default. This will delete snapshots that are outside the keep-range of the schedules. This schedule can be changed via ``cleanup.cron`` setting

### Notifiers

#### Slack

If you want to be notified in a slack channel, add the following to your config file ``com.enonic.app.snapshotter.cfg``

    notifier.slack.slackWebhook = https://hooks.slack.com/services/<SomeSlackHookUrl>
    notifier.slack.project = <MyHost1>
    notifier.slack.reportOnSuccess = false
    notifier.slack.reportOnFailure = true


#### Mail

If your Enonic XP installation is set up to send mail (https://developer.enonic.com/docs/xp/stable/deployment/config#mail),
the Snapshotter app can be configured to notify about snapshot operations by email. 
Usually you want to be notified if something went wrong (``notifier.mail.onFailure=true``) but you can also set it up to send a mail
for successful operations (``notifier.mail.onSuccess=true``). The mail notifier can be configured in ``com.enonic.app.snapshotter.cfg``

    notifier.mail.onSuccess=false
    notifier.mail.onFailure=true
    notifier.mail.from=some@email.com
    notifier.mail.to=other@email.com,operations@bbc.co.uk

## Monitoring

The Snapshotter app adds two endpoints to the statistics-endpoint (https://developer.enonic.com/docs/xp/stable/runtime/statistics):

    "com.enonic.app.snapshotter.latest"
    "com.enonic.app.snapshotter.list"


The first one is very useful to ensure that the newest snapshot is not older than expected:

    {
        indices: [
            "search-com.enonic.cms.default",
            "storage-com.enonic.cms.default",
            "search-system-repo",
            "storage-system-repo"
        ],
        timestamp: "2019-11-14T09:01:00.152Z",
        age: 9,
        name: "hourly_2019-11-14t09_01_00.001371z",
        state: "SUCCESS"
    }

Value of the `age` is the max age of the last snapshot in minutes, typically used to trigger an alarm if > e.g 80 minutes, depending on your configured frequency

## Upgrade notes

- Starting from version 3.0.0 ``app-snapshotter`` requires ``XP`` version 7.3.x and newer
- The configurations of Slack and Mail notifications were moved to the main application config file ``$XP_HOME/config/com.enonic.app.snapshotter.cfg``. 
- The configuration files ``$XP_HOME/config/com.enonic.app.snapshotter.slack.cfg`` and ``$XP_HOME/config/com.enonic.app.snapshotter.mail.cfg`` were removed
- The ``notifiers`` configuration property was removed from ``$XP_HOME/config/com.enonic.app.snapshotter.cfg`` file.
- The ``mail.hostname`` configuration property was removed from ``$XP_HOME/config/com.enonic.app.snapshotter.mail.cfg`` file.
- In order to enable ``Slack`` or ``Mail`` notifiers you should use instructions from section ``Notifiers`` of this document.
