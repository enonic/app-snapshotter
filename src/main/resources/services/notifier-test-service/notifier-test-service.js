var notifier = require('/lib/snapshotter.js');


exports.get = function (req) {

    var requestParams = req.params;

    notifier.testNotify(requestParams.notifierName);

    return {
        contentType: 'application/json',
        body: {
            "status": "ok"
        }
    }
};
