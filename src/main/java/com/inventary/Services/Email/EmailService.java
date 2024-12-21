package com.inventary.Services.Email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

@Service
public class EmailService {
    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // Send verification email
    public void sendVerificationEmail(String to, String verificationCode, String lastname) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("Bienvenido al nuevo Fondo Colectivo");

            String htmlContent = "<html>"
                    + "<body style=font-family: Arial, sans-serif;'>"
                    + "<div style='padding: 20px; text-align: center;'>"
                    + "<h1 style='color: #000000; font-weight: 800;'>¡Bienvenido/a, " + lastname + "!</h1>"
                    + "<p style='color: #000000;'>Nos complace darte la bienvenida al sistema de <strong style='color: #000000;'>'INVENTARIO'</strong>.</p>"
                    + "<p style='color: #000000;'>Como nuevo usuario, estamos seguros de que tu contribución será valiosa para alcanzar nuestros objetivos comunes.</p>"
                    + "<p style='color: #000000;'>Tu código de verificación es: <strong style='color: #000000;'>" + verificationCode + "</strong></p>"
                    + "<p style='color: #000000;'>Por favor, verifica tu dirección de correo electrónico para completar tu registro.</p>"
                    + "<p style='color: #000000;'>Si tienes alguna pregunta, no dudes en contactarnos.</p>"
                    + "<p style='color: #000000;'>Saludos cordiales,</p>"
                    + "</div>"
                    + "</body>"
                    + "</html>";

            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
