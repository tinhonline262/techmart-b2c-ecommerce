package com.shopping.microservices.order_service.service.impl;

import com.shopping.microservices.common_library.exception.ResourceNotFoundException;
import com.shopping.microservices.order_service.dto.checkout.*;
import com.shopping.microservices.order_service.entity.Checkout;
import com.shopping.microservices.order_service.entity.CheckoutItem;
import com.shopping.microservices.order_service.mapper.CheckoutMapper;
import com.shopping.microservices.order_service.repository.CheckoutRepository;
import com.shopping.microservices.order_service.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CheckoutRepository checkoutRepository;
    private final CheckoutMapper checkoutMapper;

    @Override
    @Transactional
    public CheckoutResponse createCheckout(CreateCheckoutRequest request) {
        log.info("Creating checkout for customer: {}", request.customerId());
        
        Checkout checkout = Checkout.builder()
                .customerId(request.customerId())
                .email(request.email())
                .note(request.note())
                .promotionCode(request.promotionCode())
                .status("PENDING")
                .progress("INITIATED")
                .shipmentMethodId(request.shipmentMethodId())
                .paymentMethodId(request.paymentMethodId())
                .shippingAddressId(request.shippingAddressId())
                .totalAmount(BigDecimal.ZERO)
                .totalShipmentFee(BigDecimal.ZERO)
                .totalShipmentTax(BigDecimal.ZERO)
                .totalTax(BigDecimal.ZERO)
                .totalDiscountAmount(BigDecimal.ZERO)
                .build();

        // Add items
        if (request.items() != null) {
            request.items().forEach(itemRequest -> {
                CheckoutItem item = CheckoutItem.builder()
                        .productId(itemRequest.productId())
                        .name(itemRequest.name())
                        .description(itemRequest.description())
                        .quantity(itemRequest.quantity())
                        .price(itemRequest.price())
                        .tax(itemRequest.tax() != null ? itemRequest.tax() : BigDecimal.ZERO)
                        .shipmentFee(itemRequest.shipmentFee() != null ? itemRequest.shipmentFee() : BigDecimal.ZERO)
                        .shipmentTax(itemRequest.shipmentTax() != null ? itemRequest.shipmentTax() : BigDecimal.ZERO)
                        .discountAmount(itemRequest.discountAmount() != null ? itemRequest.discountAmount() : BigDecimal.ZERO)
                        .build();
                checkout.addItem(item);
            });
        }

        // Calculate totals
        checkout.calculateTotals();

        Checkout savedCheckout = checkoutRepository.save(checkout);
        log.info("Checkout created successfully with ID: {}", savedCheckout.getId());

        return checkoutMapper.toResponse(savedCheckout);
    }

    @Override
    @Transactional
    public CheckoutResponse updateCheckoutStatus(UpdateCheckoutStatusRequest request) {
        log.info("Updating checkout status for ID: {}", request.checkoutId());
        
        Checkout checkout = checkoutRepository.findById(request.checkoutId())
                .orElseThrow(() -> new ResourceNotFoundException("Checkout not found with ID: " + request.checkoutId()));

        if (request.status() != null) {
            checkout.setStatus(request.status());
        }
        if (request.progress() != null) {
            checkout.setProgress(request.progress());
        }

        Checkout updatedCheckout = checkoutRepository.save(checkout);
        log.info("Checkout status updated successfully for ID: {}", updatedCheckout.getId());

        return checkoutMapper.toResponse(updatedCheckout);
    }

    @Override
    @Transactional(readOnly = true)
    public CheckoutResponse getCheckoutById(String id) {
        log.info("Retrieving checkout with ID: {}", id);
        
        Checkout checkout = checkoutRepository.findCheckoutWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checkout not found with ID: " + id));

        log.info("Checkout retrieved successfully: {}", id);
        return checkoutMapper.toResponse(checkout);
    }

    @Override
    @Transactional
    public CheckoutResponse updatePaymentMethod(String id, UpdatePaymentMethodRequest request) {
        log.info("Updating payment method for checkout ID: {}", id);
        
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checkout not found with ID: " + id));

        checkout.setPaymentMethodId(request.paymentMethodId());

        Checkout updatedCheckout = checkoutRepository.save(checkout);
        log.info("Payment method updated successfully for checkout ID: {}", id);

        return checkoutMapper.toResponse(updatedCheckout);
    }

    @Override
    @Transactional
    public CheckoutResponse recalculateTotals(String id) {
        log.info("Recalculating totals for checkout ID: {}", id);
        
        Checkout checkout = checkoutRepository.findCheckoutWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Checkout not found with ID: " + id));

        checkout.calculateTotals();
        Checkout updatedCheckout = checkoutRepository.save(checkout);
        
        log.info("Totals recalculated successfully for checkout ID: {}", id);
        return checkoutMapper.toResponse(updatedCheckout);
    }

    @Override
    @Transactional
    public void deleteCheckout(String id) {
        log.info("Deleting checkout with ID: {}", id);
        
        if (!checkoutRepository.existsById(id)) {
            throw new ResourceNotFoundException("Checkout not found with ID: " + id);
        }

        checkoutRepository.deleteById(id);
        log.info("Checkout deleted successfully: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String checkoutId) {
        return checkoutRepository.existsById(checkoutId);
    }
}
