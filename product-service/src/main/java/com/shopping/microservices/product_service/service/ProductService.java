package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.ProductCreationDTO;
import com.shopping.microservices.product_service.dto.ProductDTO;
import com.shopping.microservices.product_service.dto.ProductReduceStockDTO;

import java.util.List;

public interface ProductService {
    public ProductDTO findById(long id);
    public ProductDTO findBySku(String sku);
    public List<ProductDTO> findAll();
    public ProductDTO createProduct(ProductCreationDTO productCreationDTO);
    public  ProductDTO reverseProductStockBySku(ProductReduceStockDTO productDTO);
}
