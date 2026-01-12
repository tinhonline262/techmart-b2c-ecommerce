package com.shopping.microservices.location_service.repository;

import com.shopping.microservices.location_service.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    
    @Query("SELECT a FROM Address a WHERE a.id IN :ids")
    List<Address> findByIdIn(@Param("ids") List<Long> ids);
}
