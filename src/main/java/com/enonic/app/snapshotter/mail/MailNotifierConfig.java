package com.enonic.app.snapshotter.mail;

import java.util.List;

public interface MailNotifierConfig
{
    Boolean mailOnSuccess();

    Boolean mailOnFailure();

    List<String> from();

    List<String> to();

    boolean mailIsConfigured();

    String hostname();


}
