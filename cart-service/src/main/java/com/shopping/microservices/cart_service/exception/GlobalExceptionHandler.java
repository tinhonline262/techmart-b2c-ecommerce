package com.shopping.microservices.cart_service.exception;

import com.shopping.microservices.cart_service.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handle CartItemNotFoundException
     */
    @ExceptionHandler(CartItemNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleCartItemNotFoundException(
            CartItemNotFoundException ex, HttpServletRequest request) {
        log.error("CartItemNotFoundException: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        HttpStatus.NOT_FOUND.value(),
                        ErrorCode.CART_ITEM_NOT_FOUND.getMessage(),
                        ex.getMessage()
                ));
    }

    /**
     * Handle InvalidQuantityException
     */
    @ExceptionHandler(InvalidQuantityException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidQuantityException(
            InvalidQuantityException ex, HttpServletRequest request) {
        log.error("InvalidQuantityException: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.INVALID_QUANTITY.getMessage(),
                        ex.getMessage()
                ));
    }

    /**
     * Handle validation errors (MethodArgumentNotValidException)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationException(
            MethodArgumentNotValidException ex, HttpServletRequest request) {
        log.error("Validation error on path: {}", request.getRequestURI());
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        HttpStatus.BAD_REQUEST.value(),
                        ErrorCode.INVALID_REQUEST.getMessage(),
                        request.getRequestURI(),
                        errors
                ));
    }

    /**
     * Handle DataIntegrityViolationException
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolationException(
            DataIntegrityViolationException ex, HttpServletRequest request) {
        log.error("Data integrity violation: {} - Path: {}", ex.getMessage(), request.getRequestURI());
        
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(
                        HttpStatus.CONFLICT.value(),
                        ErrorCode.DATABASE_ERROR.getMessage(),
                        "Data integrity constraint violation"
                ));
    }

    /**
     * Handle NoHandlerFoundException (404)
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpServletRequest request) {
        log.error("No handler found for: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(
                        HttpStatus.NOT_FOUND.value(),
                        ErrorCode.RESOURCE_NOT_FOUND.getMessage(),
                        String.format("Endpoint not found: %s %s", ex.getHttpMethod(), ex.getRequestURL())
                ));
    }

    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred - Path: {} - Error: {}", 
                request.getRequestURI(), ex.getMessage(), ex);
        
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        ErrorCode.UNKNOWN_ERROR.getMessage(),
                        "An unexpected error occurred"
                ));
    }
}
