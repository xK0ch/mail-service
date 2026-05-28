package de.fynnkoch.mailservice.mail;

import de.fynnkoch.mailservice.config.OpenApiConfig;
import de.fynnkoch.mailservice.exception.UnauthorizedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Mail", description = "Email sending")
public class MailController {

    private final MailService mailService;
    private final String apiKey;

    public MailController(
            MailService mailService,
            @Value("${mail.api-key:}") String apiKey) {
        this.mailService = mailService;
        this.apiKey = apiKey;
    }

    @PostMapping("/contact")
    @Operation(operationId = "sendContactMessage", summary = "Send contact message",
            description = "Sends a contact form message to the configured recipient (public, fixed recipient).")
    @SecurityRequirements
    public ResponseEntity<Void> sendContactMessage(@Valid @RequestBody ContactRequest request) {
        mailService.sendContactMessage(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/mail")
    @Operation(operationId = "sendMail", summary = "Send a generic email",
            description = "Sends an email to an arbitrary recipient. Requires the X-Api-Key header.")
    @SecurityRequirement(name = OpenApiConfig.API_KEY_SCHEME)
    public ResponseEntity<Void> sendMail(
            @RequestHeader(value = "X-Api-Key", required = false) String providedKey,
            @Valid @RequestBody MailRequest request) {
        requireApiKey(providedKey);
        mailService.sendMail(request);
        return ResponseEntity.ok().build();
    }

    private void requireApiKey(String providedKey) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new UnauthorizedException("Generic mail endpoint is disabled: no API key configured.");
        }
        if (providedKey == null || !apiKey.equals(providedKey)) {
            throw new UnauthorizedException("Invalid or missing API key.");
        }
    }
}
