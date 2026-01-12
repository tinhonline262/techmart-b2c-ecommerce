package com.shopping.microservices.cart_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class CartItemId implements Serializable {
    private static final long serialVersionUID = -7848974133211783040L;
    @Size(max = 50)
    @NotNull
    @Column(name = "customer_id", nullable = false, length = 50)
    private String customerId;

    @NotNull
    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CartItemId entity = (CartItemId) o;
        return Objects.equals(this.productId, entity.productId) &&
                Objects.equals(this.customerId, entity.customerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, customerId);
    }

}