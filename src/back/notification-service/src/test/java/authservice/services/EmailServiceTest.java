package authservice.services;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private MimeMessage mimeMessage;

    private EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(mailSender, templateEngine);
    }

    @Test
    void sendVerificationEmail_Success() {
        String toEmail = "test@example.com";
        String username = "Test User";
        String userId = "12345";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("verification-email"), any(Context.class)))
                .thenReturn("<html>Email content</html>");

        emailService.sendVerificationEmail(toEmail, username, userId);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendActivationEmail_Success() {
        String toEmail = "test@example.com";
        String username = "Test User";
        String userId = "12345";

        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        when(templateEngine.process(eq("recovery-email"), any(Context.class)))
                .thenReturn("<html>Recovery email content</html>");

        emailService.sendActivationEmail(toEmail, username, userId);

        verify(mailSender).createMimeMessage();
        verify(mailSender).send(mimeMessage);
    }
}