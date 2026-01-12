package com.shopping.microservices.location_service.repository;

import com.shopping.microservices.location_service.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    
    @Query(value = "SELECT * FROM district WHERE state_or_province_id = :stateOrProvinceId", nativeQuery = true)
    List<District> findByStateOrProvinceId(@Param("stateOrProvinceId") Long stateOrProvinceId);
}
