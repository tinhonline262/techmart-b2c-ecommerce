package com.shopping.microservices.notification_service.service.impl;

import com.shopping.microservices.common_library.event.NotificationEvent;
import com.shopping.microservices.common_library.event.OrderEvent;
import com.shopping.microservices.common_library.event.PaymentEvent;
import com.shopping.microservices.notification_service.constant.AttributeConstant;
import com.shopping.microservices.notification_service.service.MailService;
import com.shopping.microservices.notification_service.template.*;
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
    public void sendVerifyUserMail(NotificationEvent mailDTO) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(AttributeConstant.NAME_ATTRIBUTE, mailDTO.getData().get("name"));
        variables.put(AttributeConstant.EMAIL_ATTRIBUTE, mailDTO.getData().get("email"));
        variables.put(AttributeConstant.VERIFY_TOKEN_ATTRIBUTE, mailDTO.getData().get("verifyToken"));
        variables.put(AttributeConstant.EXPIRED_DATE_ATTRIBUTE, mailDTO.getData().get("expiredDate"));

        AbstractMailHandler mailHandler = new VerifyUserMailHandler(
                mailSender, templateEngine, variables, senderMail, mailDTO.getData().get("email").toString()
        );
        sendMail(mailHandler);
    }

    @Override
    public void sendCompleteUserMail(NotificationEvent mailDTO) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(AttributeConstant.NAME_ATTRIBUTE, mailDTO.getData().get("name"));
        variables.put(AttributeConstant.EMAIL_ATTRIBUTE, mailDTO.getData().get("email"));
        variables.put(AttributeConstant.USERNAME_ATTRIBUTE, mailDTO.getData().get("username"));
        variables.put(AttributeConstant.CREATED_AT_ATTRIBUTE, mailDTO.getData().get("createdAt"));

        AbstractMailHandler mailHandler = new CompleteUserMailHandler(
                mailSender, templateEngine, variables, senderMail, mailDTO.getData().get("email").toString()
        );
        sendMail(mailHandler);
    }

    @Override
    public void sendOrderPlacedMail(OrderEvent mailDTO) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(AttributeConstant.NAME_ATTRIBUTE, mailDTO.getMetadata().get("customerName") != null ?
                mailDTO.getMetadata().get("customerName") : "Customer");
        variables.put(AttributeConstant.EMAIL_ATTRIBUTE, mailDTO.getEmail());
        variables.put(AttributeConstant.ORDER_NUMBER_ATTRIBUTE, mailDTO.getOrderNumber());
        variables.put(AttributeConstant.TOTAL_AMOUNT_ATTRIBUTE, mailDTO.getTotalAmount());
        variables.put(AttributeConstant.ORDER_DATE_ATTRIBUTE, mailDTO.getTimestamp());

        AbstractMailHandler mailHandler = new OrderPlacedMailHandler(
                mailSender, templateEngine, variables, senderMail, mailDTO.getEmail()
        );
        sendMail(mailHandler);
    }

    @Override
    public void sendOrderCompletedMail(OrderEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(AttributeConstant.NAME_ATTRIBUTE, event.getMetadata().get("customerName") != null ?
                event.getMetadata().get("customerName") : "Customer");
        variables.put(AttributeConstant.EMAIL_ATTRIBUTE, event.getEmail());
        variables.put(AttributeConstant.ORDER_NUMBER_ATTRIBUTE, event.getOrderNumber());
        variables.put(AttributeConstant.TOTAL_AMOUNT_ATTRIBUTE, event.getTotalAmount());
        variables.put(AttributeConstant.COMPLETED_AT_ATTRIBUTE, event.getTimestamp());
        variables.put(AttributeConstant.MESSAGE_ATTRIBUTE, event.getReason());

        AbstractMailHandler mailHandler = new OrderCompletedMailHandler(
                mailSender, templateEngine, variables, senderMail, event.getEmail()
        );
        sendMail(mailHandler);
    }

    @Override
    public void sendOrderCancelledMail(OrderEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(AttributeConstant.NAME_ATTRIBUTE, event.getMetadata().get("customerName") != null ?
                event.getMetadata().get("customerName") : "Customer");
        variables.put(AttributeConstant.EMAIL_ATTRIBUTE, event.getEmail());
        variables.put(AttributeConstant.ORDER_NUMBER_ATTRIBUTE, event.getOrderNumber());
        variables.put(AttributeConstant.TOTAL_AMOUNT_ATTRIBUTE, event.getTotalAmount());
        variables.put(AttributeConstant.CANCELLED_AT_ATTRIBUTE, event.getTimestamp());
        variables.put(AttributeConstant.REASON_ATTRIBUTE, event.getReason());

        AbstractMailHandler mailHandler = new OrderCancelledMailHandler(
                mailSender, templateEngine, variables, senderMail, event.getEmail()
        );
        sendMail(mailHandler);
    }

    @Override
    public void sendPaymentSuccessMail(PaymentEvent event) {
        Map<String, Object> variables = new HashMap<>();
        variables.put(AttributeConstant.NAME_ATTRIBUTE, event.getMetadata()!= null ?
                event.getMetadata().get("customerName") : "Customer");
        variables.put(AttributeConstant.EMAIL_ATTRIBUTE, event.getCustomerEmail());
        variables.put(AttributeConstant.ORDER_NUMBER_ATTRIBUTE, event.getOrderNumber());
        variables.put(AttributeConstant.TOTAL_AMOUNT_ATTRIBUTE, event.getAmount());
        variables.put(AttributeConstant.COMPLETED_AT_ATTRIBUTE, event.getTimestamp());

        AbstractMailHandler mailHandler = new PaymentSuccessMailHandler(
                mailSender, templateEngine, variables, senderMail, event.getCustomerEmail()
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
