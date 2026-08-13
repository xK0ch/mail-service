package de.fynnkoch.mailservice.mail;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SendMailRequest(
        @NotBlank(message = "Recipient must not be blank") @Email(message = "Recipient must be a valid email") String to,
        @NotBlank(message = "Subject must not be blank") String subject,
        @NotBlank(message = "Body must not be blank") String body
) {
}
