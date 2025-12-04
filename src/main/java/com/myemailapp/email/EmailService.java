package com.myemailapp.email;

import com.myemailapp.config.AppConfig;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.*;

/**
 * Email service for connecting to Optimum email accounts
 * Handles IMAP and SMTP operations
 */
public class EmailService {
    private AppConfig config;
    private Store store;
    private Session session;
    private boolean isConnected = false;
    
    public EmailService(AppConfig config) {
        this.config = config;
    }
    
    /**
     * Connect to Optimum email server using IMAP
     */
    public boolean connect() throws MessagingException {
        // If already connected, just return true
        if (isConnected && store != null && store.isConnected()) {
            return true;
        }
        
        Properties props = new Properties();
        props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", config.getImapServer());
        props.put("mail.imaps.port", config.getImapPort());
        props.put("mail.imaps.ssl.enable", "true");
        props.put("mail.imaps.ssl.trust", "*");
        
        // SMTP properties
        props.put("mail.smtp.host", config.getSmtpServer());
        props.put("mail.smtp.port", config.getSmtpPort());
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.trust", "*");
        
        session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(
                    config.getEmailAddress(), 
                    config.getEmailPassword()
                );
            }
        });
        
        store = session.getStore("imaps");
        store.connect(
            config.getImapServer(),
            config.getEmailAddress(),
            config.getEmailPassword()
        );
        
        isConnected = store.isConnected();
        return isConnected;
    }
    
    /**
     * Check if currently connected
     */
    public boolean isConnected() {
        return isConnected && store != null && store.isConnected();
    }
    
    /**
     * Reconnect if connection is lost
     */
    public void ensureConnected() throws MessagingException {
        if (!isConnected()) {
            connect();
        }
    }
    
    /**
     * Disconnect from email server
     */
    public void disconnect() {
        try {
            if (store != null && store.isConnected()) {
                store.close();
            }
            isConnected = false;
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Get inbox folder
     */
    public Folder getInbox() throws MessagingException {
        ensureConnected();
        Folder inbox = store.getFolder("INBOX");
        inbox.open(Folder.READ_ONLY);
        return inbox;
    }
    
    /**
     * Fetch recent emails
     */
    public List<EmailMessage> fetchRecentEmails(int count) throws MessagingException {
        List<EmailMessage> emails = new ArrayList<>();
        Folder inbox = getInbox();
        
        int messageCount = inbox.getMessageCount();
        int start = Math.max(1, messageCount - count + 1);
        
        Message[] messages = inbox.getMessages(start, messageCount);
        
        for (int i = messages.length - 1; i >= 0; i--) {
            Message msg = messages[i];
            emails.add(new EmailMessage(
                msg.getFrom()[0].toString(),
                msg.getSubject(),
                getTextContent(msg),
                msg.getSentDate()
            ));
        }
        
        inbox.close(false);
        return emails;
    }
    
    /**
     * Extract text content from message
     */
    private String getTextContent(Message msg) throws MessagingException {
        try {
            Object content = msg.getContent();
            if (content instanceof String) {
                return (String) content;
            } else if (content instanceof Multipart) {
                Multipart multipart = (Multipart) content;
                for (int i = 0; i < multipart.getCount(); i++) {
                    BodyPart bodyPart = multipart.getBodyPart(i);
                    if (bodyPart.isMimeType("text/plain")) {
                        return (String) bodyPart.getContent();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "[Unable to read content]";
    }
    
    /**
     * Send an email
     */
    public void sendEmail(String to, String subject, String body) throws MessagingException {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(config.getEmailAddress()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(body);
        
        Transport.send(message);
    }
}
