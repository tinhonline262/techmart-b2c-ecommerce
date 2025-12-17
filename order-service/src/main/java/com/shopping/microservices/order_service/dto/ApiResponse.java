package com.shopping.microservices.order_service.dto;


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
public class ApiResponse <T> {
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

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(status, message, data, null);
    }
    public static <T> ApiResponse<T> error(int status, String message, String path) {
        return new ApiResponse<>(status, message, null, path);
    }
    public static <T> ApiResponse<T> error(int status, String message, String path, T data) {
        return new ApiResponse<>(status, message, data, path);
    }
}
