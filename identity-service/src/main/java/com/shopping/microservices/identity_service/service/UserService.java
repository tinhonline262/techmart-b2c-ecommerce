package com.shopping.microservices.identity_service.service;

import com.shopping.microservices.identity_service.entity.SignUpEntity;
import com.shopping.microservices.identity_service.dto.UserProfileDTO;
import com.shopping.microservices.identity_service.entity.UserEntity;

public interface UserService {

    UserProfileDTO getProfileByUsername(String username);

    UserEntity getUserByUsername(String username);

    void createUser(SignUpEntity signUpEntity);

    void deleteUser(String id);
}
