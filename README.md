# Hệ thống web bán điện thoại và thiết bị công nghệ

Đây là ứng dụng thương mại điện tử xây bằng Spring Boot theo phong cách Thế Giới Di Động. Hệ thống hiện hỗ trợ cả giao diện MVC/Thymeleaf và lớp REST API riêng cho client, bao phủ các luồng chính như danh mục, sản phẩm, tài khoản, giỏ hàng, đặt hàng, voucher OTP email và thanh toán MoMo Sandbox.

## Kiến trúc chính

- `controller`: xử lý giao diện Thymeleaf.
- `api/controller`: cung cấp REST API cho `auth`, `product`, `category`, `system`.
- `service`: nghiệp vụ giỏ hàng, đơn hàng, danh mục, sản phẩm, MoMo, mail, upload ảnh.
- `repository`: truy cập dữ liệu qua Spring Data JPA.
- `api/dto`, `api/mapper`, `api/util`: tách riêng request/response, mapper và helper cho API.
- `templates`, `static`: giao diện Thymeleaf, CSS, JS, ảnh tĩnh.

## Công nghệ

- Java 17
- Spring Boot 3.1.5
- Spring MVC + Thymeleaf
- Spring Security
- Spring Data JPA
- Spring Validation
- Spring Cache
- MySQL
- Spring Mail
- springdoc OpenAPI / Swagger UI

## Tối ưu đã áp dụng

- Chuẩn hóa `sort` cho API sản phẩm và sản phẩm theo danh mục bằng whitelist field hợp lệ: `id`, `name`, `price`, `stockQuantity`.
- Bổ sung breadcrumb cha-con trong `GET /api/v1/products/{id}/detail`.
- Bổ sung endpoint:
  - `GET /api/v1/categories/{id}/children`
  - `GET /api/v1/categories/{id}/products`
- Chuẩn hóa metadata phân trang với `hasNext`, `hasPrevious`, `nextPage`, `previousPage`.
- Tách upload ảnh ra `ImageStorageService` để tái sử dụng cho `Product` và `Category`.
- Kiểm tra định dạng file upload, chỉ cho phép ảnh phổ biến như `jpg`, `jpeg`, `png`, `webp`, `gif`.
- Thêm cache cho category tree và danh sách sản phẩm khuyến mãi.
- Bổ sung logging nghiệp vụ cho checkout và tạo link thanh toán MoMo.
- Mở rộng health check `GET /api/v1/system/ping` để trả thêm trạng thái database và image storage.
- Chuẩn hóa thêm lỗi API phổ biến `400`, `401`, `403`, `404`, `409`.
- Thêm index JPA cho các cột truy vấn nhiều như `products.category_id`, `products.promotion_type`, `orders.user_id`, `orders.phone_number`, `categories.parent_id`.
- Bổ sung test `MockMvc` cho các API trọng điểm của `auth`, `product`, `category`.

## Chức năng hiện có

### 1. Danh mục và sản phẩm

- Danh mục hỗ trợ cấu trúc cha-con.
- Seed dữ liệu mẫu khi khởi động cho vai trò, danh mục và sản phẩm.
- Sản phẩm có tên, giá, mô tả, ảnh, tồn kho, loại khuyến mãi, phần trăm giảm giá, quà tặng.
- API sản phẩm hỗ trợ filter theo `keyword`, `categoryId`, `minPrice`, `maxPrice`, `promotionOnly`, `inStock`.
- API chi tiết sản phẩm trả về:
  - thông tin sản phẩm
  - danh mục hiện tại
  - breadcrumb danh mục
  - sản phẩm liên quan

### 2. Tài khoản và phân quyền

- `ROLE_ADMIN`: quản trị hệ thống và danh mục.
- `ROLE_MANAGER`: quản lý sản phẩm.
- `ROLE_USER`: mua hàng, xem điểm, đổi voucher, đặt hàng.
- API auth dùng session-based authentication:
  - `POST /api/v1/auth/register`
  - `POST /api/v1/auth/login`

### 3. Giỏ hàng và đặt hàng

- Giỏ hàng lưu theo session.
- Tạo `Order` và `OrderDetail` từ giỏ hàng.
- Tự giảm tồn kho sau khi tạo đơn.
- Hỗ trợ tra cứu lịch sử đơn theo tài khoản hoặc số điện thoại.

### 4. Điểm thưởng, voucher và OTP

- Cộng điểm theo giá trị đơn hàng.
- Dùng điểm để giảm phí ship.
- Đổi voucher qua OTP email với thời hạn xác thực.
- Voucher chỉ hợp lệ nếu chưa dùng và thuộc đúng người dùng.

### 5. Thanh toán

- Hình thức hỗ trợ:
  - `COD`
  - `BANK_TRANSFER`
  - `MOMO`
- Payment status đã được chuẩn hóa nội bộ bằng enum: `PENDING`, `PAID`, `FAILED`, `REDEEMED`.
- MoMo có logging cho request lỗi và trạng thái tạo `payUrl`.

## Các API nổi bật

- `GET /api/v1/products`
- `GET /api/v1/products/{id}`
- `GET /api/v1/products/{id}/detail`
- `GET /api/v1/categories`
- `GET /api/v1/categories/{id}`
- `GET /api/v1/categories/tree`
- `GET /api/v1/categories/{id}/children`
- `GET /api/v1/categories/{id}/products`
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `GET /api/v1/system/ping`

## Quy tắc nghiệp vụ

- Phí giao hàng mặc định: `30.000đ`.
- Miễn phí giao hàng nếu tổng tiền hàng lớn hơn `1.000.000đ` và có từ `2` sản phẩm trở lên.
- Dùng `10` điểm để giảm `10.000đ` phí giao hàng.
- Voucher hợp lệ được trừ trực tiếp vào tổng tiền đơn.
- Tồn kho bị trừ ngay khi đơn hàng được tạo.

## Dữ liệu và hạ tầng

- Database mặc định: MySQL.
- Hibernate đang dùng `ddl-auto=update`.
- Ảnh upload lưu tại `src/main/resources/static/images`.
- Health check xác minh được cả DB và thư mục lưu ảnh.
- OpenAPI UI có sẵn qua Swagger.

## Kiểm thử

- Đã có test `MockMvc` cho:
  - `GET /api/v1/products`
  - `GET /api/v1/products/{id}/detail`
  - `GET /api/v1/categories/tree`
  - `GET /api/v1/categories/{id}/children`
  - `GET /api/v1/categories/{id}/products`
  - `POST /api/v1/auth/register`
  - `POST /api/v1/auth/login`

Chạy test:

```bash
./mvnw test
```

Trên Windows:

```bash
mvnw.cmd test
```

## Chạy ứng dụng

Yêu cầu:

- Java 17
- Maven
- MySQL

Cấu hình trong `src/main/resources/application.properties`:

- `spring.datasource.url`
- `spring.datasource.username`
- `spring.datasource.password`
- `spring.mail.username`
- `spring.mail.password`

Chạy:

```bash
./mvnw spring-boot:run
```

Trên Windows:

```bash
mvnw.cmd spring-boot:run
```

Mặc định ứng dụng chạy tại `http://localhost:8080`.
