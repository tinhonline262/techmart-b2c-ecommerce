package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.entity.Category;
import com.shopping.microservices.product_service.entity.Product;
import com.shopping.microservices.product_service.entity.ProductCategory;
import com.shopping.microservices.product_service.exception.CategoryNotFoundException;
import com.shopping.microservices.product_service.mapper.ProductMapper;
import com.shopping.microservices.product_service.repository.*;
import com.shopping.microservices.product_service.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @Mock
    CategoryRepository categoryRepository;

    @Mock
    ProductCategoryRepository productCategoryRepository;

    @Mock
    ProductImageRepository productImageRepository;

    @Mock
    ProductMapper productMapper;

    @InjectMocks
    ProductServiceImpl productService;

    Product product;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Test Product")
                .sku("SKU123")
                .price(BigDecimal.valueOf(10.0))
                .stockQuantity(5L)
                .stockTrackingEnabled(true)
                .build();
    }

    @Test
    void createProduct_createsSuccessfully() {
        var creation = new ProductCreationDTO(
                "Test", // name
                null, // slug
                "SKU123", // sku
                null, // shortDescription
                null, // description
                null, // specification
                BigDecimal.valueOf(10), // price
                null, // oldPrice
                null, // specialPrice
                null, // cost
                5, // stockQuantity
                null, // stockTrackingEnabled
                null, // isAllowedToOrder
                null, // isPublished
                null, // isFeatured
                null, // isVisibleIndividually
                null, // brandId
                List.of(1L), // categoryIds
                null, // metaTitle
                null, // metaDescription
                null, // metaKeywords
                null, // thumbnailUrl
                null, // weight
                null, // dimensions
                null // images
        );

        when(productMapper.toEntity(any(ProductCreationDTO.class))).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(new Category(1L, "Cat", null, null, 0, null, null, false, null, null)));
        var mockedDto = mock(ProductDTO.class);
        when(mockedDto.id()).thenReturn(1L);
        when(productMapper.toDTO(any(Product.class))).thenReturn(mockedDto);

        var result = productService.createProduct(creation);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        verify(productCategoryRepository, times(1)).save(any(ProductCategory.class));
    }

    @Test
    void findById_returnsDTO() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        var mockedDto = mock(ProductDTO.class);
        when(mockedDto.id()).thenReturn(1L);
        when(productMapper.toDTO(product)).thenReturn(mockedDto);

        var dto = productService.findById(1L);
        assertThat(dto.id()).isEqualTo(1L);
    }

    @Test
    void updateProductQuantity_updatesSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        var mockedDto = mock(ProductDTO.class);
        when(mockedDto.stockQuantity()).thenReturn(10);
        when(productMapper.toDTO(any(Product.class))).thenReturn(mockedDto);

        var updated = productService.updateProductQuantity(1L, new InventoryUpdateDTO(10, "restock"));
        assertThat(updated.stockQuantity()).isEqualTo(10);
    }

    @Test
    void subtractProductQuantity_insufficientStock_throws() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.subtractProductQuantity(1L, new InventorySubtractDTO(10, "ord-1", "order")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient stock");
    }
}
