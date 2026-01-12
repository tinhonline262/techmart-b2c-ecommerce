package com.shopping.microservices.cart_service.repository;

import com.shopping.microservices.cart_service.entity.CartItem;
import com.shopping.microservices.cart_service.entity.CartItemId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for CartItem entity
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, CartItemId> {

    /**
     * Find all cart items for a specific customer
     *
     * @param customerId the customer ID
     * @return list of cart items
     */
    List<CartItem> findByIdCustomerId(String customerId);

    /**
     * Delete a specific cart item by customer ID and product ID
     *
     * @param customerId the customer ID
     * @param productId the product ID
     */
    void deleteByIdCustomerIdAndIdProductId(String customerId, Long productId);

    /**
     * Delete multiple cart items by customer ID and product IDs
     *
     * @param customerId the customer ID
     * @param productIds list of product IDs
     */
    void deleteByIdCustomerIdAndIdProductIdIn(String customerId, List<Long> productIds);
}
