package uk.co.setech.easybook.email;

import jakarta.mail.MessagingException;

public interface EmailSender {
    void send(String name, String to, String email, String subject);

    void sendEmailWithAttachment(byte[] invoicePdf, String name, String to) throws MessagingException;
}