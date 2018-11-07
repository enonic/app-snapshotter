package com.enonic.app.snapshotter.slack;

import com.google.common.collect.ImmutableSet;
import com.webcerebrium.slack.SlackMessage;
import com.webcerebrium.slack.SlackMessageAttachment;

import com.enonic.app.snapshotter.model.Job;

public class SlackMessageBuilder
{
    private final String project;

    private final Job job;

    private final Boolean failed;

    private SlackMessageBuilder( final Builder builder )
    {
        this.project = builder.project;
        this.job = builder.job;
        this.failed = builder.failed;
    }

    private enum Color
    {
        SUCCESS( "#7ec982" ), FAILED( "#d1252e" );

        private final String code;

        Color( final String code )
        {
            this.code = code;
        }
    }

    public SlackMessage execute()
    {
        final SlackMessage slackMessage = new SlackMessage();
        final SlackMessageAttachment attach = new SlackMessageAttachment( this.project, createTitle(), getColor() );

        attach.addMarkdown( ImmutableSet.of( "title", "text" ) );
        slackMessage.getAttachments().add( attach );

        return slackMessage;
    }

    public static Builder create()
    {
        return new Builder();
    }


    private String getColor()
    {
        return this.failed ? Color.FAILED.code : Color.SUCCESS.code;
    }

    private String createTitle()
    {
        return this.failed ? "Snapshot [" + job.description() + "[ failed" : "Snapshot [" + job.description() + "] success";
    }

    public final static class Builder
    {
        private String project;

        private boolean failed = false;

        private Job job;

        public Builder()
        {
        }

        public Builder project( final String project )
        {
            this.project = project;
            return this;
        }


        public Builder failed( final boolean failed )
        {
            this.failed = failed;
            return this;
        }

        public Builder job( final Job job )
        {
            this.job = job;
            return this;
        }

        public SlackMessageBuilder build()
        {
            return new SlackMessageBuilder( this );
        }
    }

}
