package com.shopping.microservices.order_service.mapper;

import com.shopping.microservices.order_service.dto.order.*;
import com.shopping.microservices.order_service.entity.Order;
import com.shopping.microservices.order_service.entity.OrderAddress;
import com.shopping.microservices.order_service.entity.OrderItem;
import com.shopping.microservices.order_service.enumeration.OrderStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

    // ==================== Order Entity <-> DTO ====================

    public Order toEntity(CreateOrderRequest request) {
        if (request == null) {
            return null;
        }
        
        Order order = new Order();
        order.setEmail(request.email());
        order.setNote(request.note());
        order.setPromotionCode(request.promotionCode());
        order.setCustomerId(request.customerId());
        order.setShipmentMethodId(request.shipmentMethodId());
        order.setPaymentMethodId(request.paymentMethodId());
        order.setAttributes(request.attributes());
        order.setStatus(OrderStatus.PENDING);
        order.setProgress("CREATED");
        order.setPaymentStatus("PENDING");
        order.setShipmentStatus("PENDING");
        order.setNumberItem(request.items() != null ? request.items().size() : 0);
        
        return order;
    }

    public OrderResponse toResponse(Order order) {
        if (order == null) {
            return null;
        }
        
        return new OrderResponse(
            order.getId(),
            order.getEmail(),
            order.getNote(),
            order.getPromotionCode(),
            order.getNumberItem(),
            order.getTotalAmount(),
            order.getTotalShipmentFee(),
            order.getTotalShipmentTax(),
            order.getTotalTax(),
            order.getTotalDiscountAmount(),
            order.getStatus(),
            order.getShipmentMethodId(),
            order.getShipmentStatus(),
            order.getCheckoutId(),
            order.getPaymentMethodId(),
            order.getPaymentStatus(),
            order.getPaymentId(),
            order.getProgress(),
            order.getCustomerId(),
            order.getRejectReason(),
            order.getLastError(),
            order.getAttributes(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            null,  // items
            null   // shippingAddress
        );
    }

    public OrderResponse toResponseWithDetails(Order order, List<OrderItemResponse> items, OrderAddressResponse shippingAddress) {
        if (order == null) {
            return null;
        }
        
        return new OrderResponse(
            order.getId(),
            order.getEmail(),
            order.getNote(),
            order.getPromotionCode(),
            order.getNumberItem(),
            order.getTotalAmount(),
            order.getTotalShipmentFee(),
            order.getTotalShipmentTax(),
            order.getTotalTax(),
            order.getTotalDiscountAmount(),
            order.getStatus(),
            order.getShipmentMethodId(),
            order.getShipmentStatus(),
            order.getCheckoutId(),
            order.getPaymentMethodId(),
            order.getPaymentStatus(),
            order.getPaymentId(),
            order.getProgress(),
            order.getCustomerId(),
            order.getRejectReason(),
            order.getLastError(),
            order.getAttributes(),
            order.getCreatedAt(),
            order.getUpdatedAt(),
            items,
            shippingAddress
        );
    }

    public OrderSummaryResponse toSummaryResponse(Order order) {
        if (order == null) {
            return null;
        }
        
        return new OrderSummaryResponse(
            order.getId(),
            order.getEmail(),
            order.getNumberItem(),
            order.getTotalAmount(),
            order.getStatus(),
            order.getPaymentStatus(),
            order.getShipmentStatus(),
            order.getProgress(),
            order.getCustomerId(),
            order.getCreatedAt(),
            order.getUpdatedAt()
        );
    }

    public List<OrderSummaryResponse> toSummaryResponseList(List<Order> orders) {
        if (orders == null) {
            return null;
        }
        
        return orders.stream()
            .map(this::toSummaryResponse)
            .collect(Collectors.toList());
    }

    public void updatePaymentStatus(UpdateOrderPaymentStatusRequest request, Order order) {
        if (request == null || order == null) {
            return;
        }
        
        if (request.paymentStatus() != null) {
            order.setPaymentStatus(request.paymentStatus());
        }
        if (request.paymentId() != null) {
            order.setPaymentId(request.paymentId());
        }
        if (request.rejectReason() != null) {
            order.setRejectReason(request.rejectReason());
        }
    }

    // ==================== OrderItem Entity <-> DTO ====================

    public OrderItem toEntity(OrderItemRequest request) {
        if (request == null) {
            return null;
        }
        
        OrderItem item = new OrderItem();
        item.setProductId(request.productId());
        item.setVariantId(request.variantId());
        item.setQuantity(request.quantity());
        item.setPrice(request.price());
        item.setTaxAmount(request.taxAmount());
        item.setShipmentFee(request.shipmentFee());
        item.setShipmentTax(request.shipmentTax());
        item.setDiscountAmount(request.discountAmount());
        item.setStatus("PENDING");
        
        return item;
    }

    public List<OrderItem> toEntityList(List<OrderItemRequest> requests) {
        if (requests == null) {
            return null;
        }
        
        return requests.stream()
            .map(this::toEntity)
            .collect(Collectors.toList());
    }

    public OrderItemResponse toResponse(OrderItem item) {
        if (item == null) {
            return null;
        }
        
        return new OrderItemResponse(
            item.getId(),
            item.getProductId(),
            item.getVariantId(),
            item.getQuantity(),
            item.getPrice(),
            item.getTaxAmount(),
            item.getShipmentFee(),
            item.getShipmentTax(),
            item.getDiscountAmount(),
            calculateItemSubtotal(item),
            item.getStatus(),
            item.getProcessingState()
        );
    }

    public List<OrderItemResponse> toResponseList(List<OrderItem> items) {
        if (items == null) {
            return null;
        }
        
        return items.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    // ==================== OrderAddress Entity <-> DTO ====================

    public OrderAddress toEntity(OrderAddressRequest request) {
        if (request == null) {
            return null;
        }
        
        OrderAddress address = new OrderAddress();
        address.setReceiverName(request.receiverName());
        address.setReceiverPhone(request.receiverPhone());
        address.setAddressLine1(request.addressLine1());
        address.setAddressLine2(request.addressLine2());
        address.setCity(request.city());
        address.setState(request.state());
        address.setCountry(request.country());
        address.setPostalCode(request.postalCode());
        
        return address;
    }

    public OrderAddressResponse toResponse(OrderAddress address) {
        if (address == null) {
            return null;
        }
        
        return new OrderAddressResponse(
            address.getId(),
            address.getReceiverName(),
            address.getReceiverPhone(),
            address.getAddressLine1(),
            address.getAddressLine2(),
            address.getCity(),
            address.getState(),
            address.getCountry(),
            address.getPostalCode()
        );
    }

    // ==================== Helper Methods ====================

    private BigDecimal calculateItemSubtotal(OrderItem item) {
        if (item.getPrice() == null || item.getQuantity() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal subtotal = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

        if (item.getTaxAmount() != null) {
            subtotal = subtotal.add(item.getTaxAmount());
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
