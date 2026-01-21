package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductAttributeCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupUpdateDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeUpdateDTO;
import com.shopping.microservices.product_service.entity.ProductOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class AttributeMapperTest {

    private AttributeMapper attributeMapper;

    @BeforeEach
    void setUp() {
        attributeMapper = new AttributeMapper();
    }

    // ============ ATTRIBUTE TESTS ============

    @Test
    void toAttributeEntity_createsEntityFromDTO() {
        // Arrange
        ProductAttributeCreationDTO dto = new ProductAttributeCreationDTO("Material", null);

        // Act
        ProductOption result = attributeMapper.toAttributeEntity(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Material");
    }

    @Test
    void toAttributeEntity_returnsNullWhenDTOIsNull() {
        // Act
        ProductOption result = attributeMapper.toAttributeEntity(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void updateAttributeEntity_updatesNameWhenProvided() {
        // Arrange
        ProductOption attribute = ProductOption.builder().id(1L).name("Material").build();
        ProductAttributeUpdateDTO dto = new ProductAttributeUpdateDTO("Fabric", null);

        // Act
        attributeMapper.updateAttributeEntity(attribute, dto);

        // Assert
        assertThat(attribute.getName()).isEqualTo("Fabric");
    }

    @Test
    void updateAttributeEntity_keepsNameWhenDTONameIsNull() {
        // Arrange
        ProductOption attribute = ProductOption.builder().id(1L).name("Material").build();
        ProductAttributeUpdateDTO dto = new ProductAttributeUpdateDTO(null, null);

        // Act
        attributeMapper.updateAttributeEntity(attribute, dto);

        // Assert
        assertThat(attribute.getName()).isEqualTo("Material");
    }

    @Test
    void updateAttributeEntity_ignoresNullDTO() {
        // Arrange
        ProductOption attribute = ProductOption.builder().id(1L).name("Material").build();

        // Act
        attributeMapper.updateAttributeEntity(attribute, null);

        // Assert
        assertThat(attribute.getName()).isEqualTo("Material");
    }

    @Test
    void toAttributeDTO_mapsEntityToDTO() {
        // Arrange
        ProductOption attribute = ProductOption.builder().id(1L).name("Material").build();

        // Act
        ProductAttributeDTO result = attributeMapper.toAttributeDTO(attribute);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Material");
    }

    @Test
    void toAttributeDTO_returnsNullWhenEntityIsNull() {
        // Act
        ProductAttributeDTO result = attributeMapper.toAttributeDTO(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void toAttributeDTOList_mapsList() {
        // Arrange
        List<ProductOption> attributes = List.of(
                ProductOption.builder().id(1L).name("Material").build(),
                ProductOption.builder().id(2L).name("Color").build()
        );

        // Act
        List<ProductAttributeDTO> result = attributeMapper.toAttributeDTOList(attributes);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Material");
        assertThat(result.get(1).name()).isEqualTo("Color");
    }

    @Test
    void toAttributeDTOList_returnsEmptyListWhenNull() {
        // Act
        List<ProductAttributeDTO> result = attributeMapper.toAttributeDTOList(null);

        // Assert
        assertThat(result).isEmpty();
    }

    // ============ ATTRIBUTE GROUP TESTS ============

    @Test
    void toAttributeGroupEntity_createsEntityFromDTO() {
        // Arrange
        ProductAttributeGroupCreationDTO dto = new ProductAttributeGroupCreationDTO("Clothing Attributes");

        // Act
        ProductOption result = attributeMapper.toAttributeGroupEntity(dto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Clothing Attributes");
    }

    @Test
    void toAttributeGroupEntity_returnsNullWhenDTOIsNull() {
        // Act
        ProductOption result = attributeMapper.toAttributeGroupEntity(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void updateAttributeGroupEntity_updatesNameWhenProvided() {
        // Arrange
        ProductOption group = ProductOption.builder().id(1L).name("Clothing Attributes").build();
        ProductAttributeGroupUpdateDTO dto = new ProductAttributeGroupUpdateDTO("Fashion Attributes");

        // Act
        attributeMapper.updateAttributeGroupEntity(group, dto);

        // Assert
        assertThat(group.getName()).isEqualTo("Fashion Attributes");
    }

    @Test
    void updateAttributeGroupEntity_keepsNameWhenDTONameIsNull() {
        // Arrange
        ProductOption group = ProductOption.builder().id(1L).name("Clothing Attributes").build();
        ProductAttributeGroupUpdateDTO dto = new ProductAttributeGroupUpdateDTO(null);

        // Act
        attributeMapper.updateAttributeGroupEntity(group, dto);

        // Assert
        assertThat(group.getName()).isEqualTo("Clothing Attributes");
    }

    @Test
    void toAttributeGroupDTO_mapsEntityToDTO() {
        // Arrange
        ProductOption group = ProductOption.builder().id(1L).name("Clothing Attributes").build();

        // Act
        ProductAttributeGroupDTO result = attributeMapper.toAttributeGroupDTO(group);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Clothing Attributes");
    }

    @Test
    void toAttributeGroupDTO_returnsNullWhenEntityIsNull() {
        // Act
        ProductAttributeGroupDTO result = attributeMapper.toAttributeGroupDTO(null);

        // Assert
        assertThat(result).isNull();
    }

    @Test
    void toAttributeGroupDTOList_mapsList() {
        // Arrange
        List<ProductOption> groups = List.of(
                ProductOption.builder().id(1L).name("Clothing Attributes").build(),
                ProductOption.builder().id(2L).name("Electronics Attributes").build()
        );

        // Act
        List<ProductAttributeGroupDTO> result = attributeMapper.toAttributeGroupDTOList(groups);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).name()).isEqualTo("Clothing Attributes");
        assertThat(result.get(1).name()).isEqualTo("Electronics Attributes");
    }

    @Test
    void toAttributeGroupDTOList_returnsEmptyListWhenNull() {
        // Act
        List<ProductAttributeGroupDTO> result = attributeMapper.toAttributeGroupDTOList(null);

        // Assert
        assertThat(result).isEmpty();
    }
}
