package com.shopping.microservices.order_service.utils;

public final class Constants {

    private Constants() {
        // Private constructor to prevent instantiation
    }

    public static final class ErrorCode {
        public static final String CHECKOUT_NOT_FOUND = "CHECKOUT_NOT_FOUND";
        public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";
        public static final String ORDER_NOT_FOUND = "ORDER_NOT_FOUND";
        public static final String FORBIDDEN = "FORBIDDEN";

        private ErrorCode() {
        }
    }

    public static final class MessageCode {
        public static final String CREATE_CHECKOUT = "Created checkout {} for customer {}";
        public static final String UPDATE_CHECKOUT_STATUS = "Updated checkout {} status from {} to {}";
        public static final String UPDATE_CHECKOUT_PAYMENT = "Updated checkout {} payment method from {} to {}";

        private MessageCode() {
        }
    }
}
