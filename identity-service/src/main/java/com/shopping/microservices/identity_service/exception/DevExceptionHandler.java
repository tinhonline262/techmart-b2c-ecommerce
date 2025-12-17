package com.shopping.microservices.identity_service.exception;

import com.shopping.microservices.identity_service.constant.ProfileConstant;
import com.shopping.microservices.identity_service.dto.ErrorResponseDTO;
import com.shopping.microservices.identity_service.utility.ExceptionHandlerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@RestControllerAdvice
@Profile(ProfileConstant.DEVELOPMENT)
class DevExceptionHandler {

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponseDTO> handleGeneralException(Exception ex, WebRequest request) {
        return ExceptionHandlerUtils.generateErrorResponse(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ErrorResponseDTO> handleRuntimeException(Exception ex, WebRequest request) {
        return ExceptionHandlerUtils.generateErrorResponse(ex, request, HttpStatus.BAD_REQUEST);
    }
}
