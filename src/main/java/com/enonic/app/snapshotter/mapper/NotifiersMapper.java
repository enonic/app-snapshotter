package com.enonic.app.snapshotter.mapper;

import java.util.Map;

import com.enonic.app.snapshotter.notifier.Notifier;
import com.enonic.app.snapshotter.notifier.Notifiers;
import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;

public class NotifiersMapper
    implements MapSerializable
{

    private final Notifiers notifiers;

    public NotifiersMapper( final Notifiers notifiers )
    {
        this.notifiers = notifiers;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.array( "notifiers" );
        this.notifiers.forEach( notifier -> {
            serialize( gen, notifier );
        } );
        gen.end();
    }

    private void serialize( final MapGenerator gen, final Notifier notifier )
    {
        gen.map();
        gen.value( "name", notifier.name() );

        serialize( gen, notifier.getPublicConfig() );
        gen.end();

    }

    private void serialize( final MapGenerator gen, final Map<String, Object> config )
    {
        gen.array( "config" );
        config.forEach( ( k, v ) -> {
            gen.map();
            gen.value( "key", k );
            gen.value( "value", v );
            gen.end();
        } );
        gen.end();
    }
}
