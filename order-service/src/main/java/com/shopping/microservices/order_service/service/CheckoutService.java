package com.shopping.microservices.order_service.service;

import com.shopping.microservices.order_service.dto.checkout.*;
import com.shopping.microservices.order_service.entity.Checkout;

public interface CheckoutService {

    /**
     * Create a new checkout session with items
     *
     * @param request the checkout creation request containing items and customer info
     * @return the created checkout response
     */
    CheckoutResponse createCheckout(CreateCheckoutRequest request);

    /**
     * Update checkout status and progress
     *
     * @param request the status update request
     * @return the associated order ID
     */
    Long updateCheckoutStatus(UpdateCheckoutStatusRequest request);

    /**
     * Get checkout in PENDING state by ID with all items
     *
     * @param checkoutId the checkout ID
     * @return the checkout response with items
     */
    CheckoutResponse getCheckoutPendingStateWithItemsById(String checkoutId);

    /**
     * Get checkout by ID with all items
     *
     * @param checkoutId the checkout ID
     * @return the checkout response with items
     */
    CheckoutResponse getCheckoutById(String checkoutId);

    /**
     * Update the payment method for a checkout
     *
     * @param checkoutId the checkout ID
     * @param request    the payment method update request
     */
    void updateCheckoutPaymentMethod(String checkoutId, UpdatePaymentMethodRequest request);

    /**
     * Calculate and update checkout totals
     *
     * @param checkoutId the checkout ID to recalculate
     * @return the updated checkout response with recalculated totals
     */
    CheckoutResponse recalculateTotals(String checkoutId);

    /**
     * Delete a checkout by ID
     *
     * @param checkoutId the checkout ID
     */
    void deleteCheckout(String checkoutId);

    /**
     * Check if checkout exists
     *
     * @param checkoutId the checkout ID
     * @return true if checkout exists
     */
    boolean existsById(String checkoutId);
}
