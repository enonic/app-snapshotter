package com.enonic.app.snapshotter;

import java.util.List;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import com.enonic.xp.config.Configuration;

public abstract class AbstractBaseConfig
{
    public abstract Configuration getConfig();

    protected List<String> doGetCommaSeparated( final String property )
    {
        final List<String> entries = Lists.newArrayList();

        if ( this.getConfig().exists( property ) )
        {
            final String[] split = this.getConfig().get( property ).split( "," );

            for ( final String entry : split )
            {
                if ( !Strings.isNullOrEmpty( entry ) )
                {
                    entries.add( entry );
                }
            }
        }
        return entries;
    }
}
