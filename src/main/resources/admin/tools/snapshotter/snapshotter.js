var snapshotter = require('/lib/snapshotter');
var portal = require('/lib/xp/portal');
var mustache = require('/lib/mustache');

exports.get = function (req) {

    var view = resolve('snapshotter.html');

    var schedulesResult = snapshotter.schedules();
    var notifiersResult = snapshotter.notifiers();


    schedulesResult.schedules.forEach(function (schedule) {
        var dateStr = schedule.nextExecTime.match(/^[^.]*/g)[0];
        var date = new Date(Date.parse(dateStr));
        schedule.readable = date.toLocaleDateString() + " " + date.toLocaleTimeString();
    });

    var model = {
        jsUrl: portal.assetUrl({path: "/js/main.js"}),
        assetsUrl: portal.assetUrl({path: ""}),
        svcUrl: portal.serviceUrl({service: 'Z'}).slice(0, -1),
        data: {
            schedules: schedulesResult.schedules,
            notifiers: notifiersResult.notifiers
        }
    };

    return {
        contentType: 'text/html',
        body: mustache.render(view, model)
    };

};
