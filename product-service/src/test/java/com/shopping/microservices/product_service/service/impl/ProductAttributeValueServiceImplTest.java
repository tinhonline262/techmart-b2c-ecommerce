package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.ProductAttributeValueDTO;
import com.shopping.microservices.product_service.entity.ProductAttribute;
import com.shopping.microservices.product_service.entity.ProductAttributeGroup;
import com.shopping.microservices.product_service.entity.ProductAttributeValue;
import com.shopping.microservices.product_service.mapper.ProductAttributeValueMapper;
import com.shopping.microservices.product_service.repository.ProductAttributeValueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductAttributeValueServiceImplTest {

    @Mock
    private ProductAttributeValueRepository valueRepository;

    @Mock
    private ProductAttributeValueMapper valueMapper;

    @InjectMocks
    private ProductAttributeValueServiceImpl valueService;

    private ProductAttributeValue attributeValue;
    private ProductAttributeValueDTO valueDTO;

    @BeforeEach
    void setUp() {
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Clothing Attributes");

        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Material");
        attribute.setProductAttributeGroup(group);

        attributeValue = new ProductAttributeValue();
        attributeValue.setId(1L);
        attributeValue.setProductAttribute(attribute);
        attributeValue.setValue("Cotton");

        valueDTO = new ProductAttributeValueDTO(1L, "Material", "Clothing Attributes", "Cotton");
    }

    @Test
    void getAttributeValues_returnsList() {
        // Arrange
        List<ProductAttributeValue> values = List.of(attributeValue);
        when(valueRepository.findByProductAttributeId(1L)).thenReturn(values);
        when(valueMapper.toDTO(attributeValue)).thenReturn(valueDTO);

        // Act
        List<ProductAttributeValueDTO> result = valueService.getAttributeValues(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).value()).isEqualTo("Cotton");
        verify(valueRepository).findByProductAttributeId(1L);
        verify(valueMapper).toDTO(attributeValue);
    }

    @Test
    void getAttributeValues_returnsEmptyListWhenNoValues() {
        // Arrange
        when(valueRepository.findByProductAttributeId(999L)).thenReturn(List.of());

        // Act
        List<ProductAttributeValueDTO> result = valueService.getAttributeValues(999L);

        // Assert
        assertThat(result).isEmpty();
        verify(valueRepository).findByProductAttributeId(999L);
        verify(valueMapper, never()).toDTO(any());
    }
}
