package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.ProductOptionValueDTO;

import java.util.List;

public interface ProductOptionValueService {
    List<ProductOptionValueDTO> getOptionValues(Long optionId);
}
