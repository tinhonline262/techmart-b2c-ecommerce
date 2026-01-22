package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.entity.Product;
import com.shopping.microservices.product_service.entity.ProductOption;
import com.shopping.microservices.product_service.entity.ProductOptionCombination;
import com.shopping.microservices.product_service.entity.ProductOptionValue;
import com.shopping.microservices.product_service.repository.CategoryRepository;
import com.shopping.microservices.product_service.repository.ProductCategoryRepository;
import com.shopping.microservices.product_service.repository.ProductImageRepository;
import com.shopping.microservices.product_service.repository.ProductOptionCombinationRepository;
import com.shopping.microservices.product_service.repository.ProductOptionRepository;
import com.shopping.microservices.product_service.repository.ProductOptionValueRepository;
import com.shopping.microservices.product_service.dto.ProductDetailDTO;
import com.shopping.microservices.product_service.dto.ProductOptionDTO;
import com.shopping.microservices.product_service.dto.ProductOptionValueDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.mock;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductMapperTest {

    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private ProductOptionValueRepository productOptionValueRepository;
    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    private ProductOptionValueMapper productOptionValueMapper;
    private ProductOptionCombinationMapper productOptionCombinationMapper;

    private ProductMapper productMapper;

    @BeforeEach
    public void setup() {
        productOptionValueMapper = new ProductOptionValueMapper();
        productOptionCombinationMapper = new ProductOptionCombinationMapper(productOptionValueMapper);
        // other mappers used in ProductMapper - simple real instances or mocks
        var brandMapper = new BrandMapper();
        var categoryMapper = new CategoryMapper(mock(CategoryRepository.class), productCategoryRepository);
        var productImageMapper = new ProductImageMapper();

        productMapper = new ProductMapper(
                brandMapper,
                categoryMapper,
                productImageMapper,
                productCategoryRepository,
                productImageRepository,
                productOptionRepository,
                productOptionValueRepository,
                productOptionCombinationRepository,
                productOptionValueMapper,
                productOptionCombinationMapper
        );
    }

    @Test
    void toDetailDTO_loadsOptionsAndCombinations() {
        var product = Product.builder().id(1L).name("Test").sku("P-1").build();

        var optSize = ProductOption.builder().id(10L).name("Size").product(product).build();
        var optColor = ProductOption.builder().id(11L).name("Color").product(product).build();

        var valSizeM = ProductOptionValue.builder().id(100L).productOption(optSize).value("M").build();
        var valColorBlack = ProductOptionValue.builder().id(101L).productOption(optColor).value("Black").build();

        var combination = ProductOptionCombination.builder().id(200L).product(product).value("Size=M;Color=Black").sku("P-1-M-Black").build();

        when(productCategoryRepository.findByProductId(1L)).thenReturn(List.of());
        when(productImageRepository.findByProductId(1L)).thenReturn(List.of());
        when(productOptionRepository.findByProductId(1L)).thenReturn(List.of(optSize, optColor));
        when(productOptionValueRepository.findByProductOptionId(10L)).thenReturn(List.of(valSizeM));
        when(productOptionValueRepository.findByProductOptionId(11L)).thenReturn(List.of(valColorBlack));
        when(productOptionValueRepository.findByProductOptionProductId(1L)).thenReturn(List.of(valSizeM, valColorBlack));
        when(productOptionCombinationRepository.findByProductId(1L)).thenReturn(List.of(combination));

        ProductDetailDTO dto = productMapper.toDetailDTO(product);

        assertNotNull(dto);
        assertEquals("P-1", dto.sku());

        List<ProductOptionDTO> options = dto.options();
        assertEquals(2, options.size());

        // find size option
        var sizeOpt = options.stream().filter(o -> "Size".equals(o.name())).findFirst().orElse(null);
        assertNotNull(sizeOpt);
        assertEquals(1, sizeOpt.values().size());
        assertEquals("M", sizeOpt.values().get(0).value());

        // verify option values mapping
        var allVals = productOptionValueRepository.findByProductOptionProductId(1L);
        var mappedVals = allVals.stream().map(productOptionValueMapper::toDTO).toList();
        assertEquals(2, mappedVals.size());
        assertEquals("Size", mappedVals.get(0).optionName());
        assertEquals("M", mappedVals.get(0).value());
        assertEquals("Color", mappedVals.get(1).optionName());
        assertEquals("Black", mappedVals.get(1).value());
    }
}