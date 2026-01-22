package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupDTO;
import org.springframework.data.domain.Pageable;

public interface ProductAttributeGroupService {

    PageResponseDTO<ProductAttributeGroupDTO> getAllAttributeGroups(Pageable pageable);

    ProductAttributeGroupDTO getAttributeGroupById(Long id);
}
