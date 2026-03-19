package com.hutech.buiduongtin.service;

import com.hutech.buiduongtin.model.Product;
import com.hutech.buiduongtin.repository.ProductRepository;
import com.hutech.buiduongtin.repository.OrderDetailRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.constraints.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    // Retrieve all products from the database
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // Retrieve promotion products only
    public List<Product> getPromotionProducts() {
        return productRepository.findByPromotionTypeIn(java.util.Arrays.asList("DISCOUNT", "GIFT"));
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

    // Add a new product to the database
    public Product addProduct(@NonNull Product product, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String imageFileName = saveImage(imageFile);
            product.setImage(imageFileName);
        }
        return productRepository.save(product);
    }

    // Update an existing product
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
            String imageFileName = saveImage(imageFile);
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
    public void deleteProductById(@NonNull Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        orderDetailRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }

    // Helper method to save the image file
    private String saveImage(MultipartFile imageFile) throws IOException {
        String uploadDir = "src/main/resources/static/images/";
        Path uploadPath = Paths.get(uploadDir);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(imageFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return fileName;
    }
}