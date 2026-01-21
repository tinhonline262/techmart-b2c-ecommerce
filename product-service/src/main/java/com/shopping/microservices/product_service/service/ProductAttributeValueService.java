package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.ProductAttributeValueDTO;

import java.util.List;

public interface ProductAttributeValueService {
    List<ProductAttributeValueDTO> getAttributeValues(Long attributeId);
}
