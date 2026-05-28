package de.fynnkoch.mailservice.mail;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Mail", description = "Email sending")
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/contact")
    @Operation(operationId = "sendContactMessage", summary = "Send contact message",
            description = "Sends a contact form message to the configured recipient (public, fixed recipient).")
    @SecurityRequirements
    public ResponseEntity<Void> sendContactMessage(@Valid @RequestBody ContactRequest request) {
        mailService.sendContactMessage(request);
        return ResponseEntity.ok().build();
    }
}
