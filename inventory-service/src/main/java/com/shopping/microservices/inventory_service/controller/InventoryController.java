package com.shopping.microservices.inventory_service.controller;

import com.shopping.microservices.common_library.dto.ApiResponse;
import com.shopping.microservices.inventory_service.dto.InventoryAdjustmentRequest;
import com.shopping.microservices.inventory_service.dto.InventoryDTO;
import com.shopping.microservices.inventory_service.entity.Inventory;
import com.shopping.microservices.inventory_service.mapper.InventoryMapper;
import com.shopping.microservices.inventory_service.repository.InventoryRepository;
import com.shopping.microservices.inventory_service.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Validated
@Slf4j
public class InventoryController {

    private final InventoryRepository inventoryRepository;
    private final InventoryService inventoryService;
    private final InventoryMapper inventoryMapper;

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<InventoryDTO>> getInventoryByProductId(@PathVariable Long productId) {
        log.info("Getting inventory for productId: {}", productId);

        return inventoryRepository.findByProductId(productId)
                .map(inventory -> {
                    InventoryDTO dto = inventoryMapper.toDTO(inventory);
                    return ResponseEntity.ok(ApiResponse.<InventoryDTO>success(dto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<ApiResponse<InventoryDTO>> getInventoryBySku(@PathVariable String sku) {
        log.info("Getting inventory for SKU: {}", sku);

        return inventoryRepository.findBySku(sku)
                .map(inventory -> {
                    InventoryDTO dto = inventoryMapper.toDTO(inventory);
                    return ResponseEntity.ok(ApiResponse.<InventoryDTO>success(dto));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/adjust")
    public ResponseEntity<ApiResponse<String>> adjustInventory(
            @Valid @RequestBody InventoryAdjustmentRequest request) {
        log.info("Adjusting inventory for productId: {}, quantity: {}",
                request.productId(), request.adjustedQuantity());

        try {
            inventoryService.updateProductQuantity(
                    request.productId(),
                    request.warehouseId(),
                    request.adjustedQuantity(),
                    request.note()
            );
            return ResponseEntity.ok(ApiResponse.success("Inventory adjusted successfully"));
        } catch (Exception e) {
            log.error("Error adjusting inventory: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.<String>builder()
                            .success(false)
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getLowStockItems(
            @RequestParam(defaultValue = "10") Long threshold) {
        log.info("Getting low stock items with threshold: {}", threshold);

        List<Inventory> lowStockItems = inventoryRepository.findLowStockItems(threshold);
        List<InventoryDTO> dtos = lowStockItems.stream()
                .map(inventoryMapper::toDTO)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(dtos));
    }

    @GetMapping("/check-stock")
    public ResponseEntity<ApiResponse<Boolean>> checkStock(
            @RequestParam String sku,
            @RequestParam(required = false) Long quantity) {
        log.info("Checking stock for SKU: {}, quantity: {}", sku, quantity);

        boolean inStock = quantity != null
                ? inventoryService.isInStock(sku, quantity)
                : inventoryService.isInStock(sku);

        return ResponseEntity.ok(ApiResponse.success(inStock));
    }
}
