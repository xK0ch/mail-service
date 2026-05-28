package de.fynnkoch.mailservice.mail;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MailController.class)
@TestPropertySource(properties = "mail.api-key=test-secret")
class MailControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MailService mailService;

    @Test
    void contactEndpointIsPublicAndSendsMessage() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Max","email":"max@example.com","message":"Hallo"}
                                """))
                .andExpect(status().isOk());

        verify(mailService).sendContactMessage(any(ContactRequest.class));
    }

    @Test
    void contactEndpointRejectsInvalidEmail() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Max","email":"not-an-email","message":"Hallo"}
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void genericMailRequiresApiKey() throws Exception {
        mockMvc.perform(post("/api/mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"to":"a@example.com","subject":"Hi","body":"Text"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void genericMailSucceedsWithValidApiKey() throws Exception {
        mockMvc.perform(post("/api/mail")
                        .header("X-Api-Key", "test-secret")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"to":"a@example.com","subject":"Hi","body":"Text"}
                                """))
                .andExpect(status().isOk());

        verify(mailService).sendMail(any(MailRequest.class));
    }
}
