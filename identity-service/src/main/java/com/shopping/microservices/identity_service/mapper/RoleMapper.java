package com.shopping.microservices.identity_service.mapper;

import com.shopping.microservices.identity_service.entity.RoleEntity;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper
public interface RoleMapper {

    default List<String> map(Set<RoleEntity> roles) {
        if (roles == null) return List.of();
        return roles.stream()
                .map(RoleEntity::getName)
                .collect(Collectors.toList());
    }
}
