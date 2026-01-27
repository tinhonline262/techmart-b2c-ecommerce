package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.ApiResponse;
import com.shopping.microservices.product_service.dto.ProductImageDTO;
import com.shopping.microservices.product_service.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products/images/{productId}")
@RequiredArgsConstructor
@Slf4j
public class ProductImageController {

    private final ProductImageService productImageService;

    /**
     * Upload a single image for a product
     * @param productId the product ID
     * @param file the image file to upload
     * @return uploaded image details
     */
    @PostMapping("/upload")  // ← Thêm {productId}
    public ResponseEntity<ApiResponse<ProductImageDTO>> uploadImage(
            @PathVariable Long productId,
            @RequestParam("file") MultipartFile file) throws IOException {

        log.info("Received request to upload image for product ID: {}", productId);

        ProductImageDTO uploadedImage = productImageService.uploadProductImage(productId, file);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.<ProductImageDTO>builder()
                        .message("Image uploaded successfully")
                        .data(uploadedImage)
                        .build());
    }

    /**
     * Upload multiple images for a product
     * @param productId the product ID
     * @param files the list of image files to upload
     * @return list of uploaded images
     */
    @PostMapping("/upload-multiple")
    public ResponseEntity<List<ProductImageDTO>> uploadImages(
            @PathVariable Long productId,
            @RequestParam(required = true) List<MultipartFile> files) throws IOException {

        log.info("Received request to upload {} images for product ID: {}", files.size(), productId);

        List<ProductImageDTO> uploadedImages = productImageService.uploadProductImages(productId, files);

        return ResponseEntity.status(HttpStatus.CREATED).body(uploadedImages);
    }

    /**
     * Get all images for a product
     * @param productId the product ID
     * @return list of product images
     */
    @GetMapping
    public ResponseEntity<List<ProductImageDTO>> getProductImages(
            @PathVariable Long productId) {

        log.info("Received request to get images for product ID: {}", productId);

        List<ProductImageDTO> images = productImageService.getProductImages(productId);

        return ResponseEntity.ok(images);
    }

    /**
     * Delete an image from a product
     * @param productId the product ID
     * @param imageId the image ID to delete
     * @return no content response
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteProductImage(
            @PathVariable Long productId,
            @PathVariable Long imageId) throws IOException {

        log.info("Received request to delete image ID: {} for product ID: {}", imageId, productId);

        productImageService.deleteProductImage(productId, imageId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Get a specific image by ID (must belong to the product)
     * @param productId the product ID
     * @param imageId the image ID
     * @return image details
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<ProductImageDTO> getImageById(
            @PathVariable Long productId,
            @PathVariable Long imageId) {

        log.info("Received request to get image ID: {} for product ID: {}", imageId, productId);

        ProductImageDTO image = productImageService.getImageByIdAndProductId(productId, imageId);

        return ResponseEntity.ok(image);
    }
}
