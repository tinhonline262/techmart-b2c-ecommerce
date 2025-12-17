package com.shopping.microservices.notification_service.service.impl;

import com.shopping.microservices.notification_service.constant.AttributeConstant;
import com.shopping.microservices.notification_service.event.CompleteUserMailEvent;
import com.shopping.microservices.notification_service.event.OrderSendNotificationEvent;
import com.shopping.microservices.notification_service.event.VerifyUserMailEvent;
import com.shopping.microservices.notification_service.service.MailService;
import com.shopping.microservices.notification_service.template.AbstractMailHandler;
import com.shopping.microservices.notification_service.template.CompleteUserMailHandler;
import com.shopping.microservices.notification_service.template.OrderPlacedMailHandler;
import com.shopping.microservices.notification_service.template.VerifyUserMailHandler;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final String senderMail;

    MailServiceImpl(JavaMailSender mailSender, SpringTemplateEngine templateEngine,
                    @Value("${mail.sender.from}") String senderMail) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.senderMail = senderMail;
    }

    @Override
    public void sendVerifyUserMail(VerifyUserMailEvent mailDTO) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(AttributeConstant.NAME_ATTRIBUTE, mailDTO.name());
        variables.put(AttributeConstant.EMAIL_ATTRIBUTE, mailDTO.email());
        variables.put(AttributeConstant.VERIFY_TOKEN_ATTRIBUTE, mailDTO.verifyToken());
        variables.put(AttributeConstant.EXPIRED_DATE_ATTRIBUTE, mailDTO.expiredDate());

        AbstractMailHandler mailHandler = new VerifyUserMailHandler(
                mailSender, templateEngine, variables, senderMail, mailDTO.email()
        );
        sendMail(mailHandler);
    }

    @Override
    public void sendCompleteUserMail(CompleteUserMailEvent mailDTO) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(AttributeConstant.NAME_ATTRIBUTE, mailDTO.name());
        variables.put(AttributeConstant.EMAIL_ATTRIBUTE, mailDTO.email());
        variables.put(AttributeConstant.USERNAME_ATTRIBUTE, mailDTO.username());
        variables.put(AttributeConstant.CREATED_AT_ATTRIBUTE, mailDTO.createdAt());

        AbstractMailHandler mailHandler = new CompleteUserMailHandler(
                mailSender, templateEngine, variables, senderMail, mailDTO.email()
        );
        sendMail(mailHandler);
    }

    @Override
    public void sendOrderPlacedMail(OrderSendNotificationEvent mailDTO) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(AttributeConstant.NAME_ATTRIBUTE, mailDTO.customerName());
        variables.put(AttributeConstant.EMAIL_ATTRIBUTE, mailDTO.customerEmail());
        variables.put(AttributeConstant.ORDER_NUMBER_ATTRIBUTE, mailDTO.orderNumber());
        variables.put(AttributeConstant.TOTAL_AMOUNT_ATTRIBUTE, mailDTO.totalAmount());
        variables.put(AttributeConstant.ORDER_DATE_ATTRIBUTE, mailDTO.orderDate());

        AbstractMailHandler mailHandler = new OrderPlacedMailHandler(
                mailSender, templateEngine, variables, senderMail, mailDTO.customerEmail()
        );
        sendMail(mailHandler);
    }


    private void sendMail(AbstractMailHandler mailHandler) {
        try {
            mailHandler.send();
        } catch (MessagingException e) {
            log.error(e.toString(), e);
        }
    }
}
