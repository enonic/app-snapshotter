package com.enonic.app.snapshotter.model;

public enum SnapshotType
{
    DAILY( "com.enonic.app.snapshotter-daily" ),
    INTERMEDIATE( "com.enonic.app.snapshotter-intermediate" );

    private String preFix;

    SnapshotType( final String preFix )
    {
        this.preFix = preFix;
    }

    public String getPreFix()
    {
        return preFix;
    }
}
