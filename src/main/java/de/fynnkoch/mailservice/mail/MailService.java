package de.fynnkoch.mailservice.mail;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    private final JavaMailSender mailSender;
    private final String contactRecipient;
    private final String fromAddress;

    public MailService(
            JavaMailSender mailSender,
            @Value("${mail.contact-recipient}") String contactRecipient,
            @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.contactRecipient = contactRecipient;
        this.fromAddress = fromAddress;
    }

    /**
     * Sends a contact form submission to the configured recipient.
     * The recipient is fixed server-side, so this endpoint cannot be abused as an open relay.
     */
    public void sendContactMessage(ContactRequest request) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(fromAddress);
        mail.setTo(contactRecipient);
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

    /**
     * Sends a generic email to an arbitrary recipient.
     * Guarded by an API key at the controller layer because it can target any address.
     */
    public void sendMail(MailRequest request) {
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(fromAddress);
        mail.setTo(request.to());
        mail.setSubject(request.subject());
        mail.setText(request.body());
        if (request.replyTo() != null && !request.replyTo().isBlank()) {
            mail.setReplyTo(request.replyTo());
        }
        mailSender.send(mail);
    }
}
