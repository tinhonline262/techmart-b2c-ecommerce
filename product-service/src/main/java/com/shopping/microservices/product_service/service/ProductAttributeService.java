package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeUpdateDTO;
import org.springframework.data.domain.Pageable;

public interface ProductAttributeService {
    PageResponseDTO<ProductAttributeDTO> getAttributes(Pageable pageable);
    ProductAttributeDTO getAttributeById(Long id);
    ProductAttributeDTO createAttribute(ProductAttributeCreationDTO dto);
    ProductAttributeDTO updateAttribute(Long id, ProductAttributeUpdateDTO dto);
    void deleteAttribute(Long id);
}