package com.shopping.microservices.order_service.mapper;

import com.shopping.microservices.order_service.dto.checkout.*;
import com.shopping.microservices.order_service.entity.Checkout;
import com.shopping.microservices.order_service.entity.CheckoutItem;
import com.shopping.microservices.order_service.model.enumeration.CheckoutState;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class CheckoutMapper {

    // ==================== Checkout Entity <-> DTO ====================

    /**
     * Converts CreateCheckoutRequest to Checkout entity.
     * Sets initial status and progress values.
     */
    public Checkout toEntity(CreateCheckoutRequest request) {
        if (request == null) {
            return null;
        }
        
        Checkout checkout = new Checkout();
        checkout.setEmail(request.email());
        checkout.setNote(request.note());
        checkout.setPromotionCode(request.promotionCode());
        checkout.setShipmentMethodId(request.shipmentMethodId());
        checkout.setPaymentMethodId(request.paymentMethodId());
        checkout.setShippingAddressId(request.shippingAddressId());
        checkout.setStatus("PENDING");
        checkout.setProgress("CREATED");
        
        return checkout;
    }

    /**
     * Converts Checkout entity to CheckoutResponse ViewModel.
     * Automatically maps nested items if present.
     */
    public CheckoutResponse toDTO(Checkout checkout) {
        if (checkout == null) {
            return null;
        }
        
        List<CheckoutItemResponse> items = null;
        if (checkout.getItems() != null) {
            items = checkout.getItems().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
        }
        
        return CheckoutResponse.builder()
            .id(checkout.getId())
            .email(checkout.getEmail())
            .note(checkout.getNote())
            .promotionCode(checkout.getPromotionCode())
            .status(checkout.getStatus())
            .progress(checkout.getProgress())
            .customerId(checkout.getCustomerId())
            .shipmentMethodId(checkout.getShipmentMethodId())
            .paymentMethodId(checkout.getPaymentMethodId())
            .shippingAddressId(checkout.getShippingAddressId())
            .totalAmount(checkout.getTotalAmount())
            .totalShipmentFee(checkout.getTotalShipmentFee())
            .totalShipmentTax(checkout.getTotalShipmentTax())
            .totalTax(checkout.getTotalTax())
            .totalDiscountAmount(checkout.getTotalDiscountAmount())
            .items(items)
            .build();
    }

    // ==================== CheckoutItem Entity <-> DTO ====================

    /**
     * Converts CheckoutItemRequest to CheckoutItem entity.
     * Product details (name, price) will be enriched from ProductService later.
     */
    public CheckoutItem toEntity(CheckoutItemRequest request) {
        if (request == null) {
            return null;
        }
        
        CheckoutItem item = new CheckoutItem();
        item.setProductId(request.productId());
        item.setDescription(request.description());
        item.setQuantity(request.quantity());
        // name, price, tax, shipmentFee, shipmentTax, discountAmount 
        // will be set from product service data during checkout creation
        
        return item;
    }

    /**
     * Converts CheckoutItem entity to CheckoutItemResponse ViewModel.
     */
    public CheckoutItemResponse toDTO(CheckoutItem item) {
        if (item == null) {
            return null;
        }
        
        return CheckoutItemResponse.builder()
            .id(item.getId())
            .productId(item.getProductId())
            .name(item.getName())
            .description(item.getDescription())
            .quantity(item.getQuantity())
            .price(item.getPrice())
            .tax(item.getTax())
            .shipmentFee(item.getShipmentFee())
            .shipmentTax(item.getShipmentTax())
            .discountAmount(item.getDiscountAmount())
            .subtotal(calculateItemSubtotal(item))
            .checkoutId(item.getCheckout() != null ? item.getCheckout().getId() : null)
            .build();
    }

    // ==================== Helper Methods ====================

    /**
     * Calculates the subtotal for a checkout item.
     * Formula: (price * quantity) + tax + shipmentFee + shipmentTax - discountAmount
     */
    private BigDecimal calculateItemSubtotal(CheckoutItem item) {
        if (item.getPrice() == null || item.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        if (item.getTax() != null) {
            subtotal = subtotal.add(item.getTax());
        }
        if (item.getShipmentFee() != null) {
            subtotal = subtotal.add(item.getShipmentFee());
        }
        if (item.getShipmentTax() != null) {
            subtotal = subtotal.add(item.getShipmentTax());
        }
        if (item.getDiscountAmount() != null) {
            subtotal = subtotal.subtract(item.getDiscountAmount());
        }

        return subtotal;
    }
}
