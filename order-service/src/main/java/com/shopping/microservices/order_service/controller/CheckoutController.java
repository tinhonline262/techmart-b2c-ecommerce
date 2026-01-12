package com.shopping.microservices.order_service.controller;

import com.shopping.microservices.order_service.dto.ApiResponse;
import com.shopping.microservices.order_service.dto.checkout.*;
import com.shopping.microservices.order_service.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for public checkout operations.
 * Base path: /api/v1/public/checkouts
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/checkouts")
public class CheckoutController {

    private final CheckoutService checkoutService;

    /**
     * Create a new checkout session with items.
     * POST /api/v1/public/checkouts
     *
     * @param request the checkout creation request
     * @return created checkout response
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<CheckoutResponse>> createCheckout(
            @Valid @RequestBody CreateCheckoutRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        HttpStatus.CREATED.value(),
                        "Checkout created successfully",
                        null // placeholder: checkoutService.createCheckout(request)
                ));
    }

    /**
     * Update checkout status and progress.
     * PUT /api/v1/public/checkouts/status
     *
     * @param request the status update request
     * @return updated checkout response
     */
    @PutMapping("/status")
    public ResponseEntity<ApiResponse<CheckoutResponse>> updateCheckoutStatus(
            @Valid @RequestBody UpdateCheckoutStatusRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Checkout status updated successfully",
                null // placeholder: checkoutService.updateCheckoutStatus(request)
        ));
    }

    /**
     * Get checkout by ID with all items.
     * GET /api/v1/public/checkouts/{id}
     *
     * @param id the checkout ID
     * @return checkout response with items
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CheckoutResponse>> getCheckoutById(
            @PathVariable("id") String id) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Checkout retrieved successfully",
                null // placeholder: checkoutService.getCheckoutById(id)
        ));
    }

    /**
     * Update the payment method for a checkout.
     * PUT /api/v1/public/checkouts/{id}/payment-method
     *
     * @param id      the checkout ID
     * @param request the payment method update request
     * @return updated checkout response
     */
    @PutMapping("/{id}/payment-method")
    public ResponseEntity<ApiResponse<CheckoutResponse>> updatePaymentMethod(
            @PathVariable("id") String id,
            @Valid @RequestBody UpdatePaymentMethodRequest request) {
        return ResponseEntity.ok(ApiResponse.success(
                HttpStatus.OK.value(),
                "Payment method updated successfully",
                null // placeholder: checkoutService.updatePaymentMethod(id, request)
        ));
    }
}
