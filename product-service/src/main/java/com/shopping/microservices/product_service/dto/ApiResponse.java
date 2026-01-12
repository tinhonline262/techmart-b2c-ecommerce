package com.shopping.microservices.product_service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApiResponse<T> {
    int status;
    String message;
    String path;
    LocalDateTime timestamp;
    T data;

    public ApiResponse(int status, String message, T data, String path) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }

    // New simplified success methods for controllers
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .status(200)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Success");
    }

    // Legacy success method (for backward compatibility)
    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(status, message, data, null);
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .status(201)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> error(int status, String message, String path) {
        return new ApiResponse<>(status, message, null, path);
    }

    public static <T> ApiResponse<T> error(int status, String message, String path, T data) {
        return new ApiResponse<>(status, message, data, path);
    }
}
