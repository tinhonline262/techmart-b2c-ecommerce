package com.shopping.microservices.product_service.client;

import com.shopping.microservices.product_service.dto.ApiResponse;
import com.shopping.microservices.product_service.dto.InventoryDTO;
import com.shopping.microservices.product_service.dto.PageResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client to communicate with inventory-service
 */
@FeignClient(name = "inventory-service")
public interface InventoryServiceClient {

    /**
     * Get all inventory records with pagination
     */
    @GetMapping("/api/v1/inventory")
    ApiResponse<PageResponseDTO<InventoryDTO>> getAllInventory(Pageable pageable);

    /**
     * Get inventory by SKU
     */
    @GetMapping("/api/v1/inventory/sku")
    ApiResponse<List<InventoryDTO>> getInventoryBySku(@RequestParam String sku);

    /**
     * Get inventory by product ID
     */
    @GetMapping("/api/v1/inventory/product")
    ApiResponse<List<InventoryDTO>> getInventoryByProductId(@RequestParam Long productId);

    /**
     * Get inventory by warehouse ID
     */
    @GetMapping("/api/v1/inventory/warehouse")
    ApiResponse<PageResponseDTO<InventoryDTO>> getInventoryByWarehouse(
            @RequestParam Long warehouseId,
            Pageable pageable);

    /**
     * Get low stock items
     */
    @GetMapping("/api/v1/inventory/low-stock")
    ApiResponse<PageResponseDTO<InventoryDTO>> getLowStockInventory(Pageable pageable);

    /**
     * Get out of stock items
     */
    @GetMapping("/api/v1/inventory/out-of-stock")
    ApiResponse<PageResponseDTO<InventoryDTO>> getOutOfStockInventory(Pageable pageable);
}
