package com.shopping.microservices.identity_service.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class RegistrationDTO {

    @NotEmpty(message = "Username can not be null or empty")
    @Pattern(regexp = "^[a-z0-9]+[a-z0-9_]{3,15}$", message = "Username is not valid")
    private String username;

    @NotEmpty(message = "Password can not be null or empty")
    @Pattern(regexp = "^[a-z0-9]+[a-z0-9_]{3,15}$", message = "Password is not valid")
    private String password;

    @NotEmpty(message = "Name of user can not be null or empty")
    private String name;

    @NotEmpty(message = "Email can not be null or empty")
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", message = "Email is not valid")
    private String email;
}
