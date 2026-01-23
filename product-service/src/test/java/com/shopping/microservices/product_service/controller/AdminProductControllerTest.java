package com.shopping.microservices.product_service.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = AdminProductController.class, excludeAutoConfiguration = {
        org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration.class,
        org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration.class,
        org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration.class
})
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
public class AdminProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

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
    void createProduct_returnsCreated() throws Exception {
        var creation = new ProductCreationDTO(
                "Test",
                null,
                "SKU-1",
                null,
                null,
                null,
                BigDecimal.valueOf(9.99),
                null,
                null,
                null,
                Integer.valueOf(10),
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                null, // brandId
                List.<Long>of(),
                "metaTitle",
                "metaDesc",
                "metaKey",
                null,
                null,
                null,
                List.<com.shopping.microservices.product_service.dto.ProductImageCreationDTO>of()
        );

        Mockito.when(productService.createProduct(any(ProductCreationDTO.class))).thenReturn(sampleProduct());

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(creation)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void getProductById_returnsProduct() throws Exception {
        Mockito.when(productService.findById(1L)).thenReturn(sampleProduct());

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.message").value("Product retrieved successfully"));
    }

    @Test
    void updateProduct_returnsUpdated() throws Exception {
        var update = new ProductUpdateDTO(
                "Updated",
                null,
                null,
                null,
                null,
                null,
                BigDecimal.valueOf(12.5),
                null,
                null,
                null,
                Integer.valueOf(20),
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                Boolean.TRUE,
                null,
                List.<Long>of(),
                null,
                null,
                null,
                null,
                null,
                null
        );
        Mockito.when(productService.updateProduct(eq(1L), any(ProductUpdateDTO.class))).thenReturn(sampleProduct());

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void deleteProduct_returnsNoContent() throws Exception {
        Mockito.doNothing().when(productService).deleteProduct(1L);

        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void updateProductQuantity_returnsUpdated() throws Exception {
        var inv = new InventoryUpdateDTO(50, "restock");
        Mockito.when(productService.updateProductQuantity(eq(1L), any(InventoryUpdateDTO.class))).thenReturn(sampleProduct());

        mockMvc.perform(put("/api/v1/products/1/quantity")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inv)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void subtractProductQuantity_returnsUpdated() throws Exception {
        var inv = new InventorySubtractDTO(2, "ORD-1", "order");
        Mockito.when(productService.subtractProductQuantity(eq(1L), any(InventorySubtractDTO.class))).thenReturn(sampleProduct());

        mockMvc.perform(post("/api/v1/products/1/quantity/subtract")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inv)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(1));
    }
}