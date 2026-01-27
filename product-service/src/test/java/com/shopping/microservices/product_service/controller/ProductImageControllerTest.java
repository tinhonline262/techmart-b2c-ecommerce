package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.ProductImageDTO;
import com.shopping.microservices.product_service.service.ProductImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for ProductImageController
 * Tests file upload, retrieval, and deletion functionality
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("ProductImageController API Tests")
class ProductImageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductImageService productImageService;

    private ProductImageDTO testImageDTO;
    private MockMultipartFile testImageFile;
    private Long testProductId = 1L;
    private Long testImageId = 100L;

    @BeforeEach
    void setUp() {
        // Setup test data
        testImageDTO = new ProductImageDTO(
                testImageId,
                testProductId,
                "https://res.cloudinary.com/dp4juv71c/image/upload/v1234567890/test_image.jpg",
                "products/test_image",
                "Test product image",
                false,
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // Create test image file
        testImageFile = new MockMultipartFile(
                "file",
                "test-image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "fake image content".getBytes()
        );
    }

    // ======================= UPLOAD SINGLE IMAGE TESTS =======================

    @Test
    @DisplayName("Upload single image - Success")
    @WithMockUser(roles = "SELLER")
    void testUploadImage_Success() throws Exception {
        // Arrange
        when(productImageService.uploadProductImage(eq(testProductId), any()))
                .thenReturn(testImageDTO);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/products/{productId}/images/upload", testProductId)
                .file(testImageFile)
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testImageId))
                .andExpect(jsonPath("$.productId").value(testProductId))
                .andExpect(jsonPath("$.imageUrl").exists())
                .andExpect(jsonPath("$.cloudinaryPublicId").exists());

        verify(productImageService, times(1)).uploadProductImage(eq(testProductId), any());
    }

    @Test
    @DisplayName("Upload single image - Admin role")
    @WithMockUser(roles = "ADMIN")
    void testUploadImage_AdminRole() throws Exception {
        // Arrange
        when(productImageService.uploadProductImage(eq(testProductId), any()))
                .thenReturn(testImageDTO);

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/products/{productId}/images/upload", testProductId)
                .file(testImageFile)
                .with(csrf()))
                .andExpect(status().isCreated());

        verify(productImageService, times(1)).uploadProductImage(eq(testProductId), any());
    }

    // ======================= UPLOAD MULTIPLE IMAGES TESTS =======================

    @Test
    @DisplayName("Upload multiple images - Success")
    @WithMockUser(roles = "SELLER")
    void testUploadMultipleImages_Success() throws Exception {
        // Arrange
        MockMultipartFile file1 = new MockMultipartFile("files", "image1.jpg",
                MediaType.IMAGE_JPEG_VALUE, "fake image 1".getBytes());
        MockMultipartFile file2 = new MockMultipartFile("files", "image2.jpg",
                MediaType.IMAGE_JPEG_VALUE, "fake image 2".getBytes());

        ProductImageDTO secondImage = new ProductImageDTO(
                101L,
                testProductId,
                "https://res.cloudinary.com/dp4juv71c/image/upload/v1234567891/test_image2.jpg",
                "products/test_image2",
                "Test product image 2",
                false,
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(productImageService.uploadProductImages(eq(testProductId), any()))
                .thenReturn(List.of(testImageDTO, secondImage));

        // Act & Assert
        mockMvc.perform(multipart("/api/v1/products/{productId}/images/upload-multiple", testProductId)
                .file(file1)
                .file(file2)
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(testImageId))
                .andExpect(jsonPath("$[1].id").value(101L));

        verify(productImageService, times(1)).uploadProductImages(eq(testProductId), any());
    }

    // ======================= GET IMAGES TESTS =======================

    @Test
    @DisplayName("Get all images for product - Success")
    void testGetProductImages_Success() throws Exception {
        // Arrange
        ProductImageDTO secondImage = new ProductImageDTO(
                101L,
                testProductId,
                "https://res.cloudinary.com/dp4juv71c/image/upload/v1234567891/test_image2.jpg",
                "products/test_image2",
                "Test product image 2",
                false,
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        when(productImageService.getProductImages(testProductId))
                .thenReturn(List.of(testImageDTO, secondImage));

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/{productId}/images", testProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(testImageId))
                .andExpect(jsonPath("$[0].productId").value(testProductId))
                .andExpect(jsonPath("$[1].id").value(101L));

        verify(productImageService, times(1)).getProductImages(testProductId);
    }

    @Test
    @DisplayName("Get all images - No images found")
    void testGetProductImages_Empty() throws Exception {
        // Arrange
        when(productImageService.getProductImages(testProductId))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/{productId}/images", testProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(productImageService, times(1)).getProductImages(testProductId);
    }

    @Test
    @DisplayName("Get specific image - Success")
    void testGetImageById_Success() throws Exception {
        // Arrange
        when(productImageService.getImageByIdAndProductId(testProductId, testImageId))
                .thenReturn(testImageDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/products/{productId}/images/{imageId}", testProductId, testImageId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(testImageId))
                .andExpect(jsonPath("$.productId").value(testProductId))
                .andExpect(jsonPath("$.imageUrl").exists());

        verify(productImageService, times(1)).getImageByIdAndProductId(testProductId, testImageId);
    }

    // ======================= DELETE IMAGE TESTS =======================

    @Test
    @DisplayName("Delete image - Success")
    @WithMockUser(roles = "SELLER")
    void testDeleteImage_Success() throws Exception {
        // Arrange
        doNothing().when(productImageService).deleteProductImage(testProductId, testImageId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/products/{productId}/images/{imageId}", testProductId, testImageId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(productImageService, times(1)).deleteProductImage(testProductId, testImageId);
    }

    @Test
    @DisplayName("Delete image - Admin role")
    @WithMockUser(roles = "ADMIN")
    void testDeleteImage_AdminRole() throws Exception {
        // Arrange
        doNothing().when(productImageService).deleteProductImage(testProductId, testImageId);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/products/{productId}/images/{imageId}", testProductId, testImageId)
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(productImageService, times(1)).deleteProductImage(testProductId, testImageId);
    }
}
