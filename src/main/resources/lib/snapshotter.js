var bean = __.newBean('com.enonic.app.snapshotter.SnapshotterBean');


exports.schedules = function () {
    var result = bean.getSchedules();
    return __.toNativeObject(result);
};

