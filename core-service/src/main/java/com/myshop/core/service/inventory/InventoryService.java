package com.myshop.core.service.inventory;

import com.myshop.commons.exception.BusinessException;
import com.myshop.core.constant.CoreMessageKeys;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import com.myshop.core.dto.request.InventoryRequest;
import com.myshop.core.dto.response.InventoryResponse;
import com.myshop.core.entity.inventory.Inventory;
import com.myshop.core.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository inventoryRepository;

    public InventoryResponse getByProductId(Long productId) {
        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.INVENTORY_NOT_FOUND)
                ));
        return toResponse(inv);
    }

    @Transactional
    public InventoryResponse setAvailable(InventoryRequest request) {
        Inventory inv = inventoryRepository.findByProductId(request.getProductId())
                .orElse(Inventory.builder()
                        .productId(request.getProductId())
                        .available(0)
                        .reserved(0)
                        .build());
        inv.setAvailable(request.getAvailable());
        inv.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inv);
        return toResponse(inv);
    }

    @Transactional
    public void reserveStock(Long productId, int qty) {
        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.INVENTORY_NOT_FOUND)
                ));
        if (inv.getAvailable() < qty) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CoreMessageKeys.INSUFFICIENT_STOCK, productId)
            );
        }
        inv.setAvailable(inv.getAvailable() - qty);
        inv.setReserved(inv.getReserved() + qty);
        inv.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inv);
    }

    @Transactional
    public void releaseStock(Long productId, int qty) {
        Inventory inv = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.INVENTORY_NOT_FOUND)
                ));
        inv.setReserved(Math.max(0, inv.getReserved() - qty));
        inv.setAvailable(inv.getAvailable() + qty);
        inv.setUpdatedAt(LocalDateTime.now());
        inventoryRepository.save(inv);
    }

    private InventoryResponse toResponse(Inventory inv) {
        return InventoryResponse.builder()
                .inventoryId(inv.getInventoryId())
                .productId(inv.getProductId())
                .available(inv.getAvailable())
                .reserved(inv.getReserved())
                .updatedAt(inv.getUpdatedAt())
                .build();
    }
}
