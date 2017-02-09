package com.enonic.app.snapshotter.mail;

import java.util.Collection;
import java.util.List;

import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Preconditions;

import com.enonic.xp.mail.MailException;
import com.enonic.xp.mail.MailMessage;

import static com.google.common.base.Strings.nullToEmpty;

public class SnapshotMail
    implements MailMessage
{
    private final String subject;

    private final List<String> from;

    private final List<String> to;

    private final String body;

    private SnapshotMail( final Builder builder )
    {
        subject = builder.subject;
        from = builder.from;
        to = builder.to;
        body = builder.body;
    }

    @Override
    public void compose( final MimeMessage message )
        throws Exception
    {
        message.setSubject( this.subject );
        message.addFrom( toAddresses( this.from ) );
        message.addRecipients( Message.RecipientType.TO, toAddresses( this.to ) );

        message.setText( nullToEmpty( this.body ), "UTF-8" );
    }

    private InternetAddress[] toAddresses( final Collection<String> addressList )
        throws Exception
    {
        return addressList.stream().filter( StringUtils::isNotBlank ).map( ( this::toAddress ) ).toArray( InternetAddress[]::new );
    }

    private InternetAddress toAddress( final String address )
        throws MailException
    {
        try
        {
            return new InternetAddress( address );
        }
        catch ( AddressException e )
        {
            throw new MailException( e.getMessage(), e );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private String subject;

        private List<String> from;

        private List<String> to;

        private String body;

        private Builder()
        {
        }

        public Builder subject( final String val )
        {
            subject = val;
            return this;
        }

        public Builder from( final List<String> val )
        {
            from = val;
            return this;
        }

        public Builder to( final List<String> val )
        {
            to = val;
            return this;
        }

        public Builder body( final String val )
        {
            body = val;
            return this;
        }

        private void validate()
        {
            Preconditions.checkArgument( this.from != null && !this.from.isEmpty(), "Missing from-address" );
            Preconditions.checkArgument( this.to != null && !this.to.isEmpty(), "Missing to-address" );
        }

        public SnapshotMail build()
        {
            validate();
            return new SnapshotMail( this );
        }
    }
}
