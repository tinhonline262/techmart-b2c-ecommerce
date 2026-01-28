package com.shopping.microservices.notification_service.template;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Getter
@Setter
@Slf4j
public class PaymentSuccessMailHandler extends AbstractMailHandler {

    public PaymentSuccessMailHandler(JavaMailSender mailSender, SpringTemplateEngine templateEngine, Map<String, Object> variables, String senderMail, String... recipientMails) {
        super(mailSender, templateEngine, senderMail, recipientMails, variables);
    }

    @Override
    public String getMailSubject() {
        return "Payment Successfully";
    }

    @Override
    public String getTemplate() {
        return "payment-success-mail-template.html";
    }

    @Override
    public Charset getCharset() {
        return StandardCharsets.UTF_8;
    }
}
