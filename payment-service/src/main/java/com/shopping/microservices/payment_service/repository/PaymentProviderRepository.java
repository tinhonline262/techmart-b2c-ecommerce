package com.shopping.microservices.payment_service.repository;

import com.shopping.microservices.payment_service.entity.PaymentProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentProviderRepository extends JpaRepository<PaymentProvider, String> {
    
    Optional<PaymentProvider> findByName(String name);
    
    List<PaymentProvider> findByEnabled(boolean enabled);
    
    @Query("SELECT p FROM PaymentProvider p WHERE p.enabled = true ORDER BY p.name")
    List<PaymentProvider> findAllActive();
    
    @Query("SELECT p FROM PaymentProvider p WHERE p.id IN :ids AND p.enabled = true")
    List<PaymentProvider> findActiveByIds(@Param("ids") List<String> ids);
    
    boolean existsByIdAndEnabled(String id, boolean enabled);
}
