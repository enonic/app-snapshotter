var bean = __.newBean('com.enonic.app.snapshotter.SnapshotterBean');


exports.schedules = function () {
    var result = bean.getSchedules();
    return __.toNativeObject(result);
};


exports.notifiers = function () {
    var result = bean.getNotifiers();
    return __.toNativeObject(result);
};

exports.testNotify = function (name) {
    bean.notify(name);
};