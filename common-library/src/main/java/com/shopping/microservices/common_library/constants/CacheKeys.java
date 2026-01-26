package com.shopping.microservices.common_library.constants;

/**
 * Cache key prefixes and TTL constants used across microservices.
 * 
 * Provides consistent cache key naming and TTL configuration
 * for both L1 (Caffeine) and L2 (Redis) caches.
 */
public final class CacheKeys {

    // ===========================================
    // Cache Key Prefixes
    // ===========================================
    
    /**
     * Prefix for inventory cache keys
     */
    public static final String INVENTORY_PREFIX = "inventory:";
    
    /**
     * Prefix for product cache keys
     */
    public static final String PRODUCT_PREFIX = "product:";
    
    /**
     * Prefix for order cache keys
     */
    public static final String ORDER_PREFIX = "order:";
    
    /**
     * Prefix for payment cache keys
     */
    public static final String PAYMENT_PREFIX = "payment:";
    
    /**
     * Prefix for customer cache keys
     */
    public static final String CUSTOMER_PREFIX = "customer:";
    
    /**
     * Prefix for category cache keys
     */
    public static final String CATEGORY_PREFIX = "category:";
    
    /**
     * Prefix for brand cache keys
     */
    public static final String BRAND_PREFIX = "brand:";
    
    /**
     * Prefix for search results cache keys
     */
    public static final String SEARCH_PREFIX = "search:";
    
    /**
     * Prefix for session cache keys
     */
    public static final String SESSION_PREFIX = "session:";
    
    /**
     * Prefix for rate limiting cache keys
     */
    public static final String RATE_LIMIT_PREFIX = "rate-limit:";
    
    /**
     * Prefix for distributed lock keys
     */
    public static final String LOCK_PREFIX = "lock:";

    // ===========================================
    // Cache Names (for @Cacheable annotations)
    // ===========================================
    
    public static final String CACHE_PRODUCTS = "products";
    public static final String CACHE_PRODUCT_BY_ID = "productById";
    public static final String CACHE_CATEGORIES = "categories";
    public static final String CACHE_CATEGORY_BY_ID = "category_by_id";
    public static final String CACHE_CATEGORY_BY_SLUG = "category_by_slug";
    public static final String CACHE_PUBLISHED_CATEGORIES = "published_categories";
    public static final String CACHE_BRANDS = "brands";
    public static final String CACHE_BRAND_BY_ID = "brandById";
    public static final String CACHE_BRAND_BY_SLUG = "brandBySlug";
    public static final String CACHE_PUBLISHED_BRANDS = "publishedBrands";
    public static final String CACHE_SEARCH_RESULTS = "searchResults";
    public static final String CACHE_PRICE_RANGE = "priceRange";
    public static final String CACHE_TOP_PRODUCTS = "topProducts";

    // ===========================================
    // Default TTL Values (in seconds)
    // ===========================================
    
    /**
     * Default TTL for cache entries (1 hour)
     */
    public static final int DEFAULT_TTL_SECONDS = 3600;
    
    /**
     * TTL for product cache (10 minutes)
     * Products change occasionally
     */
    public static final int PRODUCT_TTL_SECONDS = 600;
    
    /**
     * TTL for product by ID cache (15 minutes)
     * Individual product lookups
     */
    public static final int PRODUCT_BY_ID_TTL_SECONDS = 900;
    
    /**
     * TTL for category cache (1 hour)
     * Categories are relatively stable
     */
    public static final int CATEGORY_TTL_SECONDS = 3600;
    
    /**
     * TTL for brand cache (1 hour)
     * Brands are relatively stable
     */
    public static final int BRAND_TTL_SECONDS = 3600;
    
    /**
     * TTL for search results (5 minutes)
     * Search results can change frequently
     */
    public static final int SEARCH_TTL_SECONDS = 300;
    
    /**
     * TTL for price range cache (3 minutes)
     * Prices can fluctuate
     */
    public static final int PRICE_RANGE_TTL_SECONDS = 180;
    
    /**
     * TTL for inventory cache (3 minutes)
     * Inventory changes frequently
     */
    public static final int INVENTORY_TTL_SECONDS = 180;
    
    /**
     * TTL for order cache (10 minutes)
     */
    public static final int ORDER_TTL_SECONDS = 600;
    
    /**
     * TTL for session cache (30 minutes)
     */
    public static final int SESSION_TTL_SECONDS = 1800;
    
    /**
     * TTL for rate limiting (1 minute window)
     */
    public static final int RATE_LIMIT_TTL_SECONDS = 60;
    
    /**
     * TTL for distributed locks (30 seconds)
     */
    public static final int LOCK_TTL_SECONDS = 30;

    // ===========================================
    // L1 (Caffeine) Specific Settings
    // ===========================================
    
    /**
     * Maximum size for L1 cache (number of entries)
     */
    public static final int L1_MAX_SIZE = 10_000;
    
    /**
     * L1 cache expire after write (5 minutes)
     */
    public static final int L1_EXPIRE_AFTER_WRITE_SECONDS = 300;
    
    /**
     * L1 cache expire after access (3 minutes)
     */
    public static final int L1_EXPIRE_AFTER_ACCESS_SECONDS = 180;

    // ===========================================
    // Helper Methods
    // ===========================================
    
    /**
     * Build a product cache key
     */
    public static String productKey(Long productId) {
        return PRODUCT_PREFIX + productId;
    }
    
    /**
     * Build a product cache key by SKU
     */
    public static String productKeyBySku(String sku) {
        return PRODUCT_PREFIX + "sku:" + sku;
    }
    
    /**
     * Build an inventory cache key
     */
    public static String inventoryKey(Long productId) {
        return INVENTORY_PREFIX + productId;
    }
    
    /**
     * Build an inventory cache key by SKU
     */
    public static String inventoryKeyBySku(String sku) {
        return INVENTORY_PREFIX + "sku:" + sku;
    }
    
    /**
     * Build an order cache key
     */
    public static String orderKey(Long orderId) {
        return ORDER_PREFIX + orderId;
    }
    
    /**
     * Build an order cache key by order number
     */
    public static String orderKeyByNumber(String orderNumber) {
        return ORDER_PREFIX + "number:" + orderNumber;
    }
    
    /**
     * Build a category cache key
     */
    public static String categoryKey(Long categoryId) {
        return CATEGORY_PREFIX + categoryId;
    }
    
    /**
     * Build a lock key
     */
    public static String lockKey(String resource, Object id) {
        return LOCK_PREFIX + resource + ":" + id;
    }

    // Private constructor to prevent instantiation
    private CacheKeys() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
