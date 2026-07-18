package com.ramosabogada.service;

import com.ramosabogada.dto.ContactRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    private EmailServiceImpl emailService;

    private static final String RECIPIENT = "sramoslegal95@gmail.com";
    private static final String SENDER = "onboarding@resend.dev";
    private static final String API_KEY = "test-api-key";

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl(restTemplate, RECIPIENT, SENDER, API_KEY);
    }

    @Test
    @SuppressWarnings("unchecked")
    void sendContactEmail_sendsMailWithCorrectRecipient() {
        when(restTemplate.postForEntity(eq("https://api.resend.com/emails"), any(HttpEntity.class), eq(String.class)))
            .thenReturn(ResponseEntity.ok("{}"));

        var request = new ContactRequest("Ana García", "ana@mail.com", "Necesito asesoramiento");

        emailService.sendContactEmail(request);

        ArgumentCaptor<HttpEntity<Map<String, Object>>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(eq("https://api.resend.com/emails"), captor.capture(), eq(String.class));

        Map<String, Object> body = captor.getValue().getBody();
        assertThat(body.get("to")).isEqualTo(RECIPIENT);
        assertThat(body.get("from")).isEqualTo(SENDER);
        assertThat((String) body.get("subject")).contains("ramosabogada.github.io");
        assertThat((String) body.get("text")).contains("Ana García");
        assertThat((String) body.get("text")).contains("ana@mail.com");
        assertThat((String) body.get("text")).contains("Necesito asesoramiento");
    }
}
