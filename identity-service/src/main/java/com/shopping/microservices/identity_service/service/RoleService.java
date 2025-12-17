package com.shopping.microservices.identity_service.service;

import com.shopping.microservices.identity_service.entity.RoleEntity;

public interface RoleService {

    RoleEntity getRoleByName(String name);
}
