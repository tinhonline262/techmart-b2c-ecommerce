package com.shopping.microservices.notification_service.template;

import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class OrderCompletedMailHandler extends AbstractMailHandler {

    public OrderCompletedMailHandler(JavaMailSender mailSender, SpringTemplateEngine templateEngine, Map<String, Object> variables, String senderMail, String... recipientMails) {
        super(mailSender, templateEngine, senderMail, recipientMails, variables);
    }
    @Override
    public String getMailSubject() {
        return "Your order has been completed!";
    }

    @Override
    public String getTemplate() {
        return "order-completed-mail-template.html";
    }

    @Override
    public Charset getCharset() {
        return StandardCharsets.UTF_8;
    }
}
