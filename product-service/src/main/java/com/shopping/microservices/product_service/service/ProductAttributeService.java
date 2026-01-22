package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import org.springframework.data.domain.Pageable;

public interface ProductAttributeService {

    PageResponseDTO<ProductAttributeDTO> getAllAttributes(Pageable pageable);

    ProductAttributeDTO getAttributeById(Long id);

    ProductAttributeDTO createAttribute(ProductAttributeDTO dto);

    ProductAttributeDTO updateAttribute(Long id, ProductAttributeDTO dto);

    void deleteAttribute(Long id);
}
