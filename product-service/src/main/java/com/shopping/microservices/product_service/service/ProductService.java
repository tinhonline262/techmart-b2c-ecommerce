package com.shopping.microservices.product_service.service;


import com.shopping.microservices.product_service.dto.*;
import com.shopping.microservices.product_service.dto.ProductCreationDTO;
import com.shopping.microservices.product_service.dto.ProductDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;



import java.math.BigDecimal;
import java.util.List;

public interface ProductService {
    public ProductDTO findById(long id);
    public ProductDTO findBySku(String sku);
    public List<ProductDTO> findAll();
    public ProductDTO createProduct(ProductCreationDTO productCreationDTO);
    public ProductDTO updateProduct(Long id, ProductUpdateDTO productUpdateDTO);
    public void deleteProduct(Long id);
    public ProductDTO updateProductQuantity(Long id, InventoryUpdateDTO inventoryUpdateDTO);
    public ProductDTO subtractProductQuantity(Long id, InventorySubtractDTO inventorySubtractDTO);
    public ProductDTO reverseProductStockBySku(ProductReduceStockDTO productDTO);

    /**
     * Find published products with multi-criteria filtering
     */
    Page<ProductSummaryDTO> findPublishedProducts(
            List<Long> categoryIds,
            List<Long> brandIds,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            Boolean inStock,
            String sortBy,
            String sortDirection,
            Pageable pageable);

    /**
     * Find featured products (published and marked as featured)
     */
    List<FeaturedProductDTO> findFeaturedProducts(int limit);

    /**
     * Find featured products by specific IDs
     */
    List<FeaturedProductDTO> findFeaturedProductsByIds(List<Long> ids);

    /**
     * Find published product by ID
     */
    ProductSummaryDTO findPublishedProductById(Long id);

    /**
     * Find published product detail by ID (includes images, attributes, categories,
     * brand)
     */
    ProductDetailDTO findPublishedProductDetailById(Long id);

    /**
     * Find product variations (SKU combinations) by product ID
     */
    List<ProductVariationDTO> findProductVariations(Long productId);

    /**
     * Find related products by product ID
     */
    List<ProductRelatedDTO> findRelatedProducts(Long productId, int limit);

    /**
     * Find published product by slug (SEO-friendly URL)
     */
    ProductDetailDTO findPublishedProductBySlug(String slug);

    /**
     * Get product slug by ID
     */
    String getProductSlugById(Long id);

    /**
     * Get option values for a specific product
     */
    List<ProductOptionValueDTO> getProductOptionValues(Long productId);

    /**
     * Get option combinations (SKU variants) for a specific product
     */
    List<ProductOptionCombinationDTO> getProductOptionCombinations(Long productId);
}