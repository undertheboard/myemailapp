package com.myemailapp.email;

import java.util.Date;

/**
 * Simple email message data structure
 */
public class EmailMessage {
    private String from;
    private String subject;
    private String body;
    private Date date;
    
    public EmailMessage(String from, String subject, String body, Date date) {
        this.from = from;
        this.subject = subject;
        this.body = body;
        this.date = date;
    }
    
    public String getFrom() {
        return from;
    }
    
    public String getSubject() {
        return subject;
    }
    
    public String getBody() {
        return body;
    }
    
    public Date getDate() {
        return date;
    }
    
    @Override
    public String toString() {
        return String.format("From: %s\nSubject: %s\nDate: %s\n\n%s", 
                           from, subject, date, body);
    }
}
