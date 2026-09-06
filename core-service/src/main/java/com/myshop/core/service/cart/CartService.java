package com.myshop.core.service.cart;

import com.myshop.commons.exception.BusinessException;
import com.myshop.core.constant.CoreMessageKeys;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import com.myshop.core.dto.request.CartItemRequest;
import com.myshop.core.dto.response.CartItemResponse;
import com.myshop.core.dto.response.CartResponse;
import com.myshop.core.entity.cart.Cart;
import com.myshop.core.entity.cart.CartItem;
import com.myshop.core.entity.catalog.Product;
import com.myshop.core.repository.CartItemRepository;
import com.myshop.core.repository.CartRepository;
import com.myshop.core.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartResponse getByProfileId(Long profileId) {
        Cart cart = cartRepository.findByProfileId(profileId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.CART_NOT_FOUND)
                ));
        List<CartItemResponse> items = cartItemRepository.findByCartId(cart.getCartId()).stream()
                .map(this::toItemResponse).toList();
        return toResponse(cart, items);
    }

    @Transactional
    public CartResponse addItem(Long profileId, CartItemRequest request) {
        Cart cart = cartRepository.findByProfileId(profileId)
                .orElseGet(() -> {
                    Cart c = Cart.builder().profileId(profileId).totalPrice(BigDecimal.ZERO).build();
                    return cartRepository.save(c);
                });

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.PRODUCT_NOT_FOUND)
                ));

        BigDecimal unitPrice = product.getSpecialPrice() != null ? product.getSpecialPrice() : product.getPrice();

        CartItem existing = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), request.getProductId())
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            existing.setUnitPrice(unitPrice);
            existing.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(existing.getQuantity())));
            existing.setProductName(product.getProductName());
            cartItemRepository.save(existing);
        } else {
            CartItem item = CartItem.builder()
                    .cartId(cart.getCartId())
                    .productId(request.getProductId())
                    .quantity(request.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(unitPrice.multiply(BigDecimal.valueOf(request.getQuantity())))
                    .productName(product.getProductName())
                    .build();
            cartItemRepository.save(item);
        }

        recalculateTotal(cart);
        return getByProfileId(profileId);
    }

    @Transactional
    public CartResponse updateItem(Long profileId, Long cartItemId, int quantity) {
        Cart cart = cartRepository.findByProfileId(profileId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.CART_NOT_FOUND)
                ));
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.CART_ITEM_NOT_FOUND)
                ));
        if (!item.getCartId().equals(cart.getCartId())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CoreMessageKeys.CART_ITEM_NOT_IN_CART)
            );
        }
        item.setQuantity(quantity);
        item.setTotalPrice(item.getUnitPrice().multiply(BigDecimal.valueOf(quantity)));
        cartItemRepository.save(item);
        recalculateTotal(cart);
        return getByProfileId(profileId);
    }

    @Transactional
    public CartResponse removeItem(Long profileId, Long cartItemId) {
        Cart cart = cartRepository.findByProfileId(profileId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.CART_NOT_FOUND)
                ));
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.CART_ITEM_NOT_FOUND)
                ));
        if (!item.getCartId().equals(cart.getCartId())) {
            throw new BusinessException(
                    ErrorCode.VALIDATION_ERROR,
                    MessageHandlerUtils.getMessage(CoreMessageKeys.CART_ITEM_NOT_IN_CART)
            );
        }
        cartItemRepository.delete(item);
        recalculateTotal(cart);
        return getByProfileId(profileId);
    }

    @Transactional
    public void clearCart(Long cartId) {
        cartItemRepository.deleteByCartId(cartId);
        Cart cart = cartRepository.findById(cartId).orElse(null);
        if (cart != null) {
            cart.setTotalPrice(BigDecimal.ZERO);
            cartRepository.save(cart);
        }
    }

    private void recalculateTotal(Cart cart) {
        List<CartItem> items = cartItemRepository.findByCartId(cart.getCartId());
        BigDecimal total = items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cart.setTotalPrice(total);
        cartRepository.save(cart);
    }

    private CartResponse toResponse(Cart cart, List<CartItemResponse> items) {
        return CartResponse.builder()
                .cartId(cart.getCartId())
                .profileId(cart.getProfileId())
                .totalPrice(cart.getTotalPrice())
                .items(items)
                .build();
    }

    private CartItemResponse toItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .cartItemId(item.getCartItemId())
                .productId(item.getProductId())
                .productName(item.getProductName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .build();
    }
}
