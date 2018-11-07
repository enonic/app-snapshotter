package com.enonic.app.snapshotter.notifier;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import com.enonic.app.snapshotter.model.Job;

public class Notifiers
    implements Iterable<Notifier>
{
    private final List<Notifier> notifiers = Lists.newArrayList();

    public void success( final Job job )
    {
        this.notifiers.forEach( n -> n.success( job ) );
    }

    public void failed( final Job job, final Exception e )
    {
        this.notifiers.forEach( n -> n.failed( job, e ) );
    }

    public boolean hasAll( final List<String> needed )
    {
        return notifiers.stream().map( Notifier::name ).collect( Collectors.toList() ).containsAll( needed );
    }

    @Override
    public Iterator<Notifier> iterator()
    {
        return notifiers.iterator();
    }

    public void add( final Notifier notifier )
    {
        this.notifiers.add( notifier );
    }

    public boolean remove( final Notifier notifier )
    {
        return this.notifiers.remove( notifier );
    }


}
