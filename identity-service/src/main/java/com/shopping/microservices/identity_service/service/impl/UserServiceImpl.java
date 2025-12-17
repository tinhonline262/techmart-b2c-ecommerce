package com.shopping.microservices.identity_service.service.impl;

import com.shopping.microservices.identity_service.exception.ResourceAlreadyExistsException;
import com.shopping.microservices.identity_service.exception.ResourceNotFoundException;
import com.shopping.microservices.identity_service.entity.RoleEntity;
import com.shopping.microservices.identity_service.entity.SignUpEntity;
import com.shopping.microservices.identity_service.enumeration.UserDefaultType;
import com.shopping.microservices.identity_service.service.RoleService;
import com.shopping.microservices.identity_service.dto.UserProfileDTO;
import com.shopping.microservices.identity_service.entity.UserEntity;
import com.shopping.microservices.identity_service.mapper.UserMapper;
import com.shopping.microservices.identity_service.repository.UserRepository;
import com.shopping.microservices.identity_service.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleService roleService;

    @Override
    public UserProfileDTO getProfileByUsername(String username) {
        log.info("Getting profile for username: {}", username);
        UserEntity userEntity = getUserEntity(username);
        return userMapper.mapToProfileDTO(userEntity);
    }

    @Override
    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User with username '%s' not found".
                        formatted(username)));
    }

    @Override
    public void createUser(SignUpEntity signUpEntity) {
        if (userRepository.existsByUsername(signUpEntity.getUsername())) {
            throw new ResourceAlreadyExistsException("User is already existed");
        }
        if (userRepository.existsByEmail(signUpEntity.getEmail())) {
            throw new ResourceAlreadyExistsException("Email is already existed");
        }

        // Save user info to database
        UserEntity userEntity = userMapper.mapToEntity(signUpEntity);
        RoleEntity userRole = roleService.getRoleByName(UserDefaultType.USER.name());
        userEntity.setRoles(Set.of(userRole));
        userEntity.setSignUp(signUpEntity);
        userRepository.save(userEntity);
    }

    @Override
    public void deleteUser(String username) {
        log.info("Deleting user with username: {}", username);
        UserEntity userEntity = getUserEntity(username);
        // Soft delete
        userRepository.delete(userEntity);
        log.info("User {} deleted (soft delete)", username);
    }

    private UserEntity getUserEntity(String username) {
        return userRepository.findByUsername(username).orElseThrow(
                () -> new ResourceNotFoundException("User", "username", username)
        );
    }
}
