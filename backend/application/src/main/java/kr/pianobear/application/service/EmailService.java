package kr.pianobear.application.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.pianobear.application.model.EmailAuth;
import kr.pianobear.application.util.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class EmailService {

    private final String SERVICE_URL;
    private final String SENDER_EMAIL;
    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    @Autowired
    public EmailService(
            @Value("${application.service-url}") String serviceUrl,
            @Value("${spring.mail.username}") String senderEmail,
            JavaMailSender javaMailSender,
            RedisUtil redisUtil) {
        this.SERVICE_URL = serviceUrl;
        SENDER_EMAIL = senderEmail;
        this.javaMailSender = javaMailSender;
        this.redisUtil = redisUtil;
    }

    public void sendVerificationEmail(String memberId, String email) throws MessagingException {
        EmailAuth emailAuth = new EmailAuth();
        emailAuth.setUuid(UUID.randomUUID().toString());
        emailAuth.setMemberId(memberId);
        emailAuth.setEmailAddress(email);
        emailAuth.setVerified(false);

        MimeMessage message = createVerificationMessage(memberId, email);

        javaMailSender.send(message);
    }

    private MimeMessage createVerificationMessage(String memberId, String to) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(MimeMessage.RecipientType.TO, to);
        message.setSubject("피아노베어 이메일 인증");
        message.setFrom(SENDER_EMAIL);

        String verificationUUID = UUID.randomUUID().toString();
        String verificationUrl = SERVICE_URL + "/api/v1/email-verification/" + verificationUUID;
        message.setText(setContext(verificationUrl), "utf-8", "html");

        EmailAuth emailAuth = new EmailAuth();
        emailAuth.setUuid(verificationUUID);
        emailAuth.setEmailAddress(to);
        emailAuth.setMemberId(memberId);
        emailAuth.setVerified(false);

        redisUtil.saveWithExpiration(verificationUUID, emailAuth, 5, TimeUnit.MINUTES);

        return message;
    }

    private String setContext(String verificationUrl) {
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
}
