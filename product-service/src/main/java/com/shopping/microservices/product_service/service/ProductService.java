package com.shopping.microservices.product_service.service;


import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.dto.ProductCreationDTO;
import com.shopping.microservices.product_service.dto.ProductDTO;


import java.util.List;

public interface ProductService {
    public ProductDTO findById(long id);
    public ProductDTO findBySku(String sku);
    public List<ProductDTO> findAll();
    public ProductDTO createProduct(ProductCreationDTO productCreationDTO);
    public ProductDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO);
    public void deleteProduct(Long id);
    public ProductDTO updateProductQuantity(Long id, InventoryUpdateDTO inventoryUpdateDTO);
    public ProductDTO subtractProductQuantity(Long id, InventorySubtractDTO inventorySubtractDTO);
    public ProductDTO reverseProductStockBySku(ProductReduceStockDTO productDTO);
}
