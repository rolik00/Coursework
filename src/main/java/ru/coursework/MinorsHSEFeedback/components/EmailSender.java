package ru.coursework.MinorsHSEFeedback.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Component
@Slf4j
public class EmailSender {

    @Value("${email.host}")
    private String host;

    @Value("${email.username}")
    private String user;

    @Value("${email.password}")
    private String password;

    @Value("${email.port}")
    private String port;

    @Value("${email.smtp.ssl.enable}")
    private String sslEnable;

    @Value("${email.smtp.auth}")
    private String auth;

    public void sendEmail(String to, String subject, String body) {
        Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", port);
        properties.setProperty("mail.smtp.ssl.enable", sslEnable);
        properties.setProperty("mail.smtp.auth", auth);

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

            log.info("Email sent successfully!");

        } catch (MessagingException e) {
            log.error("Email sent error {}", e.getMessage());
            e.printStackTrace();
        }
    }
}

