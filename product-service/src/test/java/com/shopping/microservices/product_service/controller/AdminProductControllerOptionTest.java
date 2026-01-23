package com.shopping.microservices.product_service.controller;

import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.service.ProductOptionService;
import com.shopping.microservices.product_service.service.ProductOptionValueService;
import com.shopping.microservices.product_service.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminProductControllerOptionTest {

    @Mock
    private ProductService productService;

    @Mock
    private ProductOptionService productOptionService;

    @Mock
    private ProductOptionValueService productOptionValueService;

    @InjectMocks
    private AdminProductController controller;

    private ProductOptionDTO optionDTO;
    private ProductOptionCreationDTO creationDTO;
    private ProductOptionUpdateDTO updateDTO;
    private ProductOptionValueDTO valueDTO;

    @BeforeEach
    void setUp() {
        optionDTO = new ProductOptionDTO(1L, "Color", List.of(), null, null);
        creationDTO = new ProductOptionCreationDTO(1L, "Color");
        updateDTO = new ProductOptionUpdateDTO("Size");
        valueDTO = new ProductOptionValueDTO(1L, 1L, "Color", "Red", "COLOR", 1, null, null);
    }

    @Test
    void getOptions_returnsOkWithPagedOptions() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        PageResponseDTO<ProductOptionDTO> pageResponse = new PageResponseDTO<>(
                List.of(optionDTO), 0, 10, 1, 1, true, false, false
        );
        when(productOptionService.getOptions(pageable)).thenReturn(pageResponse);

        // Act
        ResponseEntity<ApiResponse<PageResponseDTO<ProductOptionDTO>>> response = controller.getOptions(pageable);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(pageResponse);
        assertThat(response.getBody().getMessage()).isEqualTo("Product options retrieved successfully");
        verify(productOptionService).getOptions(pageable);
    }

    @Test
    void getOptionById_returnsOkWithOption() {
        // Arrange
        when(productOptionService.getOptionById(1L)).thenReturn(optionDTO);

        // Act
        ResponseEntity<ApiResponse<ProductOptionDTO>> response = controller.getOptionById(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(optionDTO);
        assertThat(response.getBody().getMessage()).isEqualTo("Product option retrieved successfully");
        verify(productOptionService).getOptionById(1L);
    }

    @Test
    void createOption_returnsCreatedWithNewOption() {
        // Arrange
        when(productOptionService.createOption(creationDTO)).thenReturn(optionDTO);

        // Act
        ResponseEntity<ApiResponse<ProductOptionDTO>> response = controller.createOption(creationDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).isEqualTo(optionDTO);
        assertThat(response.getBody().getMessage()).isEqualTo("Product option created successfully");
        verify(productOptionService).createOption(creationDTO);
    }

    @Test
    void updateOption_returnsOkWithUpdatedOption() {
        // Arrange
        ProductOptionDTO updatedDTO = new ProductOptionDTO(1L, "Size", List.of(), null, null);
        when(productOptionService.updateOption(1L, updateDTO)).thenReturn(updatedDTO);

        // Act
        ResponseEntity<ApiResponse<ProductOptionDTO>> response = controller.updateOption(1L, updateDTO);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData().name()).isEqualTo("Size");
        assertThat(response.getBody().getMessage()).isEqualTo("Product option updated successfully");
        verify(productOptionService).updateOption(1L, updateDTO);
    }

    @Test
    void deleteOption_returnsNoContent() {
        // Arrange
        doNothing().when(productOptionService).deleteOption(1L);

        // Act
        ResponseEntity<ApiResponse<Void>> response = controller.deleteOption(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(productOptionService).deleteOption(1L);
    }

    @Test
    void getOptionValues_returnsOkWithValuesList() {
        // Arrange
        List<ProductOptionValueDTO> values = List.of(valueDTO);
        when(productOptionValueService.getOptionValues(1L)).thenReturn(values);

        // Act
        ResponseEntity<ApiResponse<List<ProductOptionValueDTO>>> response = controller.getOptionValues(1L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getData()).hasSize(1);
        assertThat(response.getBody().getData().get(0).value()).isEqualTo("Red");
        assertThat(response.getBody().getMessage()).isEqualTo("Product option values retrieved successfully");
        verify(productOptionValueService).getOptionValues(1L);
    }

    @Test
    void getOptionValues_throwsExceptionWhenOptionIdMissing() {
        // Act & Assert
        assertThatThrownBy(() -> controller.getOptionValues(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("optionId parameter is required");
        verify(productOptionValueService, never()).getOptionValues(any());
    }

    @Test
    void getOptionValues_returnsEmptyList() {
        // Arrange
        when(productOptionValueService.getOptionValues(999L)).thenReturn(List.of());

        // Act
        ResponseEntity<ApiResponse<List<ProductOptionValueDTO>>> response = controller.getOptionValues(999L);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getData()).isEmpty();
        verify(productOptionValueService).getOptionValues(999L);
    }

    @Test
    void getOption_callsServiceWithCorrectId() {
        // Arrange
        when(productOptionService.getOptionById(5L)).thenReturn(optionDTO);

        // Act
        controller.getOptionById(5L);

        // Assert
        verify(productOptionService).getOptionById(5L);
    }

    @Test
    void createOption_passesCorrectDTOToService() {
        // Arrange
        ProductOptionCreationDTO dto = new ProductOptionCreationDTO(1L, "Material");
        when(productOptionService.createOption(dto)).thenReturn(optionDTO);

        // Act
        controller.createOption(dto);

        // Assert
        verify(productOptionService).createOption(dto);
    }

    @Test
    void updateOption_passesCorrectParametersToService() {
        // Arrange
        ProductOptionUpdateDTO updateDTOTest = new ProductOptionUpdateDTO("Weight");
        ProductOptionDTO updatedDTO = new ProductOptionDTO(1L, "Weight", List.of(), null, null);
        when(productOptionService.updateOption(2L, updateDTOTest)).thenReturn(updatedDTO);

        // Act
        controller.updateOption(2L, updateDTOTest);

        // Assert
        verify(productOptionService).updateOption(2L, updateDTOTest);
    }

    @Test
    void deleteOption_passesCorrectIdToService() {
        // Arrange
        doNothing().when(productOptionService).deleteOption(3L);

        // Act
        controller.deleteOption(3L);

        // Assert
        verify(productOptionService).deleteOption(3L);
    }

    @Test
    void getOptions_passesCorrectPageableToService() {
        // Arrange
        Pageable pageable = PageRequest.of(1, 20);
        PageResponseDTO<ProductOptionDTO> pageResponse = new PageResponseDTO<>(
                List.of(), 1, 20, 0, 0, false, true, true
        );
        when(productOptionService.getOptions(pageable)).thenReturn(pageResponse);

        // Act
        controller.getOptions(pageable);

        // Assert
        verify(productOptionService).getOptions(pageable);
    }
}
