var snapshotter = require('/lib/snapshotter');
var portal = require('/lib/xp/portal');
var mustache = require('/lib/xp/mustache');

exports.get = function (req) {

    var view = resolve('snapshotter.html');

    var schedules = snapshotter.schedules();

    log.info("Schedules: %s", JSON.stringify(schedules, null, 2));

    var model = {
        jsUrl: portal.assetUrl({path: "/js/main.js"}),
        assetsUrl: portal.assetUrl({path: ""}),
        data: schedules
    };

    return {
        contentType: 'text/html',
        body: mustache.render(view, model)
    };

};
