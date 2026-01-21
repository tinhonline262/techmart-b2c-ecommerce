package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupUpdateDTO;
import org.springframework.data.domain.Pageable;

public interface ProductAttributeGroupService {
    PageResponseDTO<ProductAttributeGroupDTO> getAttributeGroups(Pageable pageable);
    ProductAttributeGroupDTO getAttributeGroupById(Long id);
    ProductAttributeGroupDTO createAttributeGroup(ProductAttributeGroupCreationDTO dto);
    ProductAttributeGroupDTO updateAttributeGroup(Long id, ProductAttributeGroupUpdateDTO dto);
    void deleteAttributeGroup(Long id);
}
