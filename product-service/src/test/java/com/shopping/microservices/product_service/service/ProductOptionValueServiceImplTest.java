package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.ProductOptionValueDTO;
import com.shopping.microservices.product_service.entity.ProductOption;
import com.shopping.microservices.product_service.entity.ProductOptionValue;
import com.shopping.microservices.product_service.repository.ProductOptionValueRepository;
import com.shopping.microservices.product_service.service.impl.ProductOptionValueServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductOptionValueServiceImplTest {

    @Mock
    private ProductOptionValueRepository optionValueRepository;

    @InjectMocks
    private ProductOptionValueServiceImpl optionValueService;

    private ProductOption productOption;
    private ProductOptionValue optionValue1;
    private ProductOptionValue optionValue2;

    @BeforeEach
    void setUp() {
        productOption = ProductOption.builder()
                .id(1L)
                .name("Color")
                .build();

        optionValue1 = ProductOptionValue.builder()
                .id(1L)
                .productOption(productOption)
                .value("Red")
                .displayType("COLOR")
                .displayOrder(1)
                .build();

        optionValue2 = ProductOptionValue.builder()
                .id(2L)
                .productOption(productOption)
                .value("Blue")
                .displayType("COLOR")
                .displayOrder(2)
                .build();
    }

    @Test
    void getOptionValues_returnsValuesListByOptionId() {
        // Arrange
        List<ProductOptionValue> values = List.of(optionValue1, optionValue2);
        when(optionValueRepository.findByProductOptionId(1L)).thenReturn(values);

        // Act
        List<ProductOptionValueDTO> result = optionValueService.getOptionValues(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).value()).isEqualTo("Red");
        assertThat(result.get(0).displayType()).isEqualTo("COLOR");
        assertThat(result.get(0).displayOrder()).isEqualTo(1);
        
        assertThat(result.get(1).id()).isEqualTo(2L);
        assertThat(result.get(1).value()).isEqualTo("Blue");
        assertThat(result.get(1).displayOrder()).isEqualTo(2);
        
        verify(optionValueRepository).findByProductOptionId(1L);
    }

    @Test
    void getOptionValues_returnsEmptyListWhenNoValues() {
        // Arrange
        when(optionValueRepository.findByProductOptionId(999L)).thenReturn(List.of());

        // Act
        List<ProductOptionValueDTO> result = optionValueService.getOptionValues(999L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(optionValueRepository).findByProductOptionId(999L);
    }

    @Test
    void getOptionValues_mapsOptionNameCorrectly() {
        // Arrange
        List<ProductOptionValue> values = List.of(optionValue1);
        when(optionValueRepository.findByProductOptionId(1L)).thenReturn(values);

        // Act
        List<ProductOptionValueDTO> result = optionValueService.getOptionValues(1L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).optionId()).isEqualTo(1L);
        assertThat(result.get(0).optionName()).isEqualTo("Color");
    }

    @Test
    void getOptionValues_mapsOptionalFieldsWhenNull() {
        // Arrange
        ProductOptionValue valueWithoutOption = ProductOptionValue.builder()
                .id(3L)
                .productOption(null)
                .value("Green")
                .displayType("COLOR")
                .displayOrder(3)
                .build();

        when(optionValueRepository.findByProductOptionId(999L))
                .thenReturn(List.of(valueWithoutOption));

        // Act
        List<ProductOptionValueDTO> result = optionValueService.getOptionValues(999L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).optionId()).isNull();
        assertThat(result.get(0).optionName()).isNull();
        assertThat(result.get(0).value()).isEqualTo("Green");
    }

    @Test
    void getOptionValues_callsRepositoryWithCorrectParameter() {
        // Arrange
        when(optionValueRepository.findByProductOptionId(1L)).thenReturn(List.of());

        // Act
        optionValueService.getOptionValues(1L);

        // Assert
        verify(optionValueRepository, times(1)).findByProductOptionId(1L);
    }

    @Test
    void getOptionValues_returnsMultipleValuesWithDifferentDisplayTypes() {
        // Arrange
        ProductOptionValue sizeValue = ProductOptionValue.builder()
                .id(3L)
                .productOption(ProductOption.builder().id(2L).name("Size").build())
                .value("L")
                .displayType("TEXT")
                .displayOrder(1)
                .build();

        List<ProductOptionValue> values = List.of(sizeValue);
        when(optionValueRepository.findByProductOptionId(2L)).thenReturn(values);

        // Act
        List<ProductOptionValueDTO> result = optionValueService.getOptionValues(2L);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).value()).isEqualTo("L");
        assertThat(result.get(0).displayType()).isEqualTo("TEXT");
        assertThat(result.get(0).optionName()).isEqualTo("Size");
    }
}
