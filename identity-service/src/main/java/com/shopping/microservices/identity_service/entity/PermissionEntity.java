package com.shopping.microservices.identity_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "permission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PermissionEntity extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(length = 512)
    private String description;
}
