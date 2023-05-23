package uk.co.setech.easybook.email;

public interface EmailSender {
    void send(String name, String to, String email, String subject);
}