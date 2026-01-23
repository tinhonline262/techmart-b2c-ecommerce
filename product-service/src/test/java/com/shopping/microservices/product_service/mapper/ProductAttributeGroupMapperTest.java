package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductAttributeGroupCreationDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupDTO;
import com.shopping.microservices.product_service.dto.ProductAttributeGroupUpdateDTO;
import com.shopping.microservices.product_service.entity.ProductAttributeGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProductAttributeGroupMapperTest {

    private ProductAttributeGroupMapper mapper;
    private ProductAttributeMapper attributeMapper;

    @BeforeEach
    void setUp() {
        attributeMapper = new ProductAttributeMapper();
        mapper = new ProductAttributeGroupMapper(attributeMapper);
    }

    @Test
    void toEntity_mapsCreationDTOToEntity() {
        // Arrange
        ProductAttributeGroupCreationDTO dto = new ProductAttributeGroupCreationDTO("Clothing Attributes");

        // Act
        ProductAttributeGroup entity = mapper.toEntity(dto);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getName()).isEqualTo("Clothing Attributes");
        assertThat(entity.getCreatedAt()).isNotNull();
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void toEntity_returnsNullWhenDTOIsNull() {
        // Act
        ProductAttributeGroup entity = mapper.toEntity(null);

        // Assert
        assertThat(entity).isNull();
    }

    @Test
    void updateEntity_updatesNameWhenProvided() {
        // Arrange
        ProductAttributeGroup entity = new ProductAttributeGroup();
        entity.setName("Clothing Attributes");
        ProductAttributeGroupUpdateDTO dto = new ProductAttributeGroupUpdateDTO("Fashion Attributes");

        // Act
        mapper.updateEntity(entity, dto);

        // Assert
        assertThat(entity.getName()).isEqualTo("Fashion Attributes");
        assertThat(entity.getUpdatedAt()).isNotNull();
    }

    @Test
    void updateEntity_keepsNameWhenDTONameIsNull() {
        // Arrange
        ProductAttributeGroup entity = new ProductAttributeGroup();
        entity.setName("Clothing Attributes");
        ProductAttributeGroupUpdateDTO dto = new ProductAttributeGroupUpdateDTO(null);

        // Act
        mapper.updateEntity(entity, dto);

        // Assert
        assertThat(entity.getName()).isEqualTo("Clothing Attributes");
    }

    @Test
    void toDTO_mapsEntityToDTO() {
        // Arrange
        ProductAttributeGroup entity = new ProductAttributeGroup();
        entity.setId(1L);
        entity.setName("Clothing Attributes");
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        // Act
        ProductAttributeGroupDTO dto = mapper.toDTO(entity, List.of());

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.name()).isEqualTo("Clothing Attributes");
        assertThat(dto.attributes()).isEmpty();
        assertThat(dto.createdAt()).isNotNull();
        assertThat(dto.updatedAt()).isNotNull();
    }

    @Test
    void toDTO_returnsNullWhenEntityIsNull() {
        // Act
        ProductAttributeGroupDTO dto = mapper.toDTO(null, List.of());

        // Assert
        assertThat(dto).isNull();
    }

    @Test
    void toDTO_handleNullAttributes() {
        // Arrange
        ProductAttributeGroup entity = new ProductAttributeGroup();
        entity.setId(1L);
        entity.setName("Clothing Attributes");

        // Act
        ProductAttributeGroupDTO dto = mapper.toDTO(entity, null);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.attributes()).isEmpty();
    }
}
