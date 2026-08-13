package de.fynnkoch.mailservice.mail;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MailController.class, properties = "mail.api-key=test-key")
class SendMailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MailService mailService;

    private static final String BODY = """
            {"to":"user@example.com","subject":"Hi","body":"Hello there"}
            """;

    @Test
    void sendRejectsRequestWithoutApiKey() throws Exception {
        mockMvc.perform(post("/api/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isUnauthorized());

        verify(mailService, never()).sendMail(any(SendMailRequest.class));
    }

    @Test
    void sendRejectsRequestWithWrongApiKey() throws Exception {
        mockMvc.perform(post("/api/send")
                        .header("X-Api-Key", "wrong")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isUnauthorized());

        verify(mailService, never()).sendMail(any(SendMailRequest.class));
    }

    @Test
    void sendAcceptsRequestWithValidApiKey() throws Exception {
        mockMvc.perform(post("/api/send")
                        .header("X-Api-Key", "test-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(BODY))
                .andExpect(status().isOk());

        verify(mailService).sendMail(any(SendMailRequest.class));
    }

    @Test
    void sendRejectsInvalidRecipient() throws Exception {
        mockMvc.perform(post("/api/send")
                        .header("X-Api-Key", "test-key")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"to":"not-an-email","subject":"Hi","body":"Hello"}
                                """))
                .andExpect(status().isBadRequest());

        verify(mailService, never()).sendMail(any(SendMailRequest.class));
    }
}
