package com.shopping.microservices.identity_service.service.impl;

import com.shopping.microservices.identity_service.exception.ResourceNotFoundException;
import com.shopping.microservices.identity_service.entity.RoleEntity;
import com.shopping.microservices.identity_service.repository.RoleRepository;
import com.shopping.microservices.identity_service.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public RoleEntity getRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role %s not found".formatted(name)));
    }
}
