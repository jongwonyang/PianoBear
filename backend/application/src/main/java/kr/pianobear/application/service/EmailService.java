package kr.pianobear.application.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.pianobear.application.model.EmailAuth;
import kr.pianobear.application.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {

    private final String SERVICE_URL;
    private final String SENDER_EMAIL;
    private final JavaMailSender javaMailSender;
    private final RedisRepository redisRepository;

    @Autowired
    public EmailService(
            @Value("${application.service-url}") String serviceUrl,
            @Value("${spring.mail.username}") String senderEmail,
            JavaMailSender javaMailSender,
            RedisRepository redisRepository) {
        this.SERVICE_URL = serviceUrl;
        SENDER_EMAIL = senderEmail;
        this.javaMailSender = javaMailSender;
        this.redisRepository = redisRepository;
    }

    public void sendVerificationEmail(String memberId, String email) throws MessagingException, UnsupportedEncodingException {
        EmailAuth emailAuth = new EmailAuth();
        emailAuth.setUuid(UUID.randomUUID().toString());
        emailAuth.setMemberId(memberId);
        emailAuth.setEmailAddress(email);
        emailAuth.setVerified(false);

        redisRepository.save(emailAuth.getUuid(), emailAuth, 5, TimeUnit.MINUTES);

        MimeMessage message = createVerificationMessage(emailAuth);

        javaMailSender.send(message);
    }

    public void sendPasswordResetEmail(String email, String newPassword) throws MessagingException {
        MimeMessage message = createPasswordResetMessage(email, newPassword);

        javaMailSender.send(message);
    }

    private MimeMessage createVerificationMessage(EmailAuth emailAuth) throws MessagingException, UnsupportedEncodingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setRecipients(Message.RecipientType.TO, emailAuth.getEmailAddress());
        message.setSubject("피아노베어 이메일 인증");
        message.setFrom(SENDER_EMAIL);

        String verificationUrl = SERVICE_URL + "/api/v1/auth/email-verification/" + emailAuth.getUuid();
        message.setText(setVerificationContext(verificationUrl), "utf-8", "html");

        return message;
    }

    private MimeMessage createPasswordResetMessage(String email, String newPassword) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.setRecipients(Message.RecipientType.TO, email);
        message.setSubject("피아노배어 비밀번호 초기화");
        message.setFrom(SENDER_EMAIL);

        message.setText(setPasswordResetContext(newPassword), "utf-8", "html");

        return message;
    }

    private String setVerificationContext(String verificationUrl) {
        Context context = new Context();
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        context.setVariable("verificationUrl", verificationUrl);

        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);

        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine.process("auth-mail", context);
    }

    private String setPasswordResetContext(String newPassword) {
        Context context = new Context();
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        context.setVariable("newPassword", newPassword);

        templateResolver.setPrefix("templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCacheable(false);

        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine.process("password-reset-mail", context);
    }
}
