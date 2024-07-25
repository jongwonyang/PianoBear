package kr.pianobear.application.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import kr.pianobear.application.model.EmailAuth;
import kr.pianobear.application.repository.RedisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    public void sendVerificationEmail(String memberId, String email) throws MessagingException {
        EmailAuth emailAuth = new EmailAuth();
        emailAuth.setUuid(UUID.randomUUID().toString());
        emailAuth.setMemberId(memberId);
        emailAuth.setEmailAddress(email);
        emailAuth.setVerified(false);

        redisRepository.save(emailAuth.getUuid(), emailAuth, 5, TimeUnit.MINUTES);

        MimeMessage message = createVerificationMessage(emailAuth);

        javaMailSender.send(message);
    }

    private MimeMessage createVerificationMessage(EmailAuth emailAuth) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(emailAuth.getEmailAddress());
        helper.setSubject("피아노베어 이메일 인증");
        helper.setFrom(SENDER_EMAIL);

        String verificationUrl = SERVICE_URL + "/api/v1/email-verification/" + emailAuth.getUuid();
        helper.setText(setContext(verificationUrl), true);

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
