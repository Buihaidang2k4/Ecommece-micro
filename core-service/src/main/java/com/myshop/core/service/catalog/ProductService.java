package com.myshop.core.service.catalog;

import com.myshop.commons.dto.ApiResponse;
import com.myshop.commons.exception.BusinessException;
import com.myshop.commons.exception.ErrorCode;
import com.myshop.commons.exception.MessageHandlerUtils;
import com.myshop.core.client.FileServiceClient;
import com.myshop.core.constant.CoreMessageKeys;
import com.myshop.core.constant.StorageBuckets;
import com.myshop.core.dto.file.PresignGetResponseDto;
import com.myshop.core.dto.file.PresignUploadResponseDto;
import com.myshop.core.dto.request.ProductImagePresignRequest;
import com.myshop.core.dto.request.ProductImageRequest;
import com.myshop.core.dto.request.ProductRequest;
import com.myshop.core.dto.response.MediaPresignResponse;
import com.myshop.core.dto.response.ProductImageResponse;
import com.myshop.core.dto.response.ProductResponse;
import com.myshop.core.dto.response.ProductSearchRow;
import com.myshop.core.entity.catalog.Product;
import com.myshop.core.entity.catalog.ProductImage;
import com.myshop.core.mapper.ProductQueryMapper;
import com.myshop.core.repository.ProductImageRepository;
import com.myshop.core.repository.ProductRepository;
import com.myshop.core.util.ObjectKeyUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final ProductQueryMapper productQueryMapper;
    private final FileServiceClient fileServiceClient;

    public List<ProductSearchRow> search(String name, Long categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
        return productQueryMapper.searchProducts(name, categoryId, minPrice, maxPrice);
    }

    public ProductResponse getById(Long id) {
        Product p = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.PRODUCT_NOT_FOUND)
                ));
        return toResponse(p, mapImages(id));
    }

    public ProductResponse getBySlug(String slug) {
        Product p = productRepository.findBySlug(slug)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.PRODUCT_NOT_FOUND)
                ));
        return toResponse(p, mapImages(p.getProductId()));
    }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = Product.builder()
                .categoryId(request.getCategoryId())
                .productName(request.getProductName())
                .description(request.getDescription())
                .price(request.getPrice())
                .specialPrice(request.getSpecialPrice())
                .discount(request.getDiscount())
                .bio(request.getBio())
                .slug(request.getSlug())
                .height(request.getHeight())
                .length(request.getLength())
                .weight(request.getWeight())
                .width(request.getWidth())
                .origin(request.getOrigin())
                .soldCount(0)
                .reviewCount(0)
                .avgRating(0.0)
                .createAt(LocalDateTime.now())
                .updateAt(LocalDateTime.now())
                .build();
        productRepository.save(product);
        return toResponse(product, List.of());
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.PRODUCT_NOT_FOUND)
                ));
        if (request.getCategoryId() != null) product.setCategoryId(request.getCategoryId());
        if (request.getProductName() != null) product.setProductName(request.getProductName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getSpecialPrice() != null) product.setSpecialPrice(request.getSpecialPrice());
        if (request.getDiscount() != null) product.setDiscount(request.getDiscount());
        if (request.getBio() != null) product.setBio(request.getBio());
        if (request.getSlug() != null) product.setSlug(request.getSlug());
        if (request.getHeight() != null) product.setHeight(request.getHeight());
        if (request.getLength() != null) product.setLength(request.getLength());
        if (request.getWeight() != null) product.setWeight(request.getWeight());
        if (request.getWidth() != null) product.setWidth(request.getWidth());
        if (request.getOrigin() != null) product.setOrigin(request.getOrigin());
        product.setUpdateAt(LocalDateTime.now());
        productRepository.save(product);
        return toResponse(product, mapImages(id));
    }

    @Transactional
    @CacheEvict(value = "products", key = "#id")
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    MessageHandlerUtils.getMessage(CoreMessageKeys.PRODUCT_NOT_FOUND)
            );
        }
        List<ProductImage> images = productImageRepository.findByProductId(id);
        for (ProductImage image : images) {
            fileServiceClient.delete(StorageBuckets.PRODUCT_FILES, image.getObjectKey());
        }
        productImageRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }

    public MediaPresignResponse presignImage(Long productId, ProductImagePresignRequest request) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    MessageHandlerUtils.getMessage(CoreMessageKeys.PRODUCT_NOT_FOUND)
            );
        }
        String objectKey = ObjectKeyUtils.productImageKey(productId, request.getFileName());
        ApiResponse<PresignUploadResponseDto> response = fileServiceClient.presignUpload(
                StorageBuckets.PRODUCT_FILES,
                objectKey,
                request.getContentType()
        );
        PresignUploadResponseDto data = requireData(response);
        return MediaPresignResponse.builder()
                .bucket(StorageBuckets.PRODUCT_FILES)
                .objectKey(data.getObjectKey() != null ? data.getObjectKey() : objectKey)
                .uploadUrl(data.getUploadUrl())
                .build();
    }

    @Transactional
    @CacheEvict(value = "products", key = "#productId")
    public ProductImageResponse addImage(Long productId, ProductImageRequest request) {
        if (!productRepository.existsById(productId)) {
            throw new BusinessException(
                    ErrorCode.RESOURCE_NOT_FOUND,
                    MessageHandlerUtils.getMessage(CoreMessageKeys.PRODUCT_NOT_FOUND)
            );
        }
        if (!StringUtils.hasText(request.getObjectKey())) {
            throw new BusinessException(ErrorCode.VALIDATION_ERROR, "objectKey is required");
        }
        ProductImage image = ProductImage.builder()
                .productId(productId)
                .fileName(request.getFileName())
                .fileType(request.getFileType())
                .objectKey(request.getObjectKey())
                .build();
        productImageRepository.save(image);
        return toImageResponse(image);
    }

    @Transactional
    public void deleteImage(Long imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.RESOURCE_NOT_FOUND,
                        MessageHandlerUtils.getMessage(CoreMessageKeys.IMAGE_NOT_FOUND)
                ));
        fileServiceClient.delete(StorageBuckets.PRODUCT_FILES, image.getObjectKey());
        productImageRepository.deleteById(imageId);
    }

    private List<ProductImageResponse> mapImages(Long productId) {
        return productImageRepository.findByProductId(productId).stream()
                .map(this::toImageResponse)
                .toList();
    }

    private ProductResponse toResponse(Product p, List<ProductImageResponse> images) {
        return ProductResponse.builder()
                .productId(p.getProductId())
                .categoryId(p.getCategoryId())
                .productName(p.getProductName())
                .description(p.getDescription())
                .price(p.getPrice())
                .specialPrice(p.getSpecialPrice())
                .discount(p.getDiscount())
                .bio(p.getBio())
                .slug(p.getSlug())
                .height(p.getHeight())
                .length(p.getLength())
                .weight(p.getWeight())
                .width(p.getWidth())
                .origin(p.getOrigin())
                .soldCount(p.getSoldCount())
                .reviewCount(p.getReviewCount())
                .avgRating(p.getAvgRating())
                .images(images)
                .build();
    }

    private ProductImageResponse toImageResponse(ProductImage img) {
        String url = null;
        if (StringUtils.hasText(img.getObjectKey())) {
            ApiResponse<PresignGetResponseDto> response =
                    fileServiceClient.presignGet(StorageBuckets.PRODUCT_FILES, img.getObjectKey());
            PresignGetResponseDto data = response != null ? response.getData() : null;
            url = data != null ? data.getUrl() : null;
        }
        return ProductImageResponse.builder()
                .id(img.getId())
                .fileName(img.getFileName())
                .fileType(img.getFileType())
                .objectKey(img.getObjectKey())
                .url(url)
                .build();
    }

    private static <T> T requireData(ApiResponse<T> response) {
        if (response == null || response.getData() == null) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "file-service returned empty response");
        }
        return response.getData();
    }
}
