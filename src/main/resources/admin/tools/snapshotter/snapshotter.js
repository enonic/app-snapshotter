var snapshotter = require('/lib/snapshotter');
var portal = require('/lib/xp/portal');
var mustache = require('/lib/xp/mustache');

exports.get = function (req) {

    var view = resolve('snapshotter.html');

    var result = snapshotter.schedules();

    result.schedules.forEach(function (schedule) {

        var date = new Date(Date.parse(schedule.nextExecTime));

        schedule.readable = date.toLocaleDateString() + " " + date.toLocaleTimeString();

    });
    
    var model = {
        jsUrl: portal.assetUrl({path: "/js/main.js"}),
        assetsUrl: portal.assetUrl({path: ""}),
        data: result
    };

    return {
        contentType: 'text/html',
        body: mustache.render(view, model)
    };

};
