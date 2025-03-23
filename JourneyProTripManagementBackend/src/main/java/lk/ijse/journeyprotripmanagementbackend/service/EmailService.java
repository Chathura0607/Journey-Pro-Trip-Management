package lk.ijse.journeyprotripmanagementbackend.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Sends an HTML email using a Thymeleaf template.
     *
     * @param to           The recipient's email address.
     * @param subject      The email subject.
     * @param templateName The name of the Thymeleaf template (e.g., "trip-confirmation.html").
     * @param context      The Thymeleaf context containing dynamic data.
     * @throws MessagingException If the email cannot be sent.
     */
    public void sendHtmlEmailWithTemplate(String to, String subject, String templateName, Context context) throws MessagingException {
        // Process the Thymeleaf template
        String htmlContent = templateEngine.process(templateName, context);

        // Create and send the email
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlContent, true); // Set to true to send HTML
        javaMailSender.send(message);
    }

    /**
     * Sends a simple HTML email without using a template.
     *
     * @param to      The recipient's email address.
     * @param subject The email subject.
     * @param text    The HTML content of the email.
     * @throws MessagingException If the email cannot be sent.
     */
    public void sendHtmlEmail(String to, String subject, String text) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text, true); // Set to true to send HTML
        javaMailSender.send(message);
    }
}