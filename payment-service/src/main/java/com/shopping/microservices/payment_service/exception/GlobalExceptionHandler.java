package com.shopping.microservices.payment_service.exception;

import com.shopping.microservices.common_library.dto.ApiResponse;
import com.shopping.microservices.common_library.dto.ErrorResponse;
import com.shopping.microservices.common_library.exception.BusinessException;
import com.shopping.microservices.common_library.exception.PaymentException;
import com.shopping.microservices.common_library.exception.ResourceNotFoundException;
import com.shopping.microservices.common_library.exception.ValidationException;
import com.shopping.microservices.payment_service.gateway.PaymentGatewayFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Global exception handler for payment service.
 * 
 * Handles all exceptions and converts them to standardized API responses.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle PaymentException
     */
    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ApiResponse<Void>> handlePaymentException(
            PaymentException ex, HttpServletRequest request) {
        
        log.error("Payment exception: paymentId={}, orderId={}, message={}", 
            ex.getPaymentId(), ex.getOrderId(), ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getErrorCode(),
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Handle BusinessException
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(
            BusinessException ex, HttpServletRequest request) {
        
        log.error("Business exception: code={}, message={}", ex.getErrorCode(), ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
            ex.getHttpStatus(),
            ex.getErrorCode(),
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(ex.getHttpStatus())
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(
            ResourceNotFoundException ex, HttpServletRequest request) {
        
        log.error("Resource not found: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "RESOURCE_NOT_FOUND",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Handle GatewayNotFoundException
     */
    @ExceptionHandler(PaymentGatewayFactory.GatewayNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleGatewayNotFoundException(
            PaymentGatewayFactory.GatewayNotFoundException ex, HttpServletRequest request) {
        
        log.error("Gateway not found: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "GATEWAY_NOT_FOUND",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Handle UnsupportedPaymentMethodException
     */
    @ExceptionHandler(PaymentGatewayFactory.UnsupportedPaymentMethodException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnsupportedPaymentMethodException(
            PaymentGatewayFactory.UnsupportedPaymentMethodException ex, HttpServletRequest request) {
        
        log.error("Unsupported payment method: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "UNSUPPORTED_PAYMENT_METHOD",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Handle ValidationException
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(
            ValidationException ex, HttpServletRequest request) {
        
        log.error("Validation exception: {}", ex.getMessage());

        ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "VALIDATION_ERROR",
            ex.getMessage(),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Handle method argument validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.add(new ErrorResponse.FieldError(
                error.getField(),
                error.getDefaultMessage(),
                error.getRejectedValue(),
                error.getCode()
            ));
        }

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message("Validation failed")
            .path(request.getRequestURI())
            .errorCode("VALIDATION_ERROR")
            .traceId(UUID.randomUUID().toString())
            .errors(fieldErrors)
            .build();

        log.error("Validation failed: {} errors", fieldErrors.size());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Handle constraint violation errors
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException ex, HttpServletRequest request) {
        
        List<ErrorResponse.FieldError> fieldErrors = new ArrayList<>();
        ex.getConstraintViolations().forEach(violation -> {
            String field = violation.getPropertyPath().toString();
            fieldErrors.add(new ErrorResponse.FieldError(
                field,
                violation.getMessage(),
                violation.getInvalidValue(),
                "CONSTRAINT_VIOLATION"
            ));
        });

        ErrorResponse errorResponse = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
            .message("Constraint violation")
            .path(request.getRequestURI())
            .errorCode("CONSTRAINT_VIOLATION")
            .traceId(UUID.randomUUID().toString())
            .errors(fieldErrors)
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Handle missing request parameters
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "MISSING_PARAMETER",
            String.format("Required parameter '%s' is missing", ex.getParameterName()),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Handle type mismatch errors
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "TYPE_MISMATCH",
            String.format("Parameter '%s' should be of type '%s'", 
                ex.getName(), 
                ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown"),
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Handle message not readable (malformed JSON, etc.)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "MALFORMED_REQUEST",
            "Request body is malformed or missing",
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorResponse));
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        
        log.error("Unexpected error occurred", ex);

        ErrorResponse errorResponse = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR",
            "An unexpected error occurred. Please try again later.",
            request.getRequestURI()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.error(errorResponse));
    }

    // ==================== Helper Methods ====================

    private ErrorResponse buildErrorResponse(HttpStatus status, String errorCode, 
                                             String message, String path) {
        return ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(message)
            .path(path)
            .errorCode(errorCode)
            .traceId(UUID.randomUUID().toString())
            .build();
    }
}
