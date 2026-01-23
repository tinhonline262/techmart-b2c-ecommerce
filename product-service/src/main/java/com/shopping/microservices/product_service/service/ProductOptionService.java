package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductOptionCreationDTO;
import com.shopping.microservices.product_service.dto.ProductOptionDTO;
import com.shopping.microservices.product_service.dto.ProductOptionUpdateDTO;
import org.springframework.data.domain.Pageable;

public interface ProductOptionService {
    PageResponseDTO<ProductOptionDTO> getOptions(Pageable pageable);
    ProductOptionDTO getOptionById(Long id);
    ProductOptionDTO createOption(ProductOptionCreationDTO dto);
    ProductOptionDTO updateOption(Long id, ProductOptionUpdateDTO dto);
    void deleteOption(Long id);
}
