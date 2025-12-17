package com.shopping.microservices.identity_service.utility;

import com.shopping.microservices.identity_service.dto.ErrorResponseDTO;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

@UtilityClass
@Slf4j
public class ExceptionHandlerUtils {

    public ResponseEntity<ErrorResponseDTO> generateErrorResponse(Exception ex, String exceptionMessage, WebRequest request, HttpStatus status) {
        log.error("Exception caught - status: {}, path: {}, message: {}",
                status.value(),
                request.getDescription(false),
                ex.getMessage());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .message(exceptionMessage)
                .path(request.getDescription(false))
                .status(status.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(status.value()));
    }

    public ResponseEntity<ErrorResponseDTO> generateErrorResponse(Exception ex, WebRequest request, HttpStatus status) {
        log.error("Exception caught - status: {}, path: {}, message: {}",
                status.value(),
                request.getDescription(false),
                ex.getMessage());

        ErrorResponseDTO errorResponse = ErrorResponseDTO.builder()
                .message(ex.getMessage())
                .path(request.getDescription(false))
                .status(status.value())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatusCode.valueOf(status.value()));
    }
}
