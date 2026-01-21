package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeUpdateDTO;
import com.shopping.microservices.product_service.entity.ProductOption;
import com.shopping.microservices.product_service.mapper.AttributeMapper;
import com.shopping.microservices.product_service.repository.ProductAttributeRepository;
import com.shopping.microservices.product_service.service.impl.ProductAttributeServiceImpl;
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
    private AttributeMapper attributeMapper;

    @InjectMocks
    private ProductAttributeServiceImpl attributeService;

    private ProductOption productAttribute;
    private ProductAttributeCreationDTO creationDTO;
    private ProductAttributeUpdateDTO updateDTO;
    private ProductAttributeDTO attributeDTO;

    @BeforeEach
    void setUp() {
        productAttribute = ProductOption.builder()
                .id(1L)
                .name("Material")
                .build();

        creationDTO = new ProductAttributeCreationDTO("Material", null);
        updateDTO = new ProductAttributeUpdateDTO("Fabric", null);
        attributeDTO = new ProductAttributeDTO(1L, "Material", null, "GroupName", null, null);
    }

    @Test
    void getAttributes_returnsPagedAttributes() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductOption> attributes = List.of(productAttribute);
        Page<ProductOption> page = new PageImpl<>(attributes, pageable, 1);

        when(attributeRepository.findAll(pageable)).thenReturn(page);
        when(attributeMapper.toAttributeDTO(productAttribute)).thenReturn(attributeDTO);

        // Act
        PageResponseDTO<ProductAttributeDTO> result = attributeService.getAttributes(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        verify(attributeRepository).findAll(pageable);
        verify(attributeMapper).toAttributeDTO(productAttribute);
    }

    @Test
    void getAttributeById_returnsDTOWhenExists() {
        // Arrange
        when(attributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeMapper.toAttributeDTO(productAttribute)).thenReturn(attributeDTO);

        // Act
        ProductAttributeDTO result = attributeService.getAttributeById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Material");
        verify(attributeRepository).findById(1L);
        verify(attributeMapper).toAttributeDTO(productAttribute);
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
        verify(attributeMapper, never()).toAttributeDTO(any());
    }

    @Test
    void createAttribute_createsAndReturnsDTO() {
        // Arrange
        when(attributeRepository.save(any(ProductOption.class))).thenReturn(productAttribute);
        when(attributeMapper.toAttributeDTO(productAttribute)).thenReturn(attributeDTO);

        // Act
        ProductAttributeDTO result = attributeService.createAttribute(creationDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Material");
        verify(attributeRepository).save(any(ProductOption.class));
        verify(attributeMapper).toAttributeDTO(productAttribute);
    }

    @Test
    void updateAttribute_updatesAndReturnsDTO() {
        // Arrange
        ProductAttributeDTO updatedDTO = new ProductAttributeDTO(1L, "Fabric", null, "GroupName", null, null);
        when(attributeRepository.findById(1L)).thenReturn(Optional.of(productAttribute));
        when(attributeRepository.save(any(ProductOption.class))).thenReturn(productAttribute);
        when(attributeMapper.toAttributeDTO(productAttribute)).thenReturn(updatedDTO);

        // Act
        ProductAttributeDTO result = attributeService.updateAttribute(1L, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        verify(attributeRepository).findById(1L);
        verify(attributeMapper).updateAttributeEntity(productAttribute, updateDTO);
        verify(attributeRepository).save(any(ProductOption.class));
        verify(attributeMapper).toAttributeDTO(productAttribute);
    }

    @Test
    void updateAttribute_throwsExceptionWhenNotFound() {
        // Arrange
        when(attributeRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> attributeService.updateAttribute(999L, updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product attribute not found");

        verify(attributeRepository).findById(999L);
        verify(attributeRepository, never()).save(any());
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
