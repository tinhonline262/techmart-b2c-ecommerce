package com.shopping.microservices.order_service.mapper;

import com.shopping.microservices.order_service.dto.checkout.*;
import com.shopping.microservices.order_service.entity.Checkout;
import com.shopping.microservices.order_service.entity.CheckoutItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CheckoutMapper {

    // ==================== Checkout Entity <-> DTO ====================

    public Checkout toEntity(CreateCheckoutRequest request) {
        if (request == null) {
            return null;
        }
        
        Checkout checkout = new Checkout();
        checkout.setEmail(request.email());
        checkout.setNote(request.note());
        checkout.setPromotionCode(request.promotionCode());
        checkout.setCustomerId(request.customerId());
        checkout.setShipmentMethodId(request.shipmentMethodId());
        checkout.setPaymentMethodId(request.paymentMethodId());
        checkout.setShippingAddressId(request.shippingAddressId());
        checkout.setAttributes(request.attributes());
        checkout.setStatus("PENDING");
        checkout.setProgress("CREATED");
        
        return checkout;
    }

    public CheckoutResponse toResponse(Checkout checkout) {
        if (checkout == null) {
            return null;
        }
        
        return new CheckoutResponse(
            checkout.getId(),
            checkout.getEmail(),
            checkout.getNote(),
            checkout.getPromotionCode(),
            checkout.getStatus(),
            checkout.getProgress(),
            checkout.getCustomerId(),
            checkout.getShipmentMethodId(),
            checkout.getPaymentMethodId(),
            checkout.getShippingAddressId(),
            checkout.getTotalAmount(),
            checkout.getTotalShipmentFee(),
            checkout.getTotalShipmentTax(),
            checkout.getTotalTax(),
            checkout.getTotalDiscountAmount(),
            null,
            checkout.getAttributes(),
            checkout.getLastError(),
            checkout.getCreatedAt(),
            checkout.getUpdatedAt()
        );
    }

    public CheckoutResponse toResponseWithItems(Checkout checkout, List<CheckoutItemResponse> items) {
        if (checkout == null) {
            return null;
        }
        
        return new CheckoutResponse(
            checkout.getId(),
            checkout.getEmail(),
            checkout.getNote(),
            checkout.getPromotionCode(),
            checkout.getStatus(),
            checkout.getProgress(),
            checkout.getCustomerId(),
            checkout.getShipmentMethodId(),
            checkout.getPaymentMethodId(),
            checkout.getShippingAddressId(),
            checkout.getTotalAmount(),
            checkout.getTotalShipmentFee(),
            checkout.getTotalShipmentTax(),
            checkout.getTotalTax(),
            checkout.getTotalDiscountAmount(),
            items,
            checkout.getAttributes(),
            checkout.getLastError(),
            checkout.getCreatedAt(),
            checkout.getUpdatedAt()
        );
    }

    public void updateCheckoutStatus(UpdateCheckoutStatusRequest request, Checkout checkout) {
        if (request == null || checkout == null) {
            return;
        }
        
        if (request.status() != null) {
            checkout.setStatus(request.status());
        }
        if (request.progress() != null) {
            checkout.setProgress(request.progress());
        }
    }

    public void updatePaymentMethod(UpdatePaymentMethodRequest request, Checkout checkout) {
        if (request == null || checkout == null) {
            return;
        }
        
        if (request.paymentMethodId() != null) {
            checkout.setPaymentMethodId(request.paymentMethodId());
        }
    }

    // ==================== CheckoutItem Entity <-> DTO ====================

    public CheckoutItem toEntity(CheckoutItemRequest request) {
        if (request == null) {
            return null;
        }
        
        CheckoutItem item = new CheckoutItem();
        item.setProductId(request.productId());
        item.setName(request.name());
        item.setDescription(request.description());
        item.setQuantity(request.quantity());
        item.setPrice(request.price());
        item.setTax(request.tax());
        item.setShipmentFee(request.shipmentFee());
        item.setShipmentTax(request.shipmentTax());
        item.setDiscountAmount(request.discountAmount());
        
        return item;
    }

    public List<CheckoutItem> toEntityList(List<CheckoutItemRequest> requests) {
        if (requests == null) {
            return null;
        }
        
        return requests.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }

    public CheckoutItemResponse toResponse(CheckoutItem item) {
        if (item == null) {
            return null;
        }
        
        return new CheckoutItemResponse(
            item.getId(),
            item.getProductId(),
            item.getName(),
            item.getDescription(),
            item.getQuantity(),
            item.getPrice(),
            item.getTax(),
            item.getShipmentFee(),
            item.getShipmentTax(),
            item.getDiscountAmount(),
            calculateItemSubtotal(item),
            item.getCreatedAt(),
            item.getUpdatedAt()
        );
    }

    public List<CheckoutItemResponse> toResponseList(List<CheckoutItem> items) {
        if (items == null) {
            return null;
        }
        
        return items.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // ==================== Helper Methods ====================

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
