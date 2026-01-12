package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.ProductRelatedDTO;
import com.shopping.microservices.product_service.entity.Product;
import com.shopping.microservices.product_service.entity.ProductRelated;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductRelatedMapper {

    /**
     * Map ProductRelated entity to ProductRelatedDTO
     */
    public ProductRelatedDTO toDTO(ProductRelated related) {
        if (related == null) return null;

        Product relatedProduct = related.getRelatedProduct();
        if (relatedProduct == null) return null;

        return new ProductRelatedDTO(
                relatedProduct.getId(),
                relatedProduct.getName(),
                relatedProduct.getSlug(),
                relatedProduct.getPrice(),
                null, // oldPrice
                null, // thumbnailUrl
                null, // averageRating
                null, // reviewCount
                relatedProduct.getStockQuantity() != null && relatedProduct.getStockQuantity() > 0
        );
    }

    /**
     * Map list of ProductRelated entities to list of ProductRelatedDTOs
     */
    public List<ProductRelatedDTO> toDTOList(List<ProductRelated> relatedProducts) {
        if (relatedProducts == null) return Collections.emptyList();
        return relatedProducts.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
