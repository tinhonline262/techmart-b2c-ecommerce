package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductAttributeCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeUpdateDTO;
import com.shopping.microservices.product_service.entity.ProductAttribute;
import com.shopping.microservices.product_service.entity.ProductAttributeGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProductAttributeMapperTest {

    private ProductAttributeMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductAttributeMapper();
    }

    @Test
    void toEntity_mapsCreationDTOToEntity() {
        // Arrange
        ProductAttributeCreationDTO dto = new ProductAttributeCreationDTO("Material", 1L);
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Clothing");

        // Act
        ProductAttribute entity = mapper.toEntity(dto, group);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Material");
        assertThat(entity.getProductAttributeGroup()).isEqualTo(group);
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void toEntity_returnsNullWhenDTOIsNull() {
        // Act
        ProductAttribute entity = mapper.toEntity(null, null);

        // Assert
        assertThat(entity).isNull();
    }

    @Test
    void updateEntity_updatesNameWhenProvided() {
        // Arrange
        ProductAttribute entity = new ProductAttribute();
        entity.setName("Material");
        ProductAttributeUpdateDTO dto = new ProductAttributeUpdateDTO("Fabric", 1L);

        // Act
        mapper.updateEntity(entity, dto);

        // Assert
        assertThat(entity.getName()).isEqualTo("Fabric");
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateEntity_keepsNameWhenDTONameIsNull() {
        // Arrange
        ProductAttribute entity = new ProductAttribute();
        entity.setName("Material");
        ProductAttributeUpdateDTO dto = new ProductAttributeUpdateDTO(null, 1L);

        // Act
        mapper.updateEntity(entity, dto);

        // Assert
        assertThat(entity.getName()).isEqualTo("Material");
    }

    @Test
    void toDTO_mapsEntityToDTO() {
        // Arrange
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Clothing");

        ProductAttribute entity = new ProductAttribute();
        entity.setId(1L);
        entity.setName("Material");
        entity.setProductAttributeGroup(group);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        // Act
        ProductAttributeDTO dto = mapper.toDTO(entity);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Material");
        assertThat(dto.groupId()).isEqualTo(1L);
        assertThat(dto.groupName()).isEqualTo("Clothing");
        assertThat(dto.createdAt()).isNotNull();
        assertThat(dto.updatedAt()).isNotNull();
    }

    @Test
    void toDTO_returnsNullWhenEntityIsNull() {
        // Act
        ProductAttributeDTO dto = mapper.toDTO(null);

        // Assert
        assertThat(dto).isNull();
    }

    @Test
    void toDTO_handlesNullGroupId() {
        // Arrange
        ProductAttribute entity = new ProductAttribute();
        entity.setId(1L);
        entity.setName("Material");
        entity.setProductAttributeGroup(null);

        // Act
        ProductAttributeDTO dto = mapper.toDTO(entity);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.groupId()).isNull();
        assertThat(dto.groupName()).isNull();
    }

    @Test
    void toDTOList_mapsList() {
        // Arrange
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Clothing");

        ProductAttribute entity1 = new ProductAttribute();
        entity1.setId(1L);
        entity1.setName("Material");
        entity1.setProductAttributeGroup(group);

        ProductAttribute entity2 = new ProductAttribute();
        entity2.setId(2L);
        entity2.setName("Color");
        entity2.setProductAttributeGroup(group);

        List<ProductAttribute> entities = List.of(entity1, entity2);

        // Act
        List<ProductAttributeDTO> dtos = mapper.toDTOList(entities);

        // Assert
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).name()).isEqualTo("Material");
        assertThat(dtos.get(1).name()).isEqualTo("Color");
    }

    @Test
    void toDTOList_returnsEmptyListWhenNull() {
        // Act
        List<ProductAttributeDTO> dtos = mapper.toDTOList(null);

        // Assert
        assertThat(dtos).isEmpty();
    }
}
