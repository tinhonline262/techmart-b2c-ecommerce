package com.shopping.microservices.identity_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceAlreadyExistsException extends RuntimeException {

    public ResourceAlreadyExistsException(String resourceName, String fieldName, String fieldValue) {
        super("%s already exists with the given input data %s : '%s'".formatted(resourceName, fieldName, fieldValue));
    }

    public ResourceAlreadyExistsException(String message) {
        super(message);
    }
}
