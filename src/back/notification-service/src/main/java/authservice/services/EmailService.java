package authservice.services;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Servicio para el envío de emails de verificación y recuperación de cuenta
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    
    @Value("${app.frontend.fqdn}")
    private String frontend;

    /**
     * Envía email de verificación para activación de cuenta nueva o sin verificar
     */
    public void sendVerificationEmail(String toEmail, String username, String userId) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Bienvenido a ComuniKIT - Activa tu cuenta");
            helper.setFrom("noreply.comunikit@gmail.com");

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("activationId", userId);
            context.setVariable("frontendUrl", frontend);

            String htmlContent = templateEngine.process("verification-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            log.info("Email de verificación enviado exitosamente a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error enviando email de verificación a: {}", toEmail, e);
        }
    }

    /**
     * Envía email de recuperación de cuenta para restablecer contraseña
     */
    public void sendActivationEmail(String toEmail, String username, String userId) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("ComuniKIT - Recupera tu cuenta");
            helper.setFrom("noreply.comunikit@gmail.com");

            Context context = new Context();
            context.setVariable("username", username);
            context.setVariable("userId", userId);
            context.setVariable("frontendUrl", frontend);

            String htmlContent = templateEngine.process("recovery-email", context);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            log.info("Email de recuperación enviado exitosamente a: {}", toEmail);

        } catch (Exception e) {
            log.error("Error enviando email de recuperación a: {}", toEmail, e);
        }
    }
}