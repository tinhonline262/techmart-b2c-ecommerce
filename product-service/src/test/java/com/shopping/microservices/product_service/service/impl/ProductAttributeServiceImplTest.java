package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeUpdateDTO;
import com.shopping.microservices.product_service.entity.ProductAttribute;
import com.shopping.microservices.product_service.entity.ProductAttributeGroup;
import com.shopping.microservices.product_service.mapper.ProductAttributeMapper;
import com.shopping.microservices.product_service.repository.ProductAttributeGroupRepository;
import com.shopping.microservices.product_service.repository.ProductAttributeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductAttributeServiceImplTest {

    @Mock
    private ProductAttributeRepository attributeRepository;

    @Mock
    private ProductAttributeGroupRepository attributeGroupRepository;

    @Mock
    private ProductAttributeMapper attributeMapper;

    @InjectMocks
    private ProductAttributeServiceImpl attributeService;

    private ProductAttribute productAttribute;
    private ProductAttributeGroup attributeGroup;
    private ProductAttributeCreationDTO creationDTO;
    private ProductAttributeUpdateDTO updateDTO;
    private ProductAttributeDTO attributeDTO;

    @BeforeEach
    void setUp() {
        attributeGroup = new ProductAttributeGroup();
        attributeGroup.setId(1L);
        attributeGroup.setName("Clothing Attributes");
        attributeGroup.setCreatedAt(Instant.now());
        attributeGroup.setUpdatedAt(Instant.now());

        productAttribute = new ProductAttribute();
        productAttribute.setId(1L);
        productAttribute.setName("Material");
        productAttribute.setProductAttributeGroup(attributeGroup);
        productAttribute.setCreatedAt(Instant.now());
        productAttribute.setUpdatedAt(Instant.now());

        creationDTO = new ProductAttributeCreationDTO("Material", 1L);
        updateDTO = new ProductAttributeUpdateDTO("Fabric", 1L);
        attributeDTO = new ProductAttributeDTO(1L, "Material", 1L, "Clothing Attributes", null, null);
    }

    @Test
    void getAttributes_returnsPagedAttributes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductAttribute> attributes = List.of(productAttribute);
        Page<ProductAttribute> page = new PageImpl<>(attributes, pageable, 1);

        when(attributeRepository.findAll(pageable)).thenReturn(page);
        when(attributeMapper.toDTO(productAttribute)).thenReturn(attributeDTO);

        // Act
        PageResponseDTO<ProductAttributeDTO> result = attributeService.getAttributes(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.pageNumber()).isEqualTo(0);
        verify(attributeRepository).findAll(pageable);
        verify(attributeMapper).toDTO(productAttribute);
    }

    @Test
    void getAttributeById_returnsDTOWhenExists() {
        // Arrange
        when(attributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeMapper.toDTO(productAttribute)).thenReturn(attributeDTO);

        // Act
        ProductAttributeDTO result = attributeService.getAttributeById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Material");
        verify(attributeRepository).findById(1L);
        verify(attributeMapper).toDTO(productAttribute);
    }

    @Test
    void getAttributeById_throwsExceptionWhenNotFound() {
        // Arrange
        when(attributeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> attributeService.getAttributeById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product attribute not found");

        verify(attributeRepository).findById(999L);
        verify(attributeMapper, never()).toDTO(any());
    }

    @Test
    void createAttribute_createsAndReturnsDTO() {
        // Arrange
        when(attributeGroupRepository.findById(1L)).thenReturn(Optional.of(attributeGroup));
        when(attributeMapper.toEntity(creationDTO, attributeGroup)).thenReturn(productAttribute);
        when(attributeRepository.save(any(ProductAttribute.class))).thenReturn(productAttribute);
        when(attributeMapper.toDTO(productAttribute)).thenReturn(attributeDTO);

        // Act
        ProductAttributeDTO result = attributeService.createAttribute(creationDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Material");
        verify(attributeGroupRepository).findById(1L);
        verify(attributeRepository).save(any(ProductAttribute.class));
        verify(attributeMapper).toDTO(productAttribute);
    }

    @Test
    void createAttribute_throwsExceptionWhenGroupNotFound() {
        // Arrange
        when(attributeGroupRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> attributeService.createAttribute(new ProductAttributeCreationDTO("Material", 999L)))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product attribute group not found");

        verify(attributeRepository, never()).save(any());
    }

    @Test
    void updateAttribute_updatesAndReturnsDTO() {
        // Arrange
        when(attributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeGroupRepository.findById(1L)).thenReturn(Optional.of(attributeGroup));
        when(attributeRepository.save(any(ProductAttribute.class))).thenReturn(productAttribute);
        when(attributeMapper.toDTO(productAttribute)).thenReturn(attributeDTO);

        // Act
        ProductAttributeDTO result = attributeService.updateAttribute(1L, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        verify(attributeRepository).findById(1L);
        verify(attributeMapper).updateEntity(productAttribute, updateDTO);
        verify(attributeRepository).save(any(ProductAttribute.class));
        verify(attributeMapper).toDTO(productAttribute);
    }

    @Test
    void deleteAttribute_deletesWhenExists() {
        // Arrange
        when(attributeRepository.existsById(1L)).thenReturn(true);

        // Act
        attributeService.deleteAttribute(1L);

        // Assert
        verify(attributeRepository).existsById(1L);
        verify(attributeRepository).deleteById(1L);
    }

    @Test
    void deleteAttribute_throwsExceptionWhenNotFound() {
        // Arrange
        when(attributeRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> attributeService.deleteAttribute(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product attribute not found");

        verify(attributeRepository).existsById(999L);
        verify(attributeRepository, never()).deleteById(any());
    }
}
