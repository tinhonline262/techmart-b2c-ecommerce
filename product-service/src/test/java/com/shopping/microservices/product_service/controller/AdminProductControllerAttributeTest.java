//package com.shopping.microservices.product_service.controller;
//
//import com.shopping.microservices.product_service.dto.*;
//import com.shopping.microservices.product_service.service.ProductAttributeGroupService;
//import com.shopping.microservices.product_service.service.ProductAttributeService;
//import com.shopping.microservices.product_service.service.ProductOptionService;
//import com.shopping.microservices.product_service.service.ProductOptionValueService;
//import com.shopping.microservices.product_service.service.ProductService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AdminProductControllerAttributeTest {
//
//    @Mock
//    private ProductService productService;
//
//    @Mock
//    private ProductOptionService productOptionService;
//
//    @Mock
//    private ProductOptionValueService productOptionValueService;
//
//    @Mock
//    private ProductAttributeService productAttributeService;
//
//    @Mock
//    private ProductAttributeGroupService productAttributeGroupService;
//
//    @InjectMocks
//    private AdminProductController controller;
//
//    private ProductAttributeDTO attributeDTO;
//    private ProductAttributeGroupDTO attributeGroupDTO;
//    private ProductAttributeCreationDTO attributeCreationDTO;
//    private ProductAttributeUpdateDTO attributeUpdateDTO;
//    private ProductAttributeGroupCreationDTO groupCreationDTO;
//    private ProductAttributeGroupUpdateDTO groupUpdateDTO;
//
//    @BeforeEach
//    void setUp() {
//        attributeDTO = new ProductAttributeDTO(1L, "Material", null, "GroupName", null, null);
//        attributeGroupDTO = new ProductAttributeGroupDTO(1L, "Clothing Attributes", List.of(), null, null);
//        attributeCreationDTO = new ProductAttributeCreationDTO("Material", null);
//        attributeUpdateDTO = new ProductAttributeUpdateDTO("Fabric", null);
//        groupCreationDTO = new ProductAttributeGroupCreationDTO("Clothing Attributes");
//        groupUpdateDTO = new ProductAttributeGroupUpdateDTO("Fashion Attributes");
//    }
//
//    // ============ ATTRIBUTE TESTS ============
//
//    @Test
//    void getAttributes_returnsOkWithPagedAttributes() {
//        // Arrange
//        Pageable pageable = PageRequest.of(0, 10);
//        PageResponseDTO<ProductAttributeDTO> pageResponse = new PageResponseDTO<>(
//                List.of(attributeDTO), 0, 10, 1, 1, true, false, false
//        );
//        when(productAttributeService.getAttributes(pageable)).thenReturn(pageResponse);
//
//        // Act
//        ResponseEntity<ApiResponse<PageResponseDTO<ProductAttributeDTO>>> response = controller.getAttributes(pageable);
//
//        // Assert
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isEqualTo(pageResponse);
//        assertThat(response.getBody().getMessage()).isEqualTo("Product attributes retrieved successfully");
//        verify(productAttributeService).getAttributes(pageable);
//    }
//
//    @Test
//    void getAttributeById_returnsOkWithAttribute() {
//        // Arrange
//        when(productAttributeService.getAttributeById(1L)).thenReturn(attributeDTO);
//
//        // Act
//        ResponseEntity<ApiResponse<ProductAttributeDTO>> response = controller.getAttributeById(1L);
//
//        // Assert
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isEqualTo(attributeDTO);
//        assertThat(response.getBody().getMessage()).isEqualTo("Product attribute retrieved successfully");
//        verify(productAttributeService).getAttributeById(1L);
//    }
//
//    @Test
//    void createAttribute_returnsCreatedWithNewAttribute() {
//        // Arrange
//        when(productAttributeService.createAttribute(attributeCreationDTO)).thenReturn(attributeDTO);
//
//        // Act
//        ResponseEntity<ApiResponse<ProductAttributeDTO>> response = controller.createAttribute(attributeCreationDTO);
//
//        // Assert
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isEqualTo(attributeDTO);
//        assertThat(response.getBody().getMessage()).isEqualTo("Product attribute created successfully");
//        verify(productAttributeService).createAttribute(attributeCreationDTO);
//    }
//
//    @Test
//    void updateAttribute_returnsOkWithUpdatedAttribute() {
//        // Arrange
//        when(productAttributeService.updateAttribute(1L, attributeUpdateDTO)).thenReturn(attributeDTO);
//
//        // Act
//        ResponseEntity<ApiResponse<ProductAttributeDTO>> response = controller.updateAttribute(1L, attributeUpdateDTO);
//
//        // Assert
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isEqualTo(attributeDTO);
//        assertThat(response.getBody().getMessage()).isEqualTo("Product attribute updated successfully");
//        verify(productAttributeService).updateAttribute(1L, attributeUpdateDTO);
//    }
//
//    @Test
//    void deleteAttribute_returnsNoContent() {
//        // Arrange
//        doNothing().when(productAttributeService).deleteAttribute(1L);
//
//        // Act
//        ResponseEntity<ApiResponse<Void>> response = controller.deleteAttribute(1L);
//
//        // Assert
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//        verify(productAttributeService).deleteAttribute(1L);
//    }
//
//    // ============ ATTRIBUTE GROUP TESTS ============
//
//    @Test
//    void getAttributeGroups_returnsOkWithPagedAttributeGroups() {
//        // Arrange
//        Pageable pageable = PageRequest.of(0, 10);
//        PageResponseDTO<ProductAttributeGroupDTO> pageResponse = new PageResponseDTO<>(
//                List.of(attributeGroupDTO), 0, 10, 1, 1, true, false, false
//        );
//        when(productAttributeGroupService.getAttributeGroups(pageable)).thenReturn(pageResponse);
//
//        // Act
//        ResponseEntity<ApiResponse<PageResponseDTO<ProductAttributeGroupDTO>>> response = controller.getAttributeGroups(pageable);
//
//        // Assert
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isEqualTo(pageResponse);
//        assertThat(response.getBody().getMessage()).isEqualTo("Product attribute groups retrieved successfully");
//        verify(productAttributeGroupService).getAttributeGroups(pageable);
//    }
//
//    @Test
//    void getAttributeGroupById_returnsOkWithAttributeGroup() {
//        // Arrange
//        when(productAttributeGroupService.getAttributeGroupById(1L)).thenReturn(attributeGroupDTO);
//
//        // Act
//        ResponseEntity<ApiResponse<ProductAttributeGroupDTO>> response = controller.getAttributeGroupById(1L);
//
//        // Assert
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isEqualTo(attributeGroupDTO);
//        assertThat(response.getBody().getMessage()).isEqualTo("Product attribute group retrieved successfully");
//        verify(productAttributeGroupService).getAttributeGroupById(1L);
//    }
//
//    @Test
//    void createAttributeGroup_returnsCreatedWithNewAttributeGroup() {
//        // Arrange
//        when(productAttributeGroupService.createAttributeGroup(groupCreationDTO)).thenReturn(attributeGroupDTO);
//
//        // Act
//        ResponseEntity<ApiResponse<ProductAttributeGroupDTO>> response = controller.createAttributeGroup(groupCreationDTO);
//
//        // Assert
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isEqualTo(attributeGroupDTO);
//        assertThat(response.getBody().getMessage()).isEqualTo("Product attribute group created successfully");
//        verify(productAttributeGroupService).createAttributeGroup(groupCreationDTO);
//    }
//
//    @Test
//    void updateAttributeGroup_returnsOkWithUpdatedAttributeGroup() {
//        // Arrange
//        when(productAttributeGroupService.updateAttributeGroup(1L, groupUpdateDTO)).thenReturn(attributeGroupDTO);
//
//        // Act
//        ResponseEntity<ApiResponse<ProductAttributeGroupDTO>> response = controller.updateAttributeGroup(1L, groupUpdateDTO);
//
//        // Assert
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
//        assertThat(response.getBody()).isNotNull();
//        assertThat(response.getBody().getData()).isEqualTo(attributeGroupDTO);
//        assertThat(response.getBody().getMessage()).isEqualTo("Product attribute group updated successfully");
//        verify(productAttributeGroupService).updateAttributeGroup(1L, groupUpdateDTO);
//    }
//
//    @Test
//    void deleteAttributeGroup_returnsNoContent() {
//        // Arrange
//        doNothing().when(productAttributeGroupService).deleteAttributeGroup(1L);
//
//        // Act
//        ResponseEntity<ApiResponse<Void>> response = controller.deleteAttributeGroup(1L);
//
//        // Assert
//        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
//        verify(productAttributeGroupService).deleteAttributeGroup(1L);
//    }
//}
