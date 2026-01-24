package com.shopping.microservices.order_service.mapper;

import com.shopping.microservices.order_service.dto.order.*;
import com.shopping.microservices.order_service.entity.Order;
import com.shopping.microservices.order_service.entity.OrderAddress;
import com.shopping.microservices.order_service.entity.OrderItem;
import com.shopping.microservices.order_service.enumeration.OrderProgress;
import com.shopping.microservices.order_service.enumeration.OrderStatus;
import com.shopping.microservices.order_service.enumeration.PaymentStatus;
import com.shopping.microservices.order_service.enumeration.ShipmentStatus;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper {

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
        order.setProgress(OrderProgress.CREATED);
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setShipmentStatus(ShipmentStatus.PENDING);
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
            order.getStatus() != null ? order.getStatus().name() : null,
            order.getShipmentMethodId(),
            order.getShipmentStatus() != null ? order.getShipmentStatus().name() : null,
            order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null,
            order.getPaymentId(),
            order.getCheckoutId(),
            order.getPaymentMethodId(),
            order.getProgress() != null ? order.getProgress().name() : null,
            order.getCustomerId(),
            order.getRejectReason(),
            null,
            null,
            order.getAttributes(),
            order.getLastError(),
            order.getCreatedAt(),
            order.getUpdatedAt()
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
            order.getStatus() != null ? order.getStatus().name() : null,
            order.getShipmentMethodId(),
            order.getShipmentStatus() != null ? order.getShipmentStatus().name() : null,
            order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null,
            order.getPaymentId(),
            order.getCheckoutId(),
            order.getPaymentMethodId(),
            order.getProgress() != null ? order.getProgress().name() : null,
            order.getCustomerId(),
            order.getRejectReason(),
            items,
            shippingAddress,
            order.getAttributes(),
            order.getLastError(),
            order.getCreatedAt(),
            order.getUpdatedAt()
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
            order.getStatus() != null ? order.getStatus().name() : null,
            order.getPaymentStatus() != null ? order.getPaymentStatus().name() : null,
            order.getShipmentStatus() != null ? order.getShipmentStatus().name() : null,
            order.getCustomerId(),
            order.getCreatedAt()
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
            order.setPaymentStatus(PaymentStatus.valueOf(request.paymentStatus()));
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
        item.setName(request.name());
        item.setDescription(request.description());
        item.setQuantity(request.quantity());
        item.setPrice(request.price());
        item.setTaxAmount(request.taxAmount());
        item.setTaxPercent(request.taxPercent());
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
            item.getName(),
            item.getDescription(),
            item.getQuantity(),
            item.getPrice(),
            item.getDiscountAmount(),
            item.getTaxAmount(),
            item.getTaxPercent(),
            item.getShipmentFee(),
            item.getShipmentTax(),
            item.getStatus(),
            calculateItemSubtotal(item),
            item.getProcessingState(),
            item.getCreatedAt(),
            item.getUpdatedAt()
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
        address.setContactName(request.contactName());
        address.setPhone(request.phone());
        address.setAddressLine1(request.addressLine1());
        address.setAddressLine2(request.addressLine2());
        address.setCity(request.city());
        address.setZipCode(request.zipCode());
        address.setDistrictId(request.districtId());
        address.setDistrictName(request.districtName());
        address.setStateOrProvinceId(request.stateOrProvinceId());
        address.setStateOrProvinceName(request.stateOrProvinceName());
        address.setCountryId(request.countryId());
        address.setCountryName(request.countryName());
        
        return address;
    }

    public OrderAddressResponse toResponse(OrderAddress address) {
        if (address == null) {
            return null;
        }
        
        return new OrderAddressResponse(
            address.getId(),
            address.getContactName(),
            address.getPhone(),
            address.getAddressLine1(),
            address.getAddressLine2(),
            address.getCity(),
            address.getZipCode(),
            address.getDistrictId(),
            address.getDistrictName(),
            address.getStateOrProvinceId(),
            address.getStateOrProvinceName(),
            address.getCountryId(),
            address.getCountryName()
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
