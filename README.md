# app-snapshotter

The snapshotter-app enables automatic snapshotting of your Enonic XP repository indexes - see http://xp.readthedocs.io/en/stable/operations/backup.html

Doing a snapshot will store only the changes from the last snapshot, so normally its a very lightweight operation.

The application is cluster-safe; only the master node will trigger snapshots and cleanups.

NOTE: You will still need a way to do backups of the blobs (files) - this is just to automate the snapshots of the indexes.


## Config

This application can be configured in file ``$XP_HOME/config/com.enonic.app.snapshotter.cfg``

### Schedules

As default, three schedules are added; hourly, daily and weekly:

    snapshot.hourly.cron=1 * * * *
    snapshot.hourly.keep=PT48H
    snapshot.hourly.enabled=true

    snapshot.daily.cron=0 1 * * *
    snapshot.daily.keep=P7D
    snapshot.daily.enabled=true

    snapshot.weekly.cron=0 4 * * 0
    snapshot.weekly.keep=P30D
    snapshot.weekly.enabled=true

    cleanup.cron=0 * * * *

  
The ``cron``-property is a string in Unix cron format (http://www.nncron.ru/help/EN/working/cron-format.htm)

The ``keep``-property is given as a java Duration parsable string (https://en.wikipedia.org/wiki/ISO_8601#Durations) - snapshots from the schedule older than this will be automatically deleted

You can add new named schedules if needed:

    snapshot.every-ten-minutes.cron=*/10 * * * *
    snapshot.every-ten-minutes.keep=PT2H
    snapshot.every-ten-minutes.enabled=true

To disable a default schedule, just set enabled to false, e.g ``snapshot.hourly.enabled=false``

### Cleanup

The cleanup schedule is set up to run every hour by default. This will delete snapshots that are outside the keep-range of the schedules. This schedule can be configured by changing the ``cleanup.cron``

### Notifiers

#### Slack

If you want to be notified in a slack channel, this to be configured in file ``com.enonic.app.snapshotter.cfg``

    notifier.slack.slackWebhook = https://hooks.slack.com/services/<SomeSlackHookUrl>
    notifier.slack.project = <MyHost1>
    notifier.slack.reportOnSuccess = false
    notifier.slack.reportOnFailure = true


#### Mail

If your Enonic XP installation is configured for mail (http://xp.readthedocs.io/en/stable/operations/configuration.html#mail-configuration) the Snapshotter app can be configured for sending email when snapshot operations are done. Usually you want to receive a mail if something goes wrong (``notifier.mail.onFailure=true``) but you can also set it up to send a mail when everything is ok (``notifier.mail.onSuccess=true``). The mail notifier is to be configured in file ``com.enonic.app.snapshotter.cfg``

    notifier.mail.onSuccess=false
    notifier.mail.onFailure=true
    notifier.mail.from=some@email.com
    notifier.mail.to=other@email.com,operations@bbc.co.uk

## Monitoring

The snapshotter app adds two endpoints to the statistics-endpoint (https://developer.enonic.com/docs/xp/stable/runtime/statistics):

    "com.enonic.app.snapshotter.latest"
    "com.enonic.app.snapshotter.list"


The first is very useful to ensure that the age of the newest snapshot is not older than expected:

    {
        indices: [
            "search-com.enonic.cms.default",
            "storage-com.enonic.cms.default",
            "search-system-repo",
            "storage-system-repo"
        ],
        timestamp: "2019-11-14T09:01:00.152Z",
        age: 9,
        name: "hourly_2019-11-14t09_01_00.001371z"
    }

This age-value is the age of the last snapshot in minutes, typically used to trigger an alarm if > e.g 80 minutes, depending on your configured frequency

### Migration instructions

- Starting from version 3.0.0 ``app-snapshotter`` is compatible with ``XP`` version 7.3.x and newer
- The configurations of Slack and Mail notifications were moved to config file of application ``$XP_HOME/config/com.enonic.app.snapshotter.cfg``. 
- The configuration files ``$XP_HOME/config/com.enonic.app.snapshotter.slack.cfg`` and ``$XP_HOME/config/com.enonic.app.snapshotter.mail.cfg`` were removed
- The ``notifiers`` configuration property was removed from ``$XP_HOME/config/com.enonic.app.snapshotter.cfg`` file.
- The ``mail.hostname`` configuration property was removed from ``$XP_HOME/config/com.enonic.app.snapshotter.mail.cfg`` file.
- In order to enable ``Slack`` or ``Mail`` notifiers you should use instructions from section ``Notifiers`` of this document.
