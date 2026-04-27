package com.hutech.buiduongtin.api.controller;

import com.hutech.buiduongtin.controller.GlobalControllerAdvice;
import com.hutech.buiduongtin.model.Category;
import com.hutech.buiduongtin.model.Product;
import com.hutech.buiduongtin.repository.CategoryRepository;
import com.hutech.buiduongtin.repository.OrderDetailRepository;
import com.hutech.buiduongtin.repository.ProductRepository;
import com.hutech.buiduongtin.service.CategoryService;
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

@WebMvcTest(controllers = ApiCategoryController.class, excludeFilters = @Filter(type = FilterType.ASSIGNABLE_TYPE, classes = GlobalControllerAdvice.class))
@AutoConfigureMockMvc(addFilters = false)
@Import({CategoryService.class, ProductService.class, ImageStorageService.class})
class ApiCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryRepository categoryRepository;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private OrderDetailRepository orderDetailRepository;

    @Test
    void shouldReturnCategoryTree() throws Exception {
        Category root = new Category();
        root.setId(1L);
        root.setName("Laptop");

        Category child = new Category();
        child.setId(2L);
        child.setName("Gaming");
        child.setParentCategory(root);
        root.setChildren(List.of(child));

        when(categoryRepository.findByParentCategoryIsNull()).thenReturn(List.of(root));

        mockMvc.perform(get("/api/v1/categories/tree").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].children[0].id").value(2));
    }

    @Test
    void shouldReturnCategoryChildren() throws Exception {
        Category root = new Category();
        root.setId(1L);
        root.setName("Phu kien");

        Category child = new Category();
        child.setId(3L);
        child.setName("Tai nghe");
        child.setParentCategory(root);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(root));
        when(categoryRepository.findByParentCategoryId(1L)).thenReturn(List.of(child));

        mockMvc.perform(get("/api/v1/categories/1/children"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(3))
                .andExpect(jsonPath("$.data[0].parentId").value(1));
    }

    @Test
    void shouldReturnCategoryProductsWithNormalizedSort() throws Exception {
        Category category = new Category();
        category.setId(8L);
        category.setName("Tablet");

        Product product = new Product();
        product.setId(9L);
        product.setName("iPad Air");
        product.setPrice(2000);
        product.setStockQuantity(4);
        product.setCategory(category);

        when(categoryRepository.findById(8L)).thenReturn(Optional.of(category));
        when(productRepository.findPageByCategoryIdOrParentId(eq(8L), any()))
                .thenReturn(new PageImpl<>(List.of(product), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/v1/categories/8/products")
                        .param("sort", "unknown,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.items[0].id").value(9))
                .andExpect(jsonPath("$.data.sort").value("name,asc"));
    }
}
