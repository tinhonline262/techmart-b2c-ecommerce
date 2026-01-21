package com.shopping.microservices.product_service.service;

import com.shopping.microservices.product_service.dto.PageResponseDTO;
import com.shopping.microservices.product_service.dto.ProductOptionCreationDTO;
import com.shopping.microservices.product_service.dto.ProductOptionDTO;
import com.shopping.microservices.product_service.dto.ProductOptionUpdateDTO;
import com.shopping.microservices.product_service.entity.ProductOption;
import com.shopping.microservices.product_service.repository.ProductOptionRepository;
import com.shopping.microservices.product_service.repository.ProductOptionValueRepository;
import com.shopping.microservices.product_service.service.impl.ProductOptionServiceImpl;
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
class ProductOptionServiceImplTest {

    @Mock
    private ProductOptionRepository optionRepository;

    @Mock
    private ProductOptionValueRepository optionValueRepository;

    @InjectMocks
    private ProductOptionServiceImpl optionService;

    private ProductOption productOption;
    private ProductOptionCreationDTO creationDTO;
    private ProductOptionUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        productOption = ProductOption.builder()
                .id(1L)
                .name("Color")
                .build();

        creationDTO = new ProductOptionCreationDTO(1L, "Color");
        updateDTO = new ProductOptionUpdateDTO("Size");
    }

    @Test
    void getOptions_returnsPagedOptions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<ProductOption> options = List.of(productOption);
        Page<ProductOption> page = new PageImpl<>(options, pageable, 1);

        when(optionRepository.findAll(pageable)).thenReturn(page);
        when(optionValueRepository.findByProductOptionId(1L)).thenReturn(List.of());

        // Act
        PageResponseDTO<ProductOptionDTO> result = optionService.getOptions(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.pageNumber()).isEqualTo(0);
        assertThat(result.pageSize()).isEqualTo(10);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
        verify(optionRepository).findAll(pageable);
    }

    @Test
    void getOptionById_returnsDTOWhenExists() {
        // Arrange
        when(optionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        when(optionValueRepository.findByProductOptionId(1L)).thenReturn(List.of());

        // Act
        ProductOptionDTO result = optionService.getOptionById(1L);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Color");
        verify(optionRepository).findById(1L);
    }

    @Test
    void getOptionById_throwsExceptionWhenNotFound() {
        // Arrange
        when(optionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> optionService.getOptionById(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product option not found with id: 999");
        verify(optionRepository).findById(999L);
    }

    @Test
    void createOption_createsSuccessfully() {
        // Arrange
        ProductOption savedOption = ProductOption.builder()
                .id(1L)
                .name("Color")
                .build();

        when(optionRepository.save(any(ProductOption.class))).thenReturn(savedOption);
        when(optionValueRepository.findByProductOptionId(1L)).thenReturn(List.of());

        // Act
        ProductOptionDTO result = optionService.createOption(creationDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Color");
        verify(optionRepository).save(any(ProductOption.class));
    }

    @Test
    void updateOption_updatesSuccessfully() {
        // Arrange
        ProductOption updatedOption = ProductOption.builder()
                .id(1L)
                .name("Size")
                .build();

        when(optionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        when(optionRepository.save(any(ProductOption.class))).thenReturn(updatedOption);
        when(optionValueRepository.findByProductOptionId(1L)).thenReturn(List.of());

        // Act
        ProductOptionDTO result = optionService.updateOption(1L, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Size");
        verify(optionRepository).findById(1L);
        verify(optionRepository).save(any(ProductOption.class));
    }

    @Test
    void updateOption_throwsExceptionWhenNotFound() {
        // Arrange
        when(optionRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> optionService.updateOption(999L, updateDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product option not found with id: 999");
        verify(optionRepository).findById(999L);
        verify(optionRepository, never()).save(any());
    }

    @Test
    void deleteOption_deletesSuccessfully() {
        // Arrange
        when(optionRepository.existsById(1L)).thenReturn(true);

        // Act
        optionService.deleteOption(1L);

        // Assert
        verify(optionRepository).existsById(1L);
        verify(optionRepository).deleteById(1L);
    }

    @Test
    void deleteOption_throwsExceptionWhenNotFound() {
        // Arrange
        when(optionRepository.existsById(999L)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> optionService.deleteOption(999L))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product option not found with id: 999");
        verify(optionRepository).existsById(999L);
        verify(optionRepository, never()).deleteById(any());
    }

    @Test
    void updateOption_doesNotUpdateWhenNameIsBlank() {
        // Arrange
        ProductOptionUpdateDTO blankUpdateDTO = new ProductOptionUpdateDTO("  ");
        when(optionRepository.findById(1L)).thenReturn(Optional.of(productOption));
        when(optionRepository.save(any(ProductOption.class))).thenReturn(productOption);
        when(optionValueRepository.findByProductOptionId(1L)).thenReturn(List.of());

        // Act
        ProductOptionDTO result = optionService.updateOption(1L, blankUpdateDTO);

        // Assert
        assertThat(result.name()).isEqualTo("Color"); // Original name unchanged
        verify(optionRepository).findById(1L);
    }

    @Test
    void getOptions_returnsEmptyPageWhenNoOptions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<ProductOption> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        when(optionRepository.findAll(pageable)).thenReturn(emptyPage);

        // Act
        PageResponseDTO<ProductOptionDTO> result = optionService.getOptions(pageable);

        // Assert
        assertThat(result.content()).isEmpty();
        assertThat(result.totalElements()).isEqualTo(0);
        assertThat(result.totalPages()).isEqualTo(0);
    }
}
