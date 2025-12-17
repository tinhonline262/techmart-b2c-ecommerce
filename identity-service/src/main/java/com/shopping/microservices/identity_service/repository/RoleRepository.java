package com.shopping.microservices.identity_service.repository;

import com.shopping.microservices.identity_service.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleEntity, String> {

    Optional<RoleEntity> findByName(String name);

    List<RoleEntity> findAllByNameIn(List<String> roles);
}
