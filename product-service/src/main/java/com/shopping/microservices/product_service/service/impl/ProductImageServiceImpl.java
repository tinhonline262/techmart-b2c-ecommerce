package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.ProductImageDTO;
import com.shopping.microservices.product_service.entity.Product;
import com.shopping.microservices.product_service.entity.ProductImage;
import com.shopping.microservices.product_service.exception.ErrorCode;
import com.shopping.microservices.product_service.exception.ImageUploadException;
import com.shopping.microservices.product_service.exception.ProductNotFoundException;
import com.shopping.microservices.product_service.mapper.ProductImageMapper;
import com.shopping.microservices.product_service.repository.ProductImageRepository;
import com.shopping.microservices.product_service.repository.ProductRepository;
import com.shopping.microservices.product_service.service.CloudinaryService;
import com.shopping.microservices.product_service.service.ProductImageService;
import com.shopping.microservices.product_service.validator.ImageUploadValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final CloudinaryService cloudinaryService;
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;
    private final ImageUploadValidator imageUploadValidator;

    /**
     * ✅ NEW METHOD: Upload image without product association
     * Used in Add Product page where product doesn't exist yet
     */
    @Override
    public ProductImageDTO uploadImage(MultipartFile file) throws IOException {
        log.info("Uploading image without product association");

        // Validate file
        imageUploadValidator.validateImageFile(file);

        try {
            // Upload to Cloudinary (temporary folder)
            String folderName = "products/temp";
            Map uploadResult = cloudinaryService.uploadFile(file, folderName);

            // Extract Cloudinary metadata
            String publicId = (String) uploadResult.get("public_id");
            String secureUrl = (String) uploadResult.get("secure_url");

            log.info("File uploaded successfully. Public ID: {}, URL: {}", publicId, secureUrl);

            // Return DTO without saving to database (no product yet)
            // Using record constructor or builder pattern
            return new ProductImageDTO(
                    null,  // id - not saved yet
                    null,  // productId - not associated yet
                    secureUrl,  // imageUrl
                    publicId,   // cloudinaryPublicId
                    file.getOriginalFilename(),  // altText
                    false,  // isPrimary
                    0,  // displayOrder
                    LocalDateTime.now(),  // createdAt
                    LocalDateTime.now()   // updatedAt
            );

        } catch (IOException ex) {
            log.error("Failed to upload temporary image", ex);
            throw new ImageUploadException(
                    ErrorCode.IMAGE_UPLOAD_FAILED,
                    "Failed to upload image: " + ex.getMessage()
            );
        }
    }

    @Override
    public ProductImageDTO uploadProductImage(Long productId, MultipartFile file) throws IOException {
        log.info("Uploading single image for product ID: {}", productId);

        // Validate file
        imageUploadValidator.validateImageFile(file);

        // Find product
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", productId);
                    return new ProductNotFoundException(productId);
                });

        try {
            // Upload to Cloudinary
            String folderName = "products/" + productId;
            Map uploadResult = cloudinaryService.uploadFile(file, folderName);

            // Extract Cloudinary metadata
            String publicId = (String) uploadResult.get("public_id");
            String secureUrl = (String) uploadResult.get("secure_url");

            log.info("File uploaded successfully. Public ID: {}, URL: {}", publicId, secureUrl);

            // Create ProductImage entity
            ProductImage productImage = ProductImage.builder()
                    .product(product)
                    .imageUrl(secureUrl)
                    .cloudinaryPublicId(publicId)
                    .altText(file.getOriginalFilename())
                    .isPrimary(false)
                    .displayOrder(0)
                    .build();

            // Save to database
            ProductImage savedImage = productImageRepository.save(productImage);
            log.info("Product image saved to database with ID: {}", savedImage.getId());

            return productImageMapper.toDTO(savedImage);

        } catch (IOException ex) {
            log.error("Failed to upload file for product ID: {}", productId, ex);
            throw new ImageUploadException(
                    ErrorCode.IMAGE_UPLOAD_FAILED,
                    "Failed to upload image: " + ex.getMessage()
            );
        }
    }

    @Override
    public List<ProductImageDTO> uploadProductImages(Long productId, List<MultipartFile> files) throws IOException {
        log.info("Uploading {} images for product ID: {}", files.size(), productId);

        List<ProductImageDTO> uploadedImages = new ArrayList<>();

        for (int index = 0; index < files.size(); index++) {
            try {
                ProductImageDTO imageDTO = uploadProductImage(productId, files.get(index));
                uploadedImages.add(imageDTO);
                log.info("Image {}/{} uploaded successfully", index + 1, files.size());
            } catch (Exception ex) {
                log.warn("Failed to upload image {}/{}: {}", index + 1, files.size(), ex.getMessage());
                // Continue uploading remaining files
            }
        }

        if (uploadedImages.isEmpty()) {
            log.error("No images were successfully uploaded for product ID: {}", productId);
            throw new ImageUploadException(
                    ErrorCode.IMAGE_UPLOAD_FAILED,
                    "Failed to upload any images"
            );
        }

        log.info("Successfully uploaded {} out of {} images", uploadedImages.size(), files.size());
        return uploadedImages;
    }

    @Override
    public List<ProductImageDTO> getProductImages(Long productId) {
        log.info("Fetching images for product ID: {}", productId);

        // Verify product exists
        productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", productId);
                    return new ProductNotFoundException(productId);
                });

        List<ProductImage> images = productImageRepository.findByProductId(productId);
        log.info("Found {} images for product ID: {}", images.size(), productId);

        return images.stream()
                .map(productImageMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteProductImage(Long productId, Long imageId) throws IOException {
        log.info("Deleting image ID: {} for product ID: {}", imageId, productId);

        // Verify product exists
        productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", productId);
                    return new ProductNotFoundException(productId);
                });

        // Find image
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> {
                    log.warn("Image not found with ID: {}", imageId);
                    return new ImageUploadException(
                            ErrorCode.IMAGE_NOT_FOUND,
                            "Image not found with ID: " + imageId
                    );
                });

        // Verify image belongs to the product
        if (!productImage.getProduct().getId().equals(productId)) {
            log.warn("Image ID: {} does not belong to product ID: {}", imageId, productId);
            throw new ImageUploadException(
                    ErrorCode.IMAGE_NOT_FOUND,
                    "Image does not belong to this product"
            );
        }

        try {
            // Delete from Cloudinary if public ID exists
            if (productImage.getCloudinaryPublicId() != null) {
                cloudinaryService.deleteFile(productImage.getCloudinaryPublicId());
                log.info("File deleted from Cloudinary with public ID: {}", productImage.getCloudinaryPublicId());
            }

            // Delete from database
            productImageRepository.deleteById(imageId);
            log.info("Product image deleted from database with ID: {}", imageId);

        } catch (IOException ex) {
            log.error("Failed to delete image ID: {} from Cloudinary", imageId, ex);
            throw new ImageUploadException(
                    ErrorCode.IMAGE_DELETE_FAILED,
                    "Failed to delete image: " + ex.getMessage()
            );
        }
    }

    @Override
    public ProductImageDTO getImageById(Long imageId) {
        log.info("Fetching image by ID: {}", imageId);

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> {
                    log.warn("Image not found with ID: {}", imageId);
                    return new ImageUploadException(
                            ErrorCode.IMAGE_NOT_FOUND,
                            "Image not found with ID: " + imageId
                    );
                });

        return productImageMapper.toDTO(productImage);
    }

    @Override
    public ProductImageDTO getImageByIdAndProductId(Long productId, Long imageId) {
        log.info("Fetching image ID: {} for product ID: {}", imageId, productId);

        // Verify product exists
        productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.warn("Product not found with ID: {}", productId);
                    return new ProductNotFoundException(productId);
                });

        // Find image and verify it belongs to the product
        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> {
                    log.warn("Image not found with ID: {}", imageId);
                    return new ImageUploadException(
                            ErrorCode.IMAGE_NOT_FOUND,
                            "Image not found with ID: " + imageId
                    );
                });

        // Verify image belongs to the product
        if (!productImage.getProduct().getId().equals(productId)) {
            log.warn("Image ID: {} does not belong to product ID: {}", imageId, productId);
            throw new ImageUploadException(
                    ErrorCode.IMAGE_NOT_FOUND,
                    "Image does not belong to this product"
            );
        }

        return productImageMapper.toDTO(productImage);
    }
}