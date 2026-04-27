package com.hutech.buiduongtin.api.controller;

import com.hutech.buiduongtin.controller.GlobalControllerAdvice;
import com.hutech.buiduongtin.model.Category;
import com.hutech.buiduongtin.model.Product;
import com.hutech.buiduongtin.repository.OrderDetailRepository;
import com.hutech.buiduongtin.repository.ProductRepository;
import com.hutech.buiduongtin.service.ImageStorageService;
import com.hutech.buiduongtin.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ApiProductController.class, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalControllerAdvice.class))
@AutoConfigureMockMvc(addFilters = false)
@Import({ProductService.class, ImageStorageService.class})
class ApiProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private OrderDetailRepository orderDetailRepository;

    @Test
    void shouldSanitizeSortAndReturnPaginationMetadata() throws Exception {
        Product product = product(1L, "iPhone 16");
        when(productRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(product), PageRequest.of(0, 1), 2));

        mockMvc.perform(get("/api/v1/products")
                        .param("keyword", " iphone ")
                        .param("categoryId", "1")
                        .param("minPrice", "100")
                        .param("maxPrice", "2000")
                        .param("promotionOnly", "true")
                        .param("inStock", "true")
                        .param("page", "0")
                        .param("size", "1")
                        .param("sort", "dropTable,desc")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sort").value("name,desc"))
                .andExpect(jsonPath("$.data.hasNext").value(true))
                .andExpect(jsonPath("$.data.hasPrevious").value(false))
                .andExpect(jsonPath("$.data.nextPage").value(1))
                .andExpect(jsonPath("$.data.previousPage").doesNotExist());
    }

    @Test
    void shouldReturnProductDetailWithBreadcrumbs() throws Exception {
        Category root = new Category();
        root.setId(10L);
        root.setName("Dien thoai");

        Category child = new Category();
        child.setId(11L);
        child.setName("iPhone");
        child.setParentCategory(root);

        Product product = product(5L, "iPhone 16 Pro");
        product.setCategory(child);

        Product related = product(6L, "iPhone 15 Pro");
        related.setCategory(child);

        when(productRepository.findById(5L)).thenReturn(Optional.of(product));
        when(productRepository.findByCategoryIdAndIdNot(11L, 5L, PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "id"))))
                .thenReturn(new PageImpl<>(List.of(related)));

        mockMvc.perform(get("/api/v1/products/5/detail"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.product.id").value(5))
                .andExpect(jsonPath("$.data.category.id").value(11))
                .andExpect(jsonPath("$.data.breadcrumbs[0].id").value(10))
                .andExpect(jsonPath("$.data.breadcrumbs[1].id").value(11))
                .andExpect(jsonPath("$.data.relatedProducts[0].id").value(6));
    }

    private Product product(Long id, String name) {
        Product product = new Product();
        product.setId(id);
        product.setName(name);
        product.setPrice(1000);
        product.setDescription("desc");
        product.setImage("a.jpg");
        product.setStockQuantity(10);
        return product;
    }
}
