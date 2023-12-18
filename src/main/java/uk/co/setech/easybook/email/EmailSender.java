package uk.co.setech.easybook.email;

import jakarta.mail.MessagingException;
import org.springframework.scheduling.annotation.Async;

public interface EmailSender {
    @Async
    void send(String name, String to, String email, String subject);

    @Async
    void sendEmailWithAttachment(byte[] invoicePdf, String name, String to) throws MessagingException;
}