package com.shopping.microservices.location_service.repository;

import com.shopping.microservices.location_service.entity.StateOrProvince;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StateOrProvinceRepository extends JpaRepository<StateOrProvince, Long> {
    
    Page<StateOrProvince> findByCountryId(Long countryId, Pageable pageable);
    
    List<StateOrProvince> findByCountryId(Long countryId);
    
    @Query("SELECT s FROM StateOrProvince s WHERE s.id IN :ids")
    List<StateOrProvince> findByIdIn(@Param("ids") List<Long> ids);
}
