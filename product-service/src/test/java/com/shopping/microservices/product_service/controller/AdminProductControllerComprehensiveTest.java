//package com.shopping.microservices.product_service.controller;
//
//import com.shopping.microservices.product_service.client.InventoryServiceClient;
//import com.shopping.microservices.product_service.dto.*;
//import com.shopping.microservices.product_service.dto.brand.BrandDTO;
//import com.shopping.microservices.product_service.dto.category.CategoryDTO;
//import com.shopping.microservices.product_service.service.*;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("AdminProductController Comprehensive Tests")
//class AdminProductControllerComprehensiveTest {
//
//    @Mock
//    private ProductService productService;
//
//    @Mock
//    private ProductOptionService productOptionService;
//
//    @Mock
//    private ProductOptionValueService productOptionValueService;
//
//    @Mock
//    private ProductAttributeService productAttributeService;
//
//    @Mock
//    private ProductAttributeValueService productAttributeValueService;
//
//    @Mock
//    private ProductAttributeGroupService productAttributeGroupService;
//
//    @Mock
//    private InventoryServiceClient inventoryServiceClient;
//
//    @InjectMocks
//    private AdminProductController controller;
//
//    // Test data builders
//    private ProductDTO createProductDTO(Long id, String name, String sku, BigDecimal price,
//                                         Integer stockQty, boolean isPublished, boolean isFeatured, Long categoryId, Long brandId) {
//        return new ProductDTO(
//                id, name, name.toLowerCase().replace(" ", "-"), sku,
//                "Short description", "Detailed description", "Specification",
//                price, null, null, BigDecimal.valueOf(50.00),
//                stockQty, true, true, isPublished, isFeatured, true,
//                new BrandDTO(brandId, "Brand " + brandId, "brand-" + brandId, true),
//                categoryId != null ? List.of(new CategoryDTO(categoryId, "Category " + categoryId, "cat-" + categoryId, "Desc", null, null, true, null, null)) : List.of(),
//                List.of(), "Meta Title", "Meta Description", "keywords",
//                null, BigDecimal.valueOf(1.5), "10x10x10",
//                LocalDateTime.now(), LocalDateTime.now()
//        );
//    }
//
//    private ProductOptionValueDTO createProductOptionValueDTO(Long id, Long optionId, String value) {
//        return new ProductOptionValueDTO(
//                id, optionId, "Option " + optionId, value, "SELECT", 1,
//                LocalDateTime.now(), LocalDateTime.now()
//        );
//    }
//
//    // ==================== GET PRODUCTS TESTS ====================
//    @Nested
//    @DisplayName("GET /api/v1/products - getProducts()")
//    class GetProductsTests {
//
//        @Test
//        @DisplayName("Should return all products with pagination when no filters provided")
//        void testGetProducts_NoFilters_ReturnAll() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//            ProductDTO product2 = createProductDTO(2L, "Product 2", "SKU002", BigDecimal.valueOf(200), 30, true, false, 2L, 2L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1, product2));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.getProducts(
//                    null, null, null, null, null, null, null, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody()).isNotNull();
//            assertThat(response.getBody().getData().content()).hasSize(2);
//        }
//
//        @Test
//        @DisplayName("Should filter products by ids")
//        void testGetProducts_FilterByIds() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1, createProductDTO(2L, "Product 2", "SKU002", BigDecimal.valueOf(200), 30, true, false, 2L, 2L)));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.getProducts(
//                    Arrays.asList(1L), null, null, null, null, null, null, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should filter products by categoryIds")
//        void testGetProducts_FilterByCategories() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.getProducts(
//                    null, Arrays.asList(1L), null, null, null, null, null, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should filter products by brandIds")
//        void testGetProducts_FilterByBrandIds() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.getProducts(
//                    null, null, Arrays.asList(1L), null, null, null, null, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should filter products by keyword")
//        void testGetProducts_FilterByKeyword() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.getProducts(
//                    null, null, null, "Product", null, null, null, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should filter products by price range")
//        void testGetProducts_FilterByPriceRange() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(150), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.getProducts(
//                    null, null, null, null, BigDecimal.valueOf(100), BigDecimal.valueOf(200), null, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should filter products by isPublished status")
//        void testGetProducts_FilterByPublished() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.getProducts(
//                    null, null, null, null, null, null, true, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should filter products by isFeatured status")
//        void testGetProducts_FilterByFeatured() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.getProducts(
//                    null, null, null, null, null, null, null, true, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should filter products by inStock status")
//        void testGetProducts_FilterByInStock() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.getProducts(
//                    null, null, null, null, null, null, null, null, true, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should return empty list when filters match no products")
//        void testGetProducts_EmptyResult() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            when(productService.findAll()).thenReturn(Collections.emptyList());
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.getProducts(
//                    Arrays.asList(999L), null, null, null, null, null, null, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData().isEmpty()).isTrue();
//        }
//
//        @Test
//        @DisplayName("Should handle pagination correctly")
//        void testGetProducts_Pagination() {
//            // Arrange
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//            ProductDTO product2 = createProductDTO(2L, "Product 2", "SKU002", BigDecimal.valueOf(200), 30, true, false, 2L, 2L);
//            ProductDTO product3 = createProductDTO(3L, "Product 3", "SKU003", BigDecimal.valueOf(300), 20, true, true, 3L, 3L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1, product2, product3));
//
//            // Act
//            Pageable pageable = PageRequest.of(1, 2);
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.getProducts(
//                    null, null, null, null, null, null, null, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData().pageNumber()).isEqualTo(1);
//            assertThat(response.getBody().getData().pageSize()).isEqualTo(2);
//        }
//    }
//
//    // ==================== GET LATEST PRODUCTS TESTS ====================
//    @Nested
//    @DisplayName("GET /api/v1/products/latest - getLatestProducts()")
//    class GetLatestProductsTests {
//
//        @Test
//        @DisplayName("Should return latest products sorted by createdAt descending")
//        void testGetLatestProducts_WithLimit() {
//            // Arrange
//            ProductDTO product1 = createProductDTO(1L, "Latest Product", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//            ProductDTO product2 = createProductDTO(2L, "Older Product", "SKU002", BigDecimal.valueOf(200), 30, true, false, 2L, 2L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1, product2));
//
//            // Act
//            ResponseEntity<ApiResponse<List<ProductDTO>>> response = controller.getLatestProducts(10);
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData()).isNotEmpty();
//        }
//
//        @Test
//        @DisplayName("Should respect limit parameter")
//        void testGetLatestProducts_RespectLimit() {
//            // Arrange
//            ProductDTO product1 = createProductDTO(1L, "Latest Product", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//            ProductDTO product2 = createProductDTO(2L, "Product 2", "SKU002", BigDecimal.valueOf(200), 30, true, false, 2L, 2L);
//            ProductDTO product3 = createProductDTO(3L, "Product 3", "SKU003", BigDecimal.valueOf(300), 20, true, true, 3L, 3L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1, product2, product3));
//
//            // Act
//            ResponseEntity<ApiResponse<List<ProductDTO>>> response = controller.getLatestProducts(2);
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should return empty list when no products available")
//        void testGetLatestProducts_NoProducts_ReturnsEmpty() {
//            // Arrange
//            when(productService.findAll()).thenReturn(Collections.emptyList());
//
//            // Act
//            ResponseEntity<ApiResponse<List<ProductDTO>>> response = controller.getLatestProducts(10);
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData()).isEmpty();
//        }
//
//        @Test
//        @DisplayName("Should use default limit when not provided")
//        void testGetLatestProducts_DefaultLimit_Success() {
//            // Arrange
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<List<ProductDTO>>> response = controller.getLatestProducts(10);
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//    }
//
//    // ==================== SEARCH PRODUCTS TESTS ====================
//    @Nested
//    @DisplayName("GET /api/v1/products/search - searchProducts()")
//    class SearchProductsTests {
//
//        @Test
//        @DisplayName("Should search products by keyword")
//        void testSearchProducts_ByKeyword() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Laptop Computer", "SKU001", BigDecimal.valueOf(1000), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.searchProducts(
//                    "Laptop", null, null, null, null, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData().content()).isNotEmpty();
//        }
//
//        @Test
//        @DisplayName("Should search products by categories")
//        void testSearchProducts_ByCategory() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.searchProducts(
//                    null, Arrays.asList(1L), null, null, null, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should search products by brands")
//        void testSearchProducts_ByBrands() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.searchProducts(
//                    null, null, Arrays.asList(1L), null, null, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should search products by price range")
//        void testSearchProducts_ByPriceRange() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(150), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.searchProducts(
//                    null, null, null, BigDecimal.valueOf(100), BigDecimal.valueOf(200), null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should search products by inStock status")
//        void testSearchProducts_ByInStock() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.searchProducts(
//                    null, null, null, null, null, true, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should search products by isPublished status")
//        void testSearchProducts_ByPublished() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.searchProducts(
//                    null, null, null, null, null, null, true, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should search products with combined filters")
//        void testSearchProducts_CombinedFilters() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            ProductDTO product1 = createProductDTO(1L, "Laptop Computer", "SKU001", BigDecimal.valueOf(1000), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.searchProducts(
//                    "Laptop", Arrays.asList(1L), Arrays.asList(1L), BigDecimal.valueOf(900), BigDecimal.valueOf(1100), true, true, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should return empty results when no products match search")
//        void testSearchProducts_NoMatches() {
//            // Arrange
//            Pageable pageable = PageRequest.of(0, 10);
//            when(productService.findAll()).thenReturn(Collections.emptyList());
//
//            // Act
//            ResponseEntity<ApiResponse<PageResponseDTO<ProductDTO>>> response = controller.searchProducts(
//                    "NonExistent", null, null, null, null, null, null, pageable
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData().isEmpty()).isTrue();
//        }
//    }
//
//    // ==================== EXPORT PRODUCTS TESTS ====================
//    @Nested
//    @DisplayName("GET /api/v1/products/export - exportProducts()")
//    class ExportProductsTests {
//
//        @Test
//        @DisplayName("Should export all products to CSV format")
//        void testExportProducts_AllProducts() {
//            // Arrange
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//            ProductDTO product2 = createProductDTO(2L, "Product 2", "SKU002", BigDecimal.valueOf(200), 30, true, false, 2L, 2L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1, product2));
//
//            // Act
//            ResponseEntity<ApiResponse<byte[]>> response = controller.exportProducts(
//                    null, null, null, "csv"
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData()).isNotEmpty();
//        }
//
//        @Test
//        @DisplayName("Should export products filtered by ids")
//        void testExportProducts_FilterByIds() {
//            // Arrange
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1, createProductDTO(2L, "Product 2", "SKU002", BigDecimal.valueOf(200), 30, true, false, 2L, 2L)));
//
//            // Act
//            ResponseEntity<ApiResponse<byte[]>> response = controller.exportProducts(
//                    Arrays.asList(1L), null, null, "csv"
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should export products filtered by categories")
//        void testExportProducts_FilterByCategory() {
//            // Arrange
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<byte[]>> response = controller.exportProducts(
//                    null, Arrays.asList(1L), null, "csv"
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should export products filtered by brands")
//        void testExportProducts_FilterByBrands() {
//            // Arrange
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<byte[]>> response = controller.exportProducts(
//                    null, null, Arrays.asList(1L), "csv"
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should export products with combined filters")
//        void testExportProducts_CombinedFilters() {
//            // Arrange
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<byte[]>> response = controller.exportProducts(
//                    Arrays.asList(1L), Arrays.asList(1L), Arrays.asList(1L), "csv"
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should export empty list when no products match filters")
//        void testExportProducts_EmptyList() {
//            // Arrange
//            when(productService.findAll()).thenReturn(Collections.emptyList());
//
//            // Act
//            ResponseEntity<ApiResponse<byte[]>> response = controller.exportProducts(
//                    Arrays.asList(999L), null, null, "csv"
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//
//        @Test
//        @DisplayName("Should use default CSV format when format parameter not provided")
//        void testExportProducts_DefaultFormat_Success() {
//            // Arrange
//            ProductDTO product1 = createProductDTO(1L, "Product 1", "SKU001", BigDecimal.valueOf(100), 50, true, true, 1L, 1L);
//
//            when(productService.findAll()).thenReturn(Arrays.asList(product1));
//
//            // Act
//            ResponseEntity<ApiResponse<byte[]>> response = controller.exportProducts(
//                    null, null, null, "csv"
//            );
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        }
//    }
//
//    // ==================== GET OPTION VALUES TESTS ====================
//    @Nested
//    @DisplayName("GET /api/v1/products/option-values - getOptionValues()")
//    class GetOptionValuesTests {
//
//        @Test
//        @DisplayName("Should retrieve option values for given optionId")
//        void testGetOptionValues_ValidId() {
//            // Arrange
//            Long optionId = 1L;
//            ProductOptionValueDTO value1 = createProductOptionValueDTO(1L, optionId, "Red");
//            ProductOptionValueDTO value2 = createProductOptionValueDTO(2L, optionId, "Blue");
//
//            when(productOptionValueService.getOptionValues(optionId))
//                    .thenReturn(Arrays.asList(value1, value2));
//
//            // Act
//            ResponseEntity<ApiResponse<List<ProductOptionValueDTO>>> response = controller.getOptionValues(optionId);
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData()).hasSize(2);
//        }
//
//        @Test
//        @DisplayName("Should return empty list when option has no values")
//        void testGetOptionValues_EmptyList() {
//            // Arrange
//            Long optionId = 1L;
//            when(productOptionValueService.getOptionValues(optionId))
//                    .thenReturn(Collections.emptyList());
//
//            // Act
//            ResponseEntity<ApiResponse<List<ProductOptionValueDTO>>> response = controller.getOptionValues(optionId);
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData()).isEmpty();
//        }
//
//        @Test
//        @DisplayName("Should retrieve multiple option values")
//        void testGetOptionValues_MultipleValues() {
//            // Arrange
//            Long optionId = 1L;
//            ProductOptionValueDTO value1 = createProductOptionValueDTO(1L, optionId, "Small");
//            ProductOptionValueDTO value2 = createProductOptionValueDTO(2L, optionId, "Medium");
//            ProductOptionValueDTO value3 = createProductOptionValueDTO(3L, optionId, "Large");
//            ProductOptionValueDTO value4 = createProductOptionValueDTO(4L, optionId, "X-Large");
//
//            when(productOptionValueService.getOptionValues(optionId))
//                    .thenReturn(Arrays.asList(value1, value2, value3, value4));
//
//            // Act
//            ResponseEntity<ApiResponse<List<ProductOptionValueDTO>>> response = controller.getOptionValues(optionId);
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData()).hasSize(4);
//        }
//
//        @Test
//        @DisplayName("Should retrieve option values with special characters")
//        void testGetOptionValues_SpecialCharacters() {
//            // Arrange
//            Long optionId = 1L;
//            ProductOptionValueDTO value1 = new ProductOptionValueDTO(1L, optionId, "Type", "Premium (High Quality)", "SELECT", 1, LocalDateTime.now(), LocalDateTime.now());
//            ProductOptionValueDTO value2 = new ProductOptionValueDTO(2L, optionId, "Type", "Standard & Basic", "SELECT", 2, LocalDateTime.now(), LocalDateTime.now());
//
//            when(productOptionValueService.getOptionValues(optionId))
//                    .thenReturn(Arrays.asList(value1, value2));
//
//            // Act
//            ResponseEntity<ApiResponse<List<ProductOptionValueDTO>>> response = controller.getOptionValues(optionId);
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData()).hasSize(2);
//        }
//
//        @Test
//        @DisplayName("Should retrieve option values for large optionIds")
//        void testGetOptionValues_LargeOptionId() {
//            // Arrange
//            Long optionId = 999999L;
//            ProductOptionValueDTO value1 = createProductOptionValueDTO(1L, optionId, "Value 1");
//
//            when(productOptionValueService.getOptionValues(optionId))
//                    .thenReturn(Arrays.asList(value1));
//
//            // Act
//            ResponseEntity<ApiResponse<List<ProductOptionValueDTO>>> response = controller.getOptionValues(optionId);
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData()).hasSize(1);
//        }
//
//        @Test
//        @DisplayName("Should handle negative optionId parameter gracefully")
//        void testGetOptionValues_NegativeOptionId() {
//            // Arrange
//            Long optionId = -1L;
//
//            when(productOptionValueService.getOptionValues(optionId))
//                    .thenReturn(Collections.emptyList());
//
//            // Act
//            ResponseEntity<ApiResponse<List<ProductOptionValueDTO>>> response = controller.getOptionValues(optionId);
//
//            // Assert
//            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//            assertThat(response.getBody().getData()).hasSize(0);
//        }
//    }
//}
