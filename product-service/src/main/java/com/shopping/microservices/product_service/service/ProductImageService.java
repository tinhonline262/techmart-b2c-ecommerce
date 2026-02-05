package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.ProductImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductImageService {

    /**
     * Upload image without product association (for Add Product page)
     * @param file the image file to upload
     * @return ProductImageDTO with imageUrl
     */
    ProductImageDTO uploadImage(MultipartFile file) throws IOException;

    /**
     * Upload single image for existing product
     */
    ProductImageDTO uploadProductImage(Long productId, MultipartFile file) throws IOException;

    /**
     * Upload multiple images for existing product
     */
    List<ProductImageDTO> uploadProductImages(Long productId, List<MultipartFile> files) throws IOException;

    /**
     * Get all images for a product
     */
    List<ProductImageDTO> getProductImages(Long productId);

    /**
     * Delete product image
     */
    void deleteProductImage(Long productId, Long imageId) throws IOException;

    /**
     * Get image by ID
     */
    ProductImageDTO getImageById(Long imageId);

    /**
     * Get image by ID and product ID
     */
    ProductImageDTO getImageByIdAndProductId(Long productId, Long imageId);
}