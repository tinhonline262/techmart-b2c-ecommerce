package com.shopping.microservices.product_service.mapper;

import com.shopping.microservices.product_service.dto.CategoryDTO;
import com.shopping.microservices.product_service.dto.ProductCreationDTO;
import com.shopping.microservices.product_service.dto.ProductDTO;
import com.shopping.microservices.product_service.dto.ProductImageDTO;
import com.shopping.microservices.product_service.entity.Category;
import com.shopping.microservices.product_service.entity.Product;
import com.shopping.microservices.product_service.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProductMapper {
    public Product mapToEntity(ProductCreationDTO productDTO, Category category) {
        Product product = Product.builder()
                .name(productDTO.name())
                .description(productDTO.description())
                .sku(productDTO.sku())
                .price(productDTO.price())
                .quantityInStock(productDTO.quantityInStock())
                .brand(productDTO.brand())
                .category(category)
                .isActive(productDTO.isActive())
                .build();
        if (productDTO.productImages() != null) {
            var images = productDTO.productImages().stream()
                    .map(imageDTO -> ProductImage.builder()
                            .imageUrl(imageDTO.imageUrl())
                            .isPrimary(imageDTO.isPrimary())
                            .displayOrder(imageDTO.displayOrder())
                            .product(product)
                                    .build()
                            )
                    .collect(Collectors.toSet());
            product.setProductImages(images);
        }
        return product;
    }
    public ProductDTO mapToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getPrice(),
                product.getQuantityInStock(),
                mapCategory(product.getCategory()),
                product.getBrand(),
                mapImages(product.getProductImages()),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private CategoryDTO mapCategory(Category category) {
        if (category == null) return null;
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription()
        );
    }

    private Set<ProductImageDTO> mapImages(Set<ProductImage> images) {
        if (images == null) return Set.of();
        return images.stream()
                .map(img -> new ProductImageDTO(
                        img.getId(),
                        img.getImageUrl(),
                        img.getIsPrimary(),
                        img.getDisplayOrder(),
                        img.getCreatedAt()
                ))
                .collect(Collectors.toSet());
    }
}
