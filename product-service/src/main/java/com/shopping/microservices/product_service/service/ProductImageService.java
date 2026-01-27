package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.ProductImageCreationDTO;
import com.shopping.microservices.product_service.dto.ProductImageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductImageService {
    
    /**
     * Upload a single image for a product
     * @param productId the product ID
     * @param file the image file to upload
     * @return ProductImageDTO containing uploaded image details
     * @throws IOException if upload fails
     */
    ProductImageDTO uploadProductImage(Long productId, MultipartFile file) throws IOException;
    
    /**
     * Upload multiple images for a product
     * @param productId the product ID
     * @param files the list of image files to upload
     * @return list of ProductImageDTO for uploaded images
     * @throws IOException if any upload fails
     */
    List<ProductImageDTO> uploadProductImages(Long productId, List<MultipartFile> files) throws IOException;
    
    /**
     * Get all images for a product
     * @param productId the product ID
     * @return list of ProductImageDTO for the product
     */
    List<ProductImageDTO> getProductImages(Long productId);
    
    /**
     * Delete an image from a product
     * @param productId the product ID
     * @param imageId the image ID to delete
     * @throws IOException if deletion fails
     */
    void deleteProductImage(Long productId, Long imageId) throws IOException;
    
    /**
     * Get an image by ID
     * @param imageId the image ID
     * @return ProductImageDTO if found
     */
    ProductImageDTO getImageById(Long imageId);
    
    /**
     * Get an image by ID and verify it belongs to the product
     * @param productId the product ID
     * @param imageId the image ID
     * @return ProductImageDTO if found and belongs to product
     */
    ProductImageDTO getImageByIdAndProductId(Long productId, Long imageId);
}
