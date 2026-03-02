package com.shopping.microservices.cart_service.service.impl;

import com.shopping.microservices.cart_service.client.ProductServiceClient;
import com.shopping.microservices.cart_service.dto.ApiResponse;
import com.shopping.microservices.cart_service.dto.ProductDTO;
import com.shopping.microservices.cart_service.dto.ProductSummaryDTO;
import com.shopping.microservices.cart_service.service.ProductService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Implementation of ProductService that fetches product data from Product Service via Feign Client
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductServiceClient productServiceClient;

    @Override
    @Cacheable(value = "product", key = "#productId", unless = "#result == null")
    public ProductDTO getProductById(Long productId) {
        log.debug("Fetching product details for productId: {}", productId);
        
        try {
            ApiResponse<ProductDTO> response = productServiceClient.getProductById(productId);
            
            if (response != null && response.getData() != null) {
                log.debug("Successfully fetched product: {}", response.getData().getName());
                return response.getData();
            }
            
            log.warn("No product data returned for productId: {}", productId);
            return null;
            
        } catch (FeignException.NotFound e) {
            log.warn("Product not found with id: {}", productId);
            return null;
        } catch (FeignException e) {
            log.error("Error fetching product with id: {}. Status: {}, Message: {}", 
                    productId, e.status(), e.getMessage());
            return null;
        } catch (Exception e) {
            log.error("Unexpected error fetching product with id: {}", productId, e);
            return null;
        }
    }

    @Override
    @Cacheable(value = "products", key = "#productIds.hashCode()", unless = "#result.isEmpty()")
    public Map<Long, ProductDTO> getProductsByIds(List<Long> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            log.debug("No product IDs provided, returning empty map");
            return Collections.emptyMap();
        }
        
        log.debug("Fetching product details for {} products", productIds.size());
        
        try {
            ApiResponse<List<ProductSummaryDTO>> response = productServiceClient.getProductsByIds(productIds);
            
            if (response != null && response.getData() != null && !response.getData().isEmpty()) {
                Map<Long, ProductDTO> productMap = response.getData().stream()
                        .collect(Collectors.toMap(
                                ProductSummaryDTO::id,
                                psd -> ProductDTO.builder()
                                        .id(psd.id())
                                        .name(psd.name())
                                        .price(psd.price())

                                        .build()
                        ));
                
                log.debug("Successfully fetched {} products", productMap.size());
                return productMap;
            }
            
            log.warn("No products returned for ids: {}", productIds);
            return Collections.emptyMap();
            
        } catch (FeignException e) {
            log.error("Error fetching products for ids: {}. Status: {}, Message: {}", 
                    productIds, e.status(), e.getMessage());
            return Collections.emptyMap();
        } catch (Exception e) {
            log.error("Unexpected error fetching products for ids: {}", productIds, e);
            return Collections.emptyMap();
        }
    }
}
