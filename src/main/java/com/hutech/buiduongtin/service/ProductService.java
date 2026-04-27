package com.hutech.buiduongtin.service;

import com.hutech.buiduongtin.model.Product;
import com.hutech.buiduongtin.repository.OrderDetailRepository;
import com.hutech.buiduongtin.repository.ProductRepository;
import com.hutech.buiduongtin.repository.specification.ProductSpecifications;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ImageStorageService imageStorageService;

    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Retrieve promotion products only
    @Cacheable("promotionProducts")
    public List<Product> getPromotionProducts() {
        return productRepository.findAll(ProductSpecifications.promotionOnly(true));
    }

    // Lọc sản phẩm theo danh mục (hỗ trợ cả cấp 1 và cấp 2)
    public List<Product> getProductsByCategory(Long categoryId) {
        if (categoryId == null)
            return productRepository.findAll();
        return productRepository.findByCategoryIdOrParentId(categoryId);
    }

    // Retrieve a product by its id
    public Optional<Product> getProductById(@NonNull Long id) {
        return productRepository.findById(id);
    }

    public Page<Product> searchProducts(String keyword, Long categoryId, Double minPrice, Double maxPrice,
            Boolean promotionOnly, Boolean inStock, Pageable pageable) {
        Specification<Product> specification = ProductSpecifications.withFilters(keyword, categoryId, minPrice,
                maxPrice, promotionOnly, inStock);
        return productRepository.findAll(specification, pageable);
    }

    public List<Product> getRelatedProducts(Long productId, Long categoryId, int limit) {
        if (categoryId == null) {
            return List.of();
        }
        int safeLimit = Math.min(Math.max(limit, 1), 20);
        Pageable pageable = PageRequest.of(0, safeLimit, Sort.by(Sort.Direction.DESC, "id"));
        return productRepository.findByCategoryIdAndIdNot(categoryId, productId, pageable).getContent();
    }

    public Page<Product> getProductsByCategory(Long categoryId, Pageable pageable) {
        if (categoryId == null) {
            return productRepository.findAll(pageable);
        }
        return productRepository.findPageByCategoryIdOrParentId(categoryId, pageable);
    }

    // Add a new product to the database
    @CacheEvict(value = "promotionProducts", allEntries = true)
    public Product addProduct(@NonNull Product product, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageFileName = imageStorageService.store(imageFile);
            product.setImage(imageFileName);
        }
        return productRepository.save(product);
    }

    // Update an existing product
    @CacheEvict(value = "promotionProducts", allEntries = true)
    public Product updateProduct(@NotNull Product product, MultipartFile imageFile) throws IOException {
        Long id = product.getId();
        if (id == null) {
            throw new IllegalArgumentException("Product ID cannot be null for update.");
        }

        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Product with ID " +
                        id + " does not exist."));

        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setCategory(product.getCategory());
        existingProduct.setPromotionType(product.getPromotionType());
        existingProduct.setDiscountPercent(product.getDiscountPercent());
        existingProduct.setGiftDescription(product.getGiftDescription());
        existingProduct.setStockQuantity(product.getStockQuantity());

        if (imageFile != null && !imageFile.isEmpty()) {
            String imageFileName = imageStorageService.store(imageFile);
            existingProduct.setImage(imageFileName);
        }

        return productRepository.save(existingProduct);
    }

    // Giảm số lượng tồn kho sau khi đặt hàng
    @Transactional
    @SuppressWarnings("null")
    public void decrementStock(Long productId, int qty) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalStateException("Product not found: " + productId));
        int newQty = Math.max(0, product.getStockQuantity() - qty);
        product.setStockQuantity(newQty);
        productRepository.save(product);
    }

    // Delete a product by its id
    @CacheEvict(value = "promotionProducts", allEntries = true)
    public void deleteProductById(@NonNull Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        orderDetailRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }
}
