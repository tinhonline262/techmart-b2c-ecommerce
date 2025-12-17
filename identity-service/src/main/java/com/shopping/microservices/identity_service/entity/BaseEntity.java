package com.shopping.microservices.identity_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A BaseEntity is a base superclass for JPA entities that comes with a mandatory primary key field, an application assign id and an
 * optimistic locking field.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public abstract class BaseEntity {

    /** Primary key */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /** Optimistic locking field (Property name {@code version} might be used differently, hence this is named {@code ol}). */
    @Version
    private long ol;

    /** Timestamp when the database record was inserted. */
    @Temporal(TemporalType.TIMESTAMP)
    @CreatedDate
    private LocalDateTime createdAt;

    /** Username who created the entity. */
    @CreatedBy
    private String createdBy;

    /** Timestamp when the database record was updated the last time. */
    @Temporal(TemporalType.TIMESTAMP)
    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    /** Username who modified the entity. */
    @LastModifiedBy
    private String lastModifiedBy;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
