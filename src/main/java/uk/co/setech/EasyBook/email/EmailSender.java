package uk.co.setech.EasyBook.email;

public interface EmailSender {
    void send(String name, String to, String email, String subject);
}