package com.javacodepoint.orderservice.service;

import com.javacodepoint.orderservice.event.OrderPlacedEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.resilience.annotation.Retryable;   // Spring Boot 4 native resilience
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${app.company.name:TechStore Inc.}")
    private String companyName;

    /**
     * Sends order confirmation email with retry support (Spring Boot 4 native @Retryable)
     */
    @Retryable(
            includes = {MessagingException.class, jakarta.mail.MessagingException.class},
            maxRetries = 3,
            delayString = "2000ms", // Defines the initial delay inline
            multiplier = 2.0        // Multiplier applied directly to the delay
    )
    public void sendOrderConfirmation(OrderPlacedEvent event) throws MessagingException {
        try {
            log.info("Preparing and sending order confirmation email for order: {} to {}",
                    event.getOrderNumber(), event.getCustomerEmail());

            Context context = new Context();
            context.setVariable("customerName", event.getCustomerName());
            context.setVariable("orderNumber", event.getOrderNumber());
            context.setVariable("orderDate", java.time.LocalDateTime.now());
            context.setVariable("companyName", companyName);
            context.setVariable("items", event.getItems());
            context.setVariable("totalAmount", event.getTotalAmount());

            String htmlContent = templateEngine.process("order-confirmation", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(event.getCustomerEmail());
            helper.setSubject("Your order " + event.getOrderNumber() + " has been received!");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

            log.info("Order confirmation email sent successfully to {}", event.getCustomerEmail());

        } catch (MessagingException e) {
            log.error("Failed to send email for order: {}. Will be retried...", event.getOrderNumber(), e);
            throw e; // Important: rethrow so @Retryable can catch it
        }
    }
}