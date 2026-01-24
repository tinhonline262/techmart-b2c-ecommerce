package com.shopping.microservices.order_service.service.impl;

import com.shopping.microservices.common_library.exception.BadRequestException;
import com.shopping.microservices.common_library.exception.ForbiddenException;
import com.shopping.microservices.common_library.exception.ResourceNotFoundException;
import com.shopping.microservices.order_service.dto.ApiResponse;
import com.shopping.microservices.order_service.dto.checkout.*;
import com.shopping.microservices.order_service.dto.product.ProductDTO;
import com.shopping.microservices.order_service.entity.Checkout;
import com.shopping.microservices.order_service.entity.CheckoutItem;
import com.shopping.microservices.order_service.mapper.CheckoutMapper;
import com.shopping.microservices.order_service.model.enumeration.CheckoutState;
import com.shopping.microservices.order_service.repository.CheckoutRepository;
import com.shopping.microservices.order_service.service.CheckoutService;
import com.shopping.microservices.order_service.service.OrderService;
import com.shopping.microservices.order_service.service.ProductService;
import com.shopping.microservices.order_service.utils.AuthenticationUtils;
import com.shopping.microservices.order_service.utils.Constants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CheckoutRepository checkoutRepository;
    private final CheckoutMapper checkoutMapper;
    private final ProductService productService;
    private final OrderService orderService;

    /**
     * Creates a new {@link Checkout} object in a PENDING state.
     *
     * @param checkoutPostVm the view model containing checkout details and items
     * @return a {@link CheckoutResponse} object representing the newly created checkout
     */
    @Override
    @Transactional
    public CheckoutResponse createCheckout(CreateCheckoutRequest checkoutPostVm) {
        Checkout checkout = checkoutMapper.toEntity(checkoutPostVm);
        checkout.setStatus(CheckoutState.PENDING.name());
        checkout.setCustomerId(AuthenticationUtils.extractUserId());

        prepareCheckoutItems(checkout, checkoutPostVm);
        checkout = checkoutRepository.save(checkout);

        CheckoutResponse checkoutVm = checkoutMapper.toDTO(checkout);
        log.info(Constants.MessageCode.CREATE_CHECKOUT, checkout.getId(), checkout.getCustomerId());
        
        return checkoutVm;
    }

    /**
     * Prepares checkout items by fetching product information and enriching items with product details.
     */
    private void prepareCheckoutItems(Checkout checkout, CreateCheckoutRequest checkoutPostVm) {
        if (CollectionUtils.isEmpty(checkoutPostVm.items())) {
            throw new BadRequestException("Checkout must contain at least one item");
        }

        Set<Long> productIds = checkoutPostVm.items()
                .stream()
                .map(CheckoutItemRequest::productId)
                .collect(Collectors.toSet());

        List<CheckoutItem> checkoutItems = checkoutPostVm.items()
                .stream()
                .map(checkoutMapper::toEntity)
                .map(item -> {
                    item.setCheckout(checkout);
                    return item;
                }).toList();

        Map<Long, ProductDTO> products = productService.getProductInformation(productIds, 0, productIds.size());

        List<CheckoutItem> enrichedItems = enrichCheckoutItemsWithProductDetails(products, checkoutItems);
        BigDecimal totalAmount = enrichedItems.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        checkout.setItems(enrichedItems);
        checkout.calculateTotals();
        checkout.setTotalAmount(totalAmount);

    }

    /**
     * Enriches checkout items with product details fetched from the product service.
     */
    private List<CheckoutItem> enrichCheckoutItemsWithProductDetails(
            Map<Long, ProductDTO> products,
            List<CheckoutItem> checkoutItems) {
        return checkoutItems.stream().map(item -> {
            ProductDTO product = products.get(item.getProductId());
            if (product == null) {
                throw new ResourceNotFoundException(
                    String.format("Product not found with ID: %d", item.getProductId())
                );
            }
            item.setName(product.name());
            item.setPrice(product.price());
            // Set default values for tax and fees if not already set
            if (item.getTax() == null) {
                item.setTax(BigDecimal.ZERO);
            }
            if (item.getShipmentFee() == null) {
                item.setShipmentFee(BigDecimal.ZERO);
            }
            if (item.getShipmentTax() == null) {
                item.setShipmentTax(BigDecimal.ZERO);
            }
            if (item.getDiscountAmount() == null) {
                item.setDiscountAmount(BigDecimal.ZERO);
            }
            return item;
        }).toList();
    }

    /**
     * Retrieves a checkout in PENDING state with its items by ID.
     * Validates that the checkout is owned by the current user.
     */
    @Override
    @Transactional(readOnly = true)
    public CheckoutResponse getCheckoutPendingStateWithItemsById(String id) {
        Checkout checkout = checkoutRepository.findByIdAndCheckoutState(id, CheckoutState.PENDING.name())
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("Checkout not found with ID: %s in PENDING state", id)
                ));

        if (isNotOwnedByCurrentUser(checkout)) {
            throw new ForbiddenException("You cannot view this checkout");
        }

        return checkoutMapper.toDTO(checkout);
    }

    /**
     * Updates the checkout status and returns the associated order ID.
     */
    @Override
    @Transactional
    public Long updateCheckoutStatus(UpdateCheckoutStatusRequest checkoutStatusPutVm) {
        Checkout checkout = checkoutRepository.findById(checkoutStatusPutVm.checkoutId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("Checkout not found with ID: %s", checkoutStatusPutVm.checkoutId())
                ));

        if (isNotOwnedByCurrentUser(checkout)) {
            throw new ForbiddenException("You are not authorized to update this checkout");
        }

        String oldStatus = checkout.getStatus();
        checkout.setStatus(checkoutStatusPutVm.status());
        if (checkoutStatusPutVm.progress() != null) {
            checkout.setProgress(checkoutStatusPutVm.progress());
        }
        
        checkoutRepository.save(checkout);
        log.info(Constants.MessageCode.UPDATE_CHECKOUT_STATUS,
                checkout.getId(),
                oldStatus,
                checkoutStatusPutVm.status()
        );
        
        // Find associated order
        return orderService.findOrderByCheckoutId(checkoutStatusPutVm.checkoutId()).orderId();
    }

    /**
     * Updates the payment method for a checkout.
     */
    @Override
    @Transactional
    public void updateCheckoutPaymentMethod(String id, UpdatePaymentMethodRequest checkoutPaymentMethodPutVm) {
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("Checkout not found with ID: %s", id)
                ));
        
        if (isNotOwnedByCurrentUser(checkout)) {
            throw new ForbiddenException("You are not authorized to update this checkout");
        }
        
        String oldPaymentMethod = checkout.getPaymentMethodId();
        checkout.setPaymentMethodId(checkoutPaymentMethodPutVm.paymentMethodId());
        
        log.info(Constants.MessageCode.UPDATE_CHECKOUT_PAYMENT,
                checkout.getId(),
                oldPaymentMethod,
                checkoutPaymentMethodPutVm.paymentMethodId()
        );
        
        checkoutRepository.save(checkout);
    }

    /**
     * Retrieves a checkout by ID with all its items.
     */
    @Override
    @Transactional(readOnly = true)
    public CheckoutResponse getCheckoutById(String id) {
        log.info("Retrieving checkout with ID: {}", id);
        
        Checkout checkout = checkoutRepository.findCheckoutWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("Checkout not found with ID: %s", id)
                ));

        if (isNotOwnedByCurrentUser(checkout)) {
            throw new ForbiddenException("You cannot view this checkout");
        }

        log.info("Checkout retrieved successfully: {}", id);
        return checkoutMapper.toDTO(checkout);
    }

    /**
     * Recalculates all totals for a checkout based on its items.
     */
    @Override
    @Transactional
    public CheckoutResponse recalculateTotals(String id) {
        log.info("Recalculating totals for checkout ID: {}", id);
        
        Checkout checkout = checkoutRepository.findCheckoutWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("Checkout not found with ID: %s", id)
                ));

        if (isNotOwnedByCurrentUser(checkout)) {
            throw new ForbiddenException("You are not authorized to update this checkout");
        }

        checkout.calculateTotals();
        Checkout updatedCheckout = checkoutRepository.save(checkout);
        
        log.info("Totals recalculated successfully for checkout ID: {}", id);
        return checkoutMapper.toDTO(updatedCheckout);
    }

    /**
     * Deletes a checkout by ID.
     */
    @Override
    @Transactional
    public void deleteCheckout(String id) {
        log.info("Deleting checkout with ID: {}", id);
        
        Checkout checkout = checkoutRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    String.format("Checkout not found with ID: %s", id)
                ));

        if (isNotOwnedByCurrentUser(checkout)) {
            throw new ForbiddenException("You are not authorized to delete this checkout");
        }

        checkoutRepository.deleteById(id);
        log.info("Checkout deleted successfully: {}", id);
    }

    /**
     * Checks if a checkout exists by ID.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String checkoutId) {
        return checkoutRepository.existsById(checkoutId);
    }

    /**
     * Validates if the checkout is not owned by the current authenticated user.
     * 
     * @param checkout the checkout to validate
     * @return true if the checkout is NOT owned by the current user, false otherwise
     */
    private boolean isNotOwnedByCurrentUser(Checkout checkout) {
        String currentUserId = AuthenticationUtils.extractUserId();
        if (currentUserId == null) {
            return true; // Not authenticated
        }
        return !currentUserId.equals(checkout.getCustomerId());
    }
}
