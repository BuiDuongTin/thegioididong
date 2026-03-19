package com.hutech.buiduongtin;

import com.hutech.buiduongtin.model.Category;
import com.hutech.buiduongtin.model.Product;
import com.hutech.buiduongtin.model.Role;
import com.hutech.buiduongtin.repository.CategoryRepository;
import com.hutech.buiduongtin.repository.OrderDetailRepository;
import com.hutech.buiduongtin.repository.OrderRepository;
import com.hutech.buiduongtin.repository.ProductRepository;
import com.hutech.buiduongtin.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    // Authentication dependencies temporarily disabled
    // private final UserRepository userRepository;
    // private final RoleRepository roleRepository;
    // private final PasswordEncoder passwordEncoder;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final OrderRepository orderRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        ensureRoles();
        // initRolesAndUsers(); // Disabled until Auth module is implemented
        initCategories();
        initSampleProducts();
    }

    private void ensureRoles() {
        if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_ADMIN", "Quản trị viên", new HashSet<>()));
        }
        if (roleRepository.findByName("ROLE_USER").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_USER", "Khách hàng", new HashSet<>()));
        }
        if (roleRepository.findByName("ROLE_MANAGER").isEmpty()) {
            roleRepository.save(new Role(null, "ROLE_MANAGER", "Quản lý sản phẩm", new HashSet<>()));
        }
    }

    /*
     * private void initRolesAndUsers() {
     * if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
     * roleRepository.save(new Role(null, "ROLE_ADMIN", "Quản trị viên", new
     * HashSet<>()));
     * }
     * if (roleRepository.findByName("ROLE_USER").isEmpty()) {
     * roleRepository.save(new Role(null, "ROLE_USER", "Khách hàng", new
     * HashSet<>()));
     * }
     * if (!userRepository.existsByUsername("admin")) {
     * Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseThrow();
     * Set<Role> roles = new HashSet<>();
     * roles.add(adminRole);
     * userRepository.save(new User(null, "admin",
     * passwordEncoder.encode("admin123"),
     * "admin@dangvantai.com", "0900000000", roles));
     * }
     * if (!userRepository.existsByUsername("user")) {
     * Role userRole = roleRepository.findByName("ROLE_USER").orElseThrow();
     * Set<Role> roles = new HashSet<>();
     * roles.add(userRole);
     * userRepository.save(new User(null, "user", passwordEncoder.encode("user123"),
     * "user@dangvantai.com", "0900000001", roles));
     * }
     * }
     */

    @SuppressWarnings("null")
    private void initCategories() {
        // Kiểm tra dữ liệu cũ (không có icon = schema cũ chưa có cấp 2)
        boolean hasOldData = categoryRepository.findByParentCategoryIsNull()
                .stream().anyMatch(c -> c.getIcon() == null);

        // Force reload if we don't have the new accessories subcategories (total is 35
        // now)
        if (hasOldData || categoryRepository.count() < 35) {
            // Xóa đúng thứ tự để tránh FK constraint:
            // order_details -> orders -> products -> categories
            orderDetailRepository.deleteAll();
            orderRepository.deleteAll();
            productRepository.deleteAll();
            categoryRepository.deleteAll();
        } else {
            return; // Đã có dữ liệu đủ 35+ danh mục
        }

        // --- Cấp 1: Danh mục chính ---
        Category dienthoai = categoryRepository.save(new Category("Điện thoại", "bi-phone", null));
        Category laptop = categoryRepository.save(new Category("Laptop", "bi-laptop", null));
        Category tablet = categoryRepository.save(new Category("Máy tính bảng", "bi-tablet", null));
        Category dongho = categoryRepository.save(new Category("Đồng hồ", "bi-watch", null));
        Category phu_kien = categoryRepository.save(new Category("Phụ kiện", "bi-headphones", null));

        // --- Cấp 2: Danh mục con ---
        categoryRepository.saveAll(List.of(new Category("iPhone", "bi-apple", dienthoai),
                new Category("Samsung", "bi-phone-flip", dienthoai),
                new Category("Xiaomi", "bi-phone-vibrate", dienthoai),
                new Category("OPPO", "bi-phone-landscape", dienthoai), new Category("Realme", "bi-phone", dienthoai)));

        categoryRepository.saveAll(List.of(new Category("MacBook", "bi-apple", laptop),
                new Category("Laptop Dell", "bi-pc-display", laptop), new Category("Laptop ASUS", "bi-laptop", laptop),
                new Category("Laptop Lenovo", "bi-pc", laptop), new Category("Laptop HP", "bi-display", laptop)));

        categoryRepository.saveAll(List.of(new Category("iPad", "bi-apple", tablet),
                new Category("Samsung Tab", "bi-tablet-landscape", tablet),
                new Category("Xiaomi Pad", "bi-tablet", tablet)));

        categoryRepository.saveAll(List.of(new Category("Apple Watch", "bi-apple", dongho),
                new Category("Samsung Watch", "bi-watch", dongho), new Category("Garmin", "bi-smartwatch", dongho)));

        categoryRepository.saveAll(List.of(
                // Tai nghe, Loa
                new Category("Tai nghe Bluetooth", "bi-earbuds", phu_kien),
                new Category("Tai nghe chụp tai", "bi-headphones", phu_kien),
                new Category("Loa", "bi-speaker", phu_kien),
                // Camera
                new Category("Camera Giám Sát", "bi-camera-video", phu_kien),
                new Category("Camera ngoài trời", "bi-webcam", phu_kien),
                // Phụ kiện gaming
                new Category("Chuột gaming", "bi-mouse", phu_kien),
                new Category("Bàn phím gaming", "bi-keyboard", phu_kien),
                new Category("Tai nghe gaming", "bi-headset", phu_kien),
                // Thiết bị lưu trữ
                new Category("Ổ cứng di động", "bi-hdd", phu_kien), new Category("Thẻ nhớ", "bi-sd-card", phu_kien),
                new Category("USB", "bi-usb-drive", phu_kien),
                // Phụ kiện khác
                new Category("Sạc & Cáp", "bi-plug", phu_kien), new Category("Ốp lưng", "bi-phone", phu_kien),
                new Category("Pin dự phòng", "bi-battery-charging", phu_kien)));

        System.out.println("✓ Khởi tạo danh mục 2 cấp thành công!");
    }

    @SuppressWarnings("null")
    private void initSampleProducts() {
        if (productRepository.count() > 0) {
            // Fix existing products to have correct subcategories
            fixProductCategories();
            return;
        }

        List<Category> allCats = categoryRepository.findAll();
        if (allCats.isEmpty())
            return;

        // Tìm danh mục con
        Category catIphone = findCat(allCats, "iPhone");
        Category catSamsung = findCat(allCats, "Samsung");
        Category catXiaomi = findCat(allCats, "Xiaomi");
        Category catOppo = findCat(allCats, "OPPO");
        Category catRealme = findCat(allCats, "Realme");

        Category catMac = findCat(allCats, "MacBook");
        Category catDell = findCat(allCats, "Laptop Dell");
        Category catAsus = findCat(allCats, "Laptop ASUS");
        Category catLenovo = findCat(allCats, "Laptop Lenovo");

        // Tạo sản phẩm mẫu
        productRepository.saveAll(List.of(
                createProduct("iPhone 16 Pro Max", 33990000, "Flagship mới nhất từ Apple", "iphone16promax.png",
                        catIphone, true, 10),
                createProduct("iPhone 15 Pro", 27990000, "Apple iPhone 15 Pro 256GB", "iphone15pro.jpg", catIphone,
                        false, 0),
                createProduct("Samsung Galaxy S25 Ultra", 31990000, "Samsung S25 Ultra 512GB",
                        "dfda4936-393b-49df-b252-c89bc89cc4bc_samsunggalaxys25utra.jpg", catSamsung, true,
                        15),
                createProduct("Samsung Galaxy A55", 9490000, "Samsung A55 5G 128GB", "samsunggalaxyA55.jpg", catSamsung,
                        false, 0),
                createProduct("Xiaomi 14T Pro", 18990000, "Xiaomi 14T Pro 12GB/512GB",
                        "5de116ef-760d-4213-8885-b2c76518c9e9_Xiaomi 14T Pro.jpg", catXiaomi, true, 20),
                createProduct("OPPO Find X8", 21990000, "OPPO Find X8 5G 256GB", "iphone16promax.png", catOppo, false,
                        0),
                createProduct("Realme GT 7 Pro", 14990000, "Realme GT 7 Pro 256GB",
                        "faf0bde3-fa8f-4eb8-9aaa-34b88f59051a_Realme GT 7 Pro.jpg", catRealme, true, 5),
                createProduct("MacBook Pro M4", 54990000, "MacBook Pro 14 inch M4 2024",
                        "93c01d36-be4b-4ce8-9996-14d15e01a3e2_MacBook Pro M4.jpg", catMac, true, 8),
                createProduct("MacBook Air M3", 32990000, "MacBook Air 13 inch M3",
                        "98294152-2f10-468b-9a1b-cf0297b0ee2a_MacBook Air M3.jpg", catMac, false, 0),
                createProduct("Dell XPS 15", 45990000, "Dell XPS 15 OLED i9 RTX 4070",
                        "355f9dc9-fcd2-4079-ac97-33a360f19031_Dell XPS 15.jpg", catDell, true, 12),
                createProduct("ASUS ROG Zephyrus G14", 42990000, "ASUS ROG Zephyrus G14 RTX 4070",
                        "7f6c0479-b9ee-4d17-9649-dd35a65a2b62_ASUS ROG Zephyrus G14.jpg", catAsus, false,
                        0),
                createProduct("Lenovo ThinkPad X1 Carbon", 38990000, "Lenovo ThinkPad X1 Carbon 2024",
                        "21ee60d7-a357-417c-a7d6-0adf6878574b_Lenovo ThinkPad X1 Carbon.jpg", catLenovo,
                        true, 7)));

        System.out.println("✓ Khởi tạo sản phẩm mẫu thành công!");
    }

    private Category findCat(List<Category> cats, String name) {
        return cats.stream().filter(c -> c.getName().equals(name)).findFirst().orElse(cats.get(0));
    }

    private void fixProductCategories() {
        List<Product> products = productRepository.findAll();
        List<Category> cats = categoryRepository.findAll();
        boolean changed = false;
        for (Product p : products) {
            String n = p.getName();
            String catName = null;
            if (n.contains("iPhone"))
                catName = "iPhone";
            else if (n.contains("Samsung"))
                catName = "Samsung";
            else if (n.contains("Xiaomi"))
                catName = "Xiaomi";
            else if (n.contains("OPPO"))
                catName = "OPPO";
            else if (n.contains("Realme"))
                catName = "Realme";
            else if (n.contains("MacBook"))
                catName = "MacBook";
            else if (n.contains("Dell"))
                catName = "Laptop Dell";
            else if (n.contains("ASUS"))
                catName = "Laptop ASUS";
            else if (n.contains("Lenovo"))
                catName = "Laptop Lenovo";

            if (catName != null) {
                Category correctCat = findCat(cats, catName);
                if (!p.getCategory().getId().equals(correctCat.getId())) {
                    p.setCategory(correctCat);
                    changed = true;
                }
            }
        }
        if (changed) {
            productRepository.saveAll(products);
            System.out.println("✓ Đã cập nhật lại danh mục con cho các sản phẩm mẫu!");
        }
    }

    private Product createProduct(String name, double price, String desc, String image,
            Category cat, boolean isPromotion, int discount) {
        Product p = new Product();
        p.setName(name);
        p.setPrice(price);
        p.setDescription(desc);
        p.setImage(image);
        p.setCategory(cat);
        p.setPromotionType(isPromotion ? "DISCOUNT" : "NONE");
        p.setDiscountPercent(discount);
        p.setStockQuantity(50); // Số lượng mặc định khi seed dữ liệu
        return p;
    }
}
