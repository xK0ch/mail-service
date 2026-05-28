package de.fynnkoch.mailservice.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private static final String CONTACT_RECIPIENT = "mail@fynn-koch.de";

    private final JavaMailSender mailSender;
    private final String fromAddress;

    public MailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
    }

    public void sendContactMessage(ContactRequest request) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(fromAddress);
        mail.setTo(CONTACT_RECIPIENT);
        mail.setReplyTo(request.email());
        mail.setSubject("Kontaktanfrage von " + request.name());

        StringBuilder body = new StringBuilder();
        body.append("Name: ").append(request.name()).append("\n");
        body.append("E-Mail: ").append(request.email()).append("\n");
        if (request.organisation() != null && !request.organisation().isBlank()) {
            body.append("Organisation: ").append(request.organisation()).append("\n");
        }
        if (request.phone() != null && !request.phone().isBlank()) {
            body.append("Telefon: ").append(request.phone()).append("\n");
        }
        body.append("\nNachricht:\n").append(request.message());

        mail.setText(body.toString());
        mailSender.send(mail);
    }
}
