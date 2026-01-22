package com.shopping.microservices.product_service.service.impl;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupUpdateDTO;
import com.shopping.microservices.product_service.entity.ProductAttributeGroup;
import com.shopping.microservices.product_service.mapper.ProductAttributeGroupMapper;
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
class ProductAttributeGroupServiceImplTest {

    @Mock
    private ProductAttributeGroupRepository groupRepository;

    @Mock
    private ProductAttributeRepository attributeRepository;

    @Mock
    private ProductAttributeGroupMapper groupMapper;

    @InjectMocks
    private ProductAttributeGroupServiceImpl groupService;

    private ProductAttributeGroup attributeGroup;
    private ProductAttributeGroupCreationDTO creationDTO;
    private ProductAttributeGroupUpdateDTO updateDTO;
    private ProductAttributeGroupDTO groupDTO;

    @BeforeEach
    void setUp() {
        attributeGroup = new ProductAttributeGroup();
        attributeGroup.setId(1L);
        attributeGroup.setName("Clothing Attributes");
        attributeGroup.setCreatedAt(Instant.now());
        attributeGroup.setUpdatedAt(Instant.now());

        creationDTO = new ProductAttributeGroupCreationDTO("Clothing Attributes");
        updateDTO = new ProductAttributeGroupUpdateDTO("Fashion Attributes");
        groupDTO = new ProductAttributeGroupDTO(1L, "Clothing Attributes", List.of(), null, null);
    }

    @Test
    void getAttributeGroups_returnsPagedGroups() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductAttributeGroup> groups = List.of(attributeGroup);
        Page<ProductAttributeGroup> page = new PageImpl<>(groups, pageable, 1);

        when(groupRepository.findAll(pageable)).thenReturn(page);
        when(attributeRepository.findByProductAttributeGroupId(1L)).thenReturn(List.of());
        when(groupMapper.toDTO(attributeGroup, List.of())).thenReturn(groupDTO);

        // Act
        PageResponseDTO<ProductAttributeGroupDTO> result = groupService.getAttributeGroups(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.pageNumber()).isEqualTo(0);
        verify(groupRepository).findAll(pageable);
        verify(groupMapper).toDTO(attributeGroup, List.of());
    }

    @Test
    void getAttributeGroupById_returnsDTOWhenExists() {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.of(attributeGroup));
        when(attributeRepository.findByProductAttributeGroupId(1L)).thenReturn(List.of());
        when(groupMapper.toDTO(attributeGroup, List.of())).thenReturn(groupDTO);

        // Act
        ProductAttributeGroupDTO result = groupService.getAttributeGroupById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Clothing Attributes");
        verify(groupRepository).findById(1L);
        verify(groupMapper).toDTO(attributeGroup, List.of());
    }

    @Test
    void getAttributeGroupById_throwsExceptionWhenNotFound() {
        // Arrange
        when(groupRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> groupService.getAttributeGroupById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product attribute group not found");

        verify(groupRepository).findById(999L);
        verify(groupMapper, never()).toDTO(any(), any());
    }

    @Test
    void createAttributeGroup_createsAndReturnsDTO() {
        // Arrange
        when(groupMapper.toEntity(creationDTO)).thenReturn(attributeGroup);
        when(groupRepository.save(any(ProductAttributeGroup.class))).thenReturn(attributeGroup);
        when(groupMapper.toDTO(attributeGroup, null)).thenReturn(groupDTO);

        // Act
        ProductAttributeGroupDTO result = groupService.createAttributeGroup(creationDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Clothing Attributes");
        verify(groupRepository).save(any(ProductAttributeGroup.class));
        verify(groupMapper).toDTO(attributeGroup, null);
    }

    @Test
    void updateAttributeGroup_updatesAndReturnsDTO() {
        // Arrange
        when(groupRepository.findById(1L)).thenReturn(Optional.of(attributeGroup));
        when(groupRepository.save(any(ProductAttributeGroup.class))).thenReturn(attributeGroup);
        when(attributeRepository.findByProductAttributeGroupId(1L)).thenReturn(List.of());
        when(groupMapper.toDTO(attributeGroup, List.of())).thenReturn(groupDTO);

        // Act
        ProductAttributeGroupDTO result = groupService.updateAttributeGroup(1L, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        verify(groupRepository).findById(1L);
        verify(groupMapper).updateEntity(attributeGroup, updateDTO);
        verify(groupRepository).save(any(ProductAttributeGroup.class));
        verify(groupMapper).toDTO(attributeGroup, List.of());
    }

    @Test
    void deleteAttributeGroup_deletesWhenExists() {
        // Arrange
        when(groupRepository.existsById(1L)).thenReturn(true);

        // Act
        groupService.deleteAttributeGroup(1L);

        // Assert
        verify(groupRepository).existsById(1L);
        verify(groupRepository).deleteById(1L);
    }

    @Test
    void deleteAttributeGroup_throwsExceptionWhenNotFound() {
        // Arrange
        when(groupRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> groupService.deleteAttributeGroup(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product attribute group not found");

        verify(groupRepository).existsById(999L);
        verify(groupRepository, never()).deleteById(any());
    }
}
