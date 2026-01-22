package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.service.ProductService;
import com.shopping.microservices.product_service.service.ProductOptionService;
import com.shopping.microservices.product_service.service.ProductOptionValueService;
import com.shopping.microservices.product_service.service.ProductAttributeService;
import com.shopping.microservices.product_service.service.ProductAttributeValueService;
import com.shopping.microservices.product_service.service.ProductAttributeGroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

public class AdminProductControllerUnitTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductOptionService productOptionService;

    @Mock
    private ProductOptionValueService productOptionValueService;

    @Mock
    private ProductAttributeService productAttributeService;

    @Mock
    private ProductAttributeValueService productAttributeValueService;

    @Mock
    private ProductAttributeGroupService productAttributeGroupService;

    private AdminProductController controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        controller = new AdminProductController(productService, productOptionService, productOptionValueService, productAttributeService, productAttributeValueService, productAttributeGroupService);
    }

    private ProductDTO sampleProduct() {
        return new ProductDTO(
                1L,
                "Test Product",
                "test-product",
                "SKU-1",
                "short",
                "desc",
                "spec",
                BigDecimal.valueOf(9.99),
                null,
                null,
                null,
                100,
                true,
                true,
                true,
                true,
                true,
                null,
                List.of(),
                List.of(),
                "metaTitle",
                "metaDesc",
                "metaKey",
                null,
                null,
                null,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void createProduct_returnsCreated() {
        var creation = new ProductCreationDTO("Test", null, "SKU-1", null, null, null, BigDecimal.valueOf(9.99), null, null, null, Integer.valueOf(10), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, null, List.of(), "metaTitle", "metaDesc", "metaKey", null, null, null, List.of());
        Mockito.when(productService.createProduct(any(ProductCreationDTO.class))).thenReturn(sampleProduct());

        ResponseEntity<ApiResponse<ProductDTO>> resp = controller.createProduct(creation);
        assertEquals(201, resp.getStatusCode().value());
        assertNotNull(resp.getBody());
        assertEquals(1L, resp.getBody().getData().id());
    }

    @Test
    void getProductById_returnsProduct() {
        Mockito.when(productService.findById(1L)).thenReturn(sampleProduct());

        ResponseEntity<ApiResponse<ProductDTO>> resp = controller.getProductById(1L);
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(1L, resp.getBody().getData().id());
    }

    @Test
    void updateProduct_returnsUpdated() {
        var update = new ProductUpdateDTO("Updated", null, null, null, null, null, BigDecimal.valueOf(12.5), null, null, null, Integer.valueOf(20), Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, Boolean.TRUE, null, List.of(), null, null, null, null, null, null);
        Mockito.when(productService.updateProduct(eq(1L), any(ProductUpdateDTO.class))).thenReturn(sampleProduct());

        ResponseEntity<ApiResponse<ProductDTO>> resp = controller.updateProduct(1L, update);
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(1L, resp.getBody().getData().id());
    }

    @Test
    void deleteProduct_returnsNoContent() {
        Mockito.doNothing().when(productService).deleteProduct(1L);

        ResponseEntity<ApiResponse<Void>> resp = controller.deleteProduct(1L);
        assertEquals(204, resp.getStatusCode().value());
    }

    @Test
    void updateProductQuantity_returnsUpdated() {
        var inv = new InventoryUpdateDTO(50, "restock");
        Mockito.when(productService.updateProductQuantity(eq(1L), any(InventoryUpdateDTO.class))).thenReturn(sampleProduct());

        ResponseEntity<ApiResponse<ProductDTO>> resp = controller.updateProductQuantity(1L, inv);
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(1L, resp.getBody().getData().id());
    }

    @Test
    void subtractProductQuantity_returnsUpdated() {
        var inv = new InventorySubtractDTO(2, "ORD-1", "order");
        Mockito.when(productService.subtractProductQuantity(eq(1L), any(InventorySubtractDTO.class))).thenReturn(sampleProduct());

        ResponseEntity<ApiResponse<ProductDTO>> resp = controller.subtractProductQuantity(1L, inv);
        assertEquals(200, resp.getStatusCode().value());
        assertEquals(1L, resp.getBody().getData().id());
    }
}