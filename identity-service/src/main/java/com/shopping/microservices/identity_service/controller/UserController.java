package com.shopping.microservices.identity_service.controller;

import com.shopping.microservices.identity_service.dto.ApiResponse;
import com.shopping.microservices.identity_service.dto.UserProfileDTO;
import com.shopping.microservices.identity_service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @GetMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    ApiResponse<UserProfileDTO> getProfileByUsername(@PathVariable String username) {
        return ApiResponse.success(HttpStatus.OK.value(), "User profile", userService.getProfileByUsername(username));
    }

    @DeleteMapping("/{username}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    void deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
    }
}
