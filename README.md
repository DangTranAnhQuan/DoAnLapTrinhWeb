# OneShop - Website Bán Mỹ Phẩm Trực Tuyến

Dự án **OneShop** là một ứng dụng web thương mại điện tử hoàn chỉnh, được xây dựng với mục tiêu cung cấp một nền tảng bán mỹ phẩm trực tuyến. 
Dự án này tích hợp đầy đủ các chức năng từ quản lý sản phẩm, tương tác người dùng, đến xử lý giao dịch, tạo nên một giải pháp thương mại điện tử mạnh mẽ và thân thiện.

## 🎯 Tổng quan (Overview)

Dự án được xây dựng trên nền tảng **Java Spring Boot**, sử dụng **Spring Data JPA** để tương tác với cơ sở dữ liệu **SQL Server**, và **Thymeleaf** kết hợp **Bootstrap 5** để xây dựng giao diện người dùng. Hệ thống cũng tích hợp **Spring Security** để xử lý xác thực, phân quyền và mã hóa mật khẩu, cùng với **Spring Mail** để gửi mã OTP.

## ✨ Tính năng nổi bật

Dự án được phân chia thành hai khu vực chính: giao diện cho khách hàng (User) và trang quản trị (Admin).

### 1\. Chức năng chung

  * **Xác thực:** Đăng ký (với mã OTP xác thực qua Email), Đăng nhập, Đăng xuất.
  * **Bảo mật:** Quên mật khẩu (lấy lại bằng OTP qua Email), mã hóa mật khẩu người dùng (sử dụng Spring Security).
  * **Tương tác:** Tìm kiếm sản phẩm, lọc sản phẩm nâng cao.

### 2\. Giao diện Người dùng (User)

  * **Trang chủ & Shop:** Hiển thị sản phẩm (sản phẩm mới, bán chạy, đánh giá cao) với cơ chế phân trang.
  * **Sản phẩm:** Xem chi tiết sản phẩm, xem các sản phẩm liên quan.
  * **Giỏ hàng:** Quản lý giỏ hàng (thêm, sửa, xóa) được lưu trữ vào cơ sở dữ liệu.
  * **Yêu thích:** Thêm/xóa sản phẩm khỏi danh sách yêu thích.
  * **Thanh toán:**
      * Quy trình Thanh toán chi tiết.
      * Hỗ trợ thanh toán khi nhận hàng (COD) và thanh toán Online (VietinBank).
      * Áp dụng mã giảm giá khi thanh toán.
  * **Quản lý tài khoản:**
      * Cập nhật thông tin cá nhân, thay đổi mật khẩu.
      * Quản lý sổ địa chỉ (thêm/sửa/xóa nhiều địa chỉ nhận hàng).
  * **Quản lý đơn hàng:**
      * Xem lịch sử đơn hàng và theo dõi trạng thái (Đang xử lý, Đã xác nhận, Đang giao, Đã giao, Đã hủy, Trả hàng-Hoàn tiền).
  * **Đánh giá:** Người dùng có thể đánh giá (bình luận text, hình ảnh/video) cho các sản phẩm đã mua.
  * **Chat:** Tích hợp widget chat trực tiếp với Admin.

### 3\. Trang Quản trị (Admin)

  * **Dashboard:** Bảng điều khiển tổng quan, thống kê doanh thu, đơn hàng, người dùng mới.
  * **Quản lý Người dùng:** Tìm kiếm, xem chi tiết, thêm/sửa/xóa và phân quyền người dùng.
  * **Quản lý Sản phẩm:** CRUD (Thêm/Sửa/Xóa) sản phẩm.
  * **Quản lý Thuộc tính:** Quản lý Danh mục và Thương hiệu.
  * **Quản lý Đơn hàng:** Xem danh sách, tìm kiếm/lọc đơn hàng, cập nhật trạng thái đơn hàng (duyệt đơn, xác nhận giao hàng...).
  * **Quản lý Khuyến mãi:** Quản lý các chương trình giảm giá, mã voucher.
  * **Quản lý Vận chuyển:**
      * Quản lý các nhà vận chuyển.
      * Thiết lập các gói cước và phí vận chuyển linh hoạt theo từng tỉnh thành.
  * **Quản lý Kho hàng:**
      * Quản lý Nhà cung cấp.
      * Tạo phiếu nhập hàng.
      * Theo dõi số lượng tồn kho.
  * **Hỗ trợ:** Chat trực tiếp, quản lý các cuộc hội thoại với khách hàng.

## 🛠️ Công nghệ sử dụng

  * **Backend:** Java 17, Spring Boot 3.x
  * **Frontend:** Thymeleaf, Bootstrap 5, JavaScript (jQuery)
  * **Database:** Microsoft SQL Server
  * **Data Access:** Spring Data JPA / Hibernate
  * **Security:** Spring Security (Xác thực, Phân quyền, Mã hóa mật khẩu)
  * **Email:** Spring Mail (dùng cho OTP)
  * **Build Tool:** Apache Maven

## 🚀 Hướng dẫn cài đặt (Getting Started)

Thực hiện các bước sau để chạy dự án trên máy cục bộ của bạn.

### 1\. Yêu cầu (Prerequisites)

Đảm bảo bạn đã cài đặt các công cụ sau:

  * **JDK 17** hoặc cao hơn.
  * **Apache Maven** 3.8+
  * **Microsoft SQL Server 2019** (hoặc cao hơn) và **SQL Server Management Studio (SSMS)**.
  * **Git**
  * Một IDE Java (ví dụ: IntelliJ IDEA, Eclipse, VS Code).

### 2\. Cài đặt (Installation)

1.  **Clone Repository:**

    ```bash
    git clone https://github.com/DangTranAnhQuan/DoAnLapTrinhWeb.git
    ```

2.  **Checkout nhánh `Duy`:**

    ```bash
    cd DoAnLapTrinhWeb
    git checkout Duy
    ```

3.  **Mở dự án:** Mở dự án bằng IDE của bạn và đợi Maven tự động tải về các dependencies (thư viện).

### 3\. Cấu hình Cơ sở dữ liệu (Database Setup)

Đây là bước quan trọng nhất.

1.  Mở **SSMS** và kết nối tới SQL Server của bạn.
2.  Chạy file `oneshop.sql` để tạo database `OneShop` và toàn bộ cấu trúc bảng.
3.  Sau đó, hãy chạy file `dulieumau.sql` để thêm dữ liệu mẫu (sản phẩm, tài khoản, đơn hàng...) giúp website có thể hoạt động ngay.

### 4\. Cấu hình ứng dụng (Application Configuration)

1.  Điều hướng đến file cấu hình: `src/main/resources/application.properties`.

2.  Cập nhật các thông tin kết nối cho phù hợp với môi trường của bạn:

    ```properties
    # =============================================
    # DATABASE (SQL SERVER)
    # =============================================
    # Thay đổi 'TEN_SERVER' và 'PORT' (thường là 1433)
    spring.datasource.url=jdbc:sqlserver://TEN_SERVER:PORT;databaseName=OneShop;encrypt=true;trustServerCertificate=true;
    # Thay đổi username và password của SQL Server
    spring.datasource.username=sa
    spring.datasource.password=12345

    # =============================================
    # SPRING MAIL (DÙNG CHO OTP)
    # =============================================
    # Sử dụng tài khoản Gmail của bạn
    spring.mail.host=smtp.gmail.com
    spring.mail.port=587
    spring.mail.username=your-email@gmail.com
    # QUAN TRỌNG: Đây là Mật khẩu ứng dụng (App Password) của Google, không phải mật khẩu email
    spring.mail.password=your-google-app-password 
    ```

### 5\. Chạy ứng dụng (Usage)

Sau khi hoàn tất cài đặt và cấu hình:

1.  Tìm và chạy file `OneShopApplication.java` từ IDE của bạn.
2.  *Hoặc* chạy bằng Maven:
    ```bash
    mvn spring-boot:run
    ```
3.  Truy cập ứng dụng:
      * **Trang User:** `http://localhost:8080`
      * **Trang Admin:** `http://localhost:8080/admin`
      * **Tài khoản Admin (mẫu):** `admin@oneshop.com` (Mật khẩu được mã hóa trong file `dulieumau.sql` là `admin123`)

## 📂 Cấu trúc thư mục (Project Structure)

Dưới đây là cấu trúc các thư mục quan trọng của dự án:

```
OneShop
 ┣ src/main/java/nhom17/OneShop
 ┃ ┣ config           # Cấu hình Spring Security, MVC
 ┃ ┣ controller       # Xử lý request (Admin, User, Auth...)
 ┃ ┣ dto              # Data Transfer Objects (đối tượng truyền dữ liệu)
 ┃ ┣ entity           # Các Entity (ánh xạ bảng trong CSDL)
 ┃ ┣ exception        # Xử lý ngoại lệ tùy chỉnh
 ┃ ┣ repository       # Interfaces của Spring Data JPA
 ┃ ┣ request          # Đối tượng dùng để nhận dữ liệu từ form
 ┃ ┣ service          # Chứa logic nghiệp vụ (business logic)
 ┃ ┗ OneShopApplication.java  # File chạy chính
 ┣ src/main/resources
 ┃ ┣ static           # Tài nguyên tĩnh (CSS, JS, Images)
 ┃ ┃ ┣ admin          # (Tài nguyên cho trang Admin)
 ┃ ┃ ┗ web            # (Tài nguyên cho trang User)
 ┃ ┣ templates        # Giao diện Thymeleaf
 ┃ ┃ ┣ admin          # (Giao diện trang Admin)
 ┃ ┃ ┣ user           # (Giao diện trang User)
 ┃ ┃ ┣ fragments      # (Các thành phần tái sử dụng: header, footer)
 ┃ ┃ ┗ layouts        # (Layout chính)
 ┃ ┗ application.properties   # File cấu hình chính
 ┣ uploads            # Thư mục lưu trữ file được tải lên (ảnh sản phẩm, avatar...)
 ┗ pom.xml            # File quản lý thư viện của Maven
```
