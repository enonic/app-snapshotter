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
    snapshot.hourly.keep=PT24H
    snapshot.hourly.enabled=true

    snapshot.daily.cron=0 1 * * *
    snapshot.daily.keep=P7D
    snapshot.daily.enabled=true

    snapshot.weekly.cron=0 4 * * 0
    snapshot.weekly.keep=P30D
    snapshot.weekly.enabled=true

    cleanup.cron=0 * * * *
    
    notifiers=

  
The ``cron``-property is a string in Unix cron format (http://www.nncron.ru/help/EN/working/cron-format.htm)

The ``keep``-property is given as a java Duration parsable string (https://en.wikipedia.org/wiki/ISO_8601#Durations) - snapshots from the schedule older than this will be automatically deleted

You can add new, named schedules if needed:

    snapshot.every-ten-minutes.cron=0/10 * * * *
    snapshot.every-ten-minutes.keep=PT2H
    snapshot.every-ten-minutes.enabled=true

To disable a default schedule, just set enabled to false, e.g ``snapshot.hourly.enabled=false``

### Cleanup

The cleanup schedule is set up to run every hour by default. This will delete snapshots that are outside the keep-range of the schedules. This schedule can be configured by changing the ``cleanup.cron``

### Notifiers

Provides a comma-separated list of notifiers, e.g ``notifiers=slack,mail``

#### Slack

If you want to be notified in a slack channel, this be be configured in file ``com.enonic.app.snapshotter.slack.cfg``

    slackWebhook = https://hooks.slack.com/services/<SomeSlackHookUrl>
    project = <MyHost1>
    reportOnSuccess = false
    reportOnFailure = true


#### Mail

If you Enonic XP installation is configured for mail (http://xp.readthedocs.io/en/stable/operations/configuration.html#mail-configuration) the Snapshotter app can be configured for sending email when snapshot operations are done. Usually you want to receive a mail if something goes wrong (``mail.onFailure=true``) but you can also set it up to send a mail when everything is ok (``mail.onSuccess=true``). The mail notifier is to be configured in file ``com.enonic.app.snapshotter.mail``

    mailOnSuccess=false
    mailOnFailure=true
    from=some@email.com
    to=other@email.com,operations@bbc.co.uk
    hostname=MyHost

## Monitoring

The snapshotter app adds two endpoints to the monitor api:

    "com.enonic.app.snapshotter.latest"
    "com.enonic.app.snapshotter.list"


Use this to ensure that the age of the newest snapshot is not older than expected
