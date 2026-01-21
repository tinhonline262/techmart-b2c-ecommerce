package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupUpdateDTO;
import com.shopping.microservices.product_service.entity.ProductOption;
import com.shopping.microservices.product_service.mapper.AttributeMapper;
import com.shopping.microservices.product_service.repository.ProductAttributeGroupRepository;
import com.shopping.microservices.product_service.service.impl.ProductAttributeGroupServiceImpl;
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
class ProductAttributeGroupServiceImplTest {

    @Mock
    private ProductAttributeGroupRepository attributeGroupRepository;

    @Mock
    private AttributeMapper attributeMapper;

    @InjectMocks
    private ProductAttributeGroupServiceImpl attributeGroupService;

    private ProductOption productAttributeGroup;
    private ProductAttributeGroupCreationDTO creationDTO;
    private ProductAttributeGroupUpdateDTO updateDTO;
    private ProductAttributeGroupDTO attributeGroupDTO;

    @BeforeEach
    void setUp() {
        productAttributeGroup = ProductOption.builder()
                .id(1L)
                .name("Clothing Attributes")
                .build();

        creationDTO = new ProductAttributeGroupCreationDTO("Clothing Attributes");
        updateDTO = new ProductAttributeGroupUpdateDTO("Fashion Attributes");
        attributeGroupDTO = new ProductAttributeGroupDTO(1L, "Clothing Attributes", List.of(), null, null);
    }

    @Test
    void getAttributeGroups_returnsPagedAttributeGroups() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductOption> groups = List.of(productAttributeGroup);
        Page<ProductOption> page = new PageImpl<>(groups, pageable, 1);

        when(attributeGroupRepository.findAll(pageable)).thenReturn(page);
        when(attributeMapper.toAttributeGroupDTO(productAttributeGroup)).thenReturn(attributeGroupDTO);

        // Act
        PageResponseDTO<ProductAttributeGroupDTO> result = attributeGroupService.getAttributeGroups(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        verify(attributeGroupRepository).findAll(pageable);
        verify(attributeMapper).toAttributeGroupDTO(productAttributeGroup);
    }

    @Test
    void getAttributeGroupById_returnsDTOWhenExists() {
        // Arrange
        when(attributeGroupRepository.findById(1L)).thenReturn(Optional.of(productAttributeGroup));
        when(attributeMapper.toAttributeGroupDTO(productAttributeGroup)).thenReturn(attributeGroupDTO);

        // Act
        ProductAttributeGroupDTO result = attributeGroupService.getAttributeGroupById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Clothing Attributes");
        verify(attributeGroupRepository).findById(1L);
        verify(attributeMapper).toAttributeGroupDTO(productAttributeGroup);
    }

    @Test
    void getAttributeGroupById_throwsExceptionWhenNotFound() {
        // Arrange
        when(attributeGroupRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> attributeGroupService.getAttributeGroupById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product attribute group not found");

        verify(attributeGroupRepository).findById(999L);
        verify(attributeMapper, never()).toAttributeGroupDTO(any());
    }

    @Test
    void createAttributeGroup_createsAndReturnsDTO() {
        // Arrange
        when(attributeGroupRepository.save(any(ProductOption.class))).thenReturn(productAttributeGroup);
        when(attributeMapper.toAttributeGroupDTO(productAttributeGroup)).thenReturn(attributeGroupDTO);

        // Act
        ProductAttributeGroupDTO result = attributeGroupService.createAttributeGroup(creationDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Clothing Attributes");
        verify(attributeGroupRepository).save(any(ProductOption.class));
        verify(attributeMapper).toAttributeGroupDTO(productAttributeGroup);
    }

    @Test
    void updateAttributeGroup_updatesAndReturnsDTO() {
        // Arrange
        ProductAttributeGroupDTO updatedDTO = new ProductAttributeGroupDTO(1L, "Fashion Attributes", List.of(), null, null);
        when(attributeGroupRepository.findById(1L)).thenReturn(Optional.of(productAttributeGroup));
        when(attributeGroupRepository.save(any(ProductOption.class))).thenReturn(productAttributeGroup);
        when(attributeMapper.toAttributeGroupDTO(productAttributeGroup)).thenReturn(updatedDTO);

        // Act
        ProductAttributeGroupDTO result = attributeGroupService.updateAttributeGroup(1L, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        verify(attributeGroupRepository).findById(1L);
        verify(attributeMapper).updateAttributeGroupEntity(productAttributeGroup, updateDTO);
        verify(attributeGroupRepository).save(any(ProductOption.class));
        verify(attributeMapper).toAttributeGroupDTO(productAttributeGroup);
    }

    @Test
    void updateAttributeGroup_throwsExceptionWhenNotFound() {
        // Arrange
        when(attributeGroupRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> attributeGroupService.updateAttributeGroup(999L, updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product attribute group not found");

        verify(attributeGroupRepository).findById(999L);
        verify(attributeGroupRepository, never()).save(any());
    }

    @Test
    void deleteAttributeGroup_deletesWhenExists() {
        // Arrange
        when(attributeGroupRepository.existsById(1L)).thenReturn(true);

        // Act
        attributeGroupService.deleteAttributeGroup(1L);

        // Assert
        verify(attributeGroupRepository).existsById(1L);
        verify(attributeGroupRepository).deleteById(1L);
    }

    @Test
    void deleteAttributeGroup_throwsExceptionWhenNotFound() {
        // Arrange
        when(attributeGroupRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> attributeGroupService.deleteAttributeGroup(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product attribute group not found");

        verify(attributeGroupRepository).existsById(999L);
        verify(attributeGroupRepository, never()).deleteById(any());
    }
}
