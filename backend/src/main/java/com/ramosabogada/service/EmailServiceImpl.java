package com.ramosabogada.service;

import com.ramosabogada.dto.ContactRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    private final RestTemplate restTemplate;
    private final String recipientEmail;
    private final String senderEmail;
    private final String resendApiKey;

    public EmailServiceImpl(
        RestTemplate restTemplate,
        @Value("${mail.recipient}") String recipientEmail,
        @Value("${resend.from-email}") String senderEmail,
        @Value("${resend.api-key}") String resendApiKey
    ) {
        this.restTemplate = restTemplate;
        this.recipientEmail = recipientEmail;
        this.senderEmail = senderEmail;
        this.resendApiKey = resendApiKey;
    }

    @Override
    public void sendContactEmail(ContactRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(resendApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
            "from", senderEmail,
            "to", recipientEmail,
            "subject", "Consulta desde ramosabogada.github.io",
            "text", buildEmailBody(request)
        );

        restTemplate.postForEntity(RESEND_API_URL, new HttpEntity<>(body, headers), String.class);
    }

    private String buildEmailBody(ContactRequest request) {
        return String.format("""
            Nombre: %s
            Email: %s

            Mensaje:
            %s
            """, request.name(), request.email(), request.message());
    }
}
