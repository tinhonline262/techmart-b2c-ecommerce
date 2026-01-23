package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductAttributeValueDTO;
import com.shopping.microservices.product_service.entity.ProductAttribute;
import com.shopping.microservices.product_service.entity.ProductAttributeGroup;
import com.shopping.microservices.product_service.entity.ProductAttributeValue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProductAttributeValueMapperTest {

    private ProductAttributeValueMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductAttributeValueMapper();
    }

    @Test
    void toDTO_mapsEntityToDTO() {
        // Arrange
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Clothing Attributes");

        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Material");
        attribute.setProductAttributeGroup(group);

        ProductAttributeValue entity = new ProductAttributeValue();
        entity.setId(1L);
        entity.setProductAttribute(attribute);
        entity.setValue("Cotton");

        // Act
        ProductAttributeValueDTO dto = mapper.toDTO(entity);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.attributeId()).isEqualTo(1L);
        assertThat(dto.attributeName()).isEqualTo("Material");
        assertThat(dto.attributeGroupName()).isEqualTo("Clothing Attributes");
        assertThat(dto.value()).isEqualTo("Cotton");
    }

    @Test
    void toDTO_returnsNullWhenEntityIsNull() {
        // Act
        ProductAttributeValueDTO dto = mapper.toDTO(null);

        // Assert
        assertThat(dto).isNull();
    }

    @Test
    void toDTO_handlesNullAttribute() {
        // Arrange
        ProductAttributeValue entity = new ProductAttributeValue();
        entity.setValue("Cotton");

        // Act
        ProductAttributeValueDTO dto = mapper.toDTO(entity);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.attributeId()).isNull();
        assertThat(dto.attributeName()).isNull();
        assertThat(dto.attributeGroupName()).isNull();
        assertThat(dto.value()).isEqualTo("Cotton");
    }

    @Test
    void toDTO_handlesNullAttributeGroup() {
        // Arrange
        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Material");
        attribute.setProductAttributeGroup(null);

        ProductAttributeValue entity = new ProductAttributeValue();
        entity.setProductAttribute(attribute);
        entity.setValue("Cotton");

        // Act
        ProductAttributeValueDTO dto = mapper.toDTO(entity);

        // Assert
        assertThat(dto).isNotNull();
        assertThat(dto.attributeId()).isEqualTo(1L);
        assertThat(dto.attributeName()).isEqualTo("Material");
        assertThat(dto.attributeGroupName()).isNull();
        assertThat(dto.value()).isEqualTo("Cotton");
    }

    @Test
    void toDTOList_mapsList() {
        // Arrange
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Clothing Attributes");

        ProductAttribute attribute = new ProductAttribute();
        attribute.setId(1L);
        attribute.setName("Material");
        attribute.setProductAttributeGroup(group);

        ProductAttributeValue entity1 = new ProductAttributeValue();
        entity1.setProductAttribute(attribute);
        entity1.setValue("Cotton");

        ProductAttributeValue entity2 = new ProductAttributeValue();
        entity2.setProductAttribute(attribute);
        entity2.setValue("Polyester");

        List<ProductAttributeValue> entities = List.of(entity1, entity2);

        // Act
        List<ProductAttributeValueDTO> dtos = mapper.toDTOList(entities);

        // Assert
        assertThat(dtos).hasSize(2);
        assertThat(dtos.get(0).value()).isEqualTo("Cotton");
        assertThat(dtos.get(1).value()).isEqualTo("Polyester");
    }

    @Test
    void toDTOList_returnsEmptyListWhenNull() {
        // Act
        List<ProductAttributeValueDTO> dtos = mapper.toDTOList(null);

        // Assert
        assertThat(dtos).isEmpty();
    }
}
