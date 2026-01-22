package com.shopping.microservices.common_library.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Generic API response wrapper for consistent API responses.
 * 
 * Provides a standard structure for successful and failed responses
 * with support for generic data types.
 *
 * @param <T> Type of the response data
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Whether the request was successful
     */
    private Boolean success;

    /**
     * Response data (null if error)
     */
    private T data;

    /**
     * Error details (null if success)
     */
    private ErrorResponse error;

    /**
     * Optional success/info message
     */
    private String message;

    /**
     * Pagination information (if applicable)
     */
    private PageInfo pageInfo;

    /**
     * Nested class for pagination metadata
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageInfo implements Serializable {
        
        private static final long serialVersionUID = 1L;

        /**
         * Current page number (0-indexed)
         */
        private Integer page;

        /**
         * Number of items per page
         */
        private Integer size;

        /**
         * Total number of items
         */
        private Long totalElements;

        /**
         * Total number of pages
         */
        private Integer totalPages;

        /**
         * Whether this is the first page
         */
        private Boolean first;

        /**
         * Whether this is the last page
         */
        private Boolean last;
    }

    /**
     * Static factory method for successful response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .build();
    }

    /**
     * Static factory method for successful response with data and message
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .message(message)
                .build();
    }

    /**
     * Static factory method for successful response with pagination
     */
    public static <T> ApiResponse<T> success(T data, PageInfo pageInfo) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .pageInfo(pageInfo)
                .build();
    }

    /**
     * Static factory method for successful response without data
     */
    public static <T> ApiResponse<T> success() {
        return ApiResponse.<T>builder()
                .success(true)
                .build();
    }

    /**
     * Static factory method for successful response with message only
     */
    public static <T> ApiResponse<T> successMessage(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    /**
     * Static factory method for error response
     */
    public static <T> ApiResponse<T> error(ErrorResponse error) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .build();
    }

    /**
     * Static factory method for error response with message
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    /**
     * Static factory method for error response with error and message
     */
    public static <T> ApiResponse<T> error(ErrorResponse error, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(error)
                .message(message)
                .build();
    }

    /**
     * Static factory method for 404 Not Found
     */
    public static <T> ApiResponse<T> notFound(String message, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorResponse.notFound(message, path))
                .message(message)
                .build();
    }

    /**
     * Static factory method for 400 Bad Request
     */
    public static <T> ApiResponse<T> badRequest(String message, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorResponse.badRequest(message, path))
                .message(message)
                .build();
    }

    /**
     * Check if response is successful
     */
    public boolean isSuccess() {
        return Boolean.TRUE.equals(success);
    }

    /**
     * Check if response has data
     */
    public boolean hasData() {
        return data != null;
    }

    /**
     * Check if response has error
     */
    public boolean hasError() {
        return error != null;
    }
}
