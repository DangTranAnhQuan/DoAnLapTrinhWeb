<div align="center">

# OneShop - Website Bán Mỹ Phẩm Trực Tuyến

Dự án **OneShop** là một ứng dụng web thương mại điện tử hoàn chỉnh, được xây dựng với mục tiêu cung cấp một nền tảng bán mỹ phẩm trực tuyến.  
Dự án này tích hợp đầy đủ các chức năng từ quản lý sản phẩm, tương tác người dùng, đến xử lý giao dịch, tạo nên một giải pháp thương mại điện tử mạnh mẽ và thân thiện.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Maven-3.9+-blue.svg)](https://maven.apache.org/)
[![SQL Server](https://img.shields.io/badge/SQL%20Server-2019+-red.svg)](https://www.microsoft.com/sql-server)
[![Tomcat](https://img.shields.io/badge/Tomcat-10+-yellow.svg)](https://tomcat.apache.org/)

</div>

---

## 📑 Mục lục

1. [🎯 Tổng quan (Overview)](#-tổng-quan-overview)
2. [✨ Tính năng nổi bật](#-tính-năng-nổi-bật)
   - [1. Chức năng chung](#1-chức-năng-chung)
   - [2. Giao diện Người dùng (User)](#2-giao-diện-người-dùng-user)
   - [3. Trang Quản trị (Admin)](#3-trang-quản-trị-admin)
3. [🛠️ Công nghệ sử dụng](#️-công-nghệ-sử-dụng)
4. [🚀 Hướng dẫn cài đặt (Getting Started)](#-hướng-dẫn-cài-đặt-getting-started)
   - [1. Yêu cầu (Prerequisites)](#1-yêu-cầu-prerequisites)
   - [2. Cài đặt (Installation)](#2-cài-đặt-installation)
   - [3. Cấu hình Cơ sở dữ liệu (Database Setup)](#3-cấu-hình-cơ-sở-dữ-liệu-database-setup)
   - [4. Cấu hình ứng dụng (Application Configuration)](#4-cấu-hình-ứng-dụng-application-configuration)
   - [5. Chạy ứng dụng (Usage)](#5-chạy-ứng-dụng-usage)
5. [📂 Cấu trúc thư mục (Project Structure)](#-cấu-trúc-thư-mục-project-structure)
6. [🎥 Video Demo chạy dự án](#-video-demo-chạy-dự-án)
7. [🤝 Đóng góp](#-đóng-góp)

---

## 🎯 Tổng quan (Overview)

Dự án được xây dựng trên nền tảng **Java Spring Boot**, sử dụng **Spring Data JPA** để tương tác với cơ sở dữ liệu **SQL Server**, và **Thymeleaf** kết hợp **Bootstrap 5** để xây dựng giao diện người dùng.  
Hệ thống cũng tích hợp **Spring Security** để xử lý xác thực, phân quyền và mã hóa mật khẩu, cùng với **Spring Mail** để gửi mã OTP.

---

## ✨ Tính năng nổi bật

Dự án được phân chia thành hai khu vực chính: giao diện cho người dùng (User) và trang quản trị (Admin).

| **Nhóm người dùng / Thành phần** | **Chức năng chính** |
| -------------------------------- | -------------------- |
| 🧑‍💼 **Khách hàng (Chưa đăng ký)** | - Đăng ký tài khoản.<br>- Tra cứu / tìm kiếm sản phẩm.<br>- Thêm sản phẩm vào giỏ hàng.<br>- Liên hệ cửa hàng để được tư vấn. |
| 👤 **Thành viên (Đã đăng ký)** | - Đăng nhập, đăng xuất, quên mật khẩu, xác thực OTP.<br>- Tra cứu và mua sản phẩm trực tuyến.<br>- Quản lý giỏ hàng (thêm / sửa / xóa).<br>- Thanh toán (COD, VNPay hoặc ngân hàng liên kết).<br>- Áp dụng mã giảm giá khi thanh toán.<br>- Cập nhật thông tin tài khoản và địa chỉ nhận hàng.<br>- Theo dõi và quản lý đơn hàng (đang xử lý, đã xác nhận, đang giao, đã giao, hoàn tiền, hủy đơn).<br>- Đánh giá sản phẩm (bình luận, hình ảnh, video).<br>- Chat và liên hệ trực tiếp với cửa hàng. |
| 🛠️ **Quản trị viên (Admin)** | - Quản lý người dùng và phân quyền.<br>- Quản lý khuyến mãi và mã giảm giá.<br>- Quản lý sản phẩm, danh mục và thương hiệu.<br>- Quản lý đơn hàng, cập nhật trạng thái giao hàng.<br>- Quản lý đơn vận chuyển và nhà vận chuyển.<br>- Quản lý kho hàng, phiếu nhập và nhà cung cấp.<br>- Quản lý gói cước vận chuyển theo khu vực.<br>- Hỗ trợ khách hàng qua chat trực tuyến.<br>- Thống kê doanh thu, đơn hàng, người dùng mới và báo cáo tổng hợp.<br>- Dashboard hiển thị dữ liệu tổng quan. |

---

## 🛠️ Công nghệ sử dụng

| **Thành phần**        | **Phiên bản / Công nghệ**     | **Ghi chú** |
| ---------------------- | ----------------------------- | ------------ |
| **Backend**            | Java 17, Spring Boot 3.x      | Xử lý logic nghiệp vụ, API, bảo mật và kết nối CSDL |
| **Frontend**           | Thymeleaf, Bootstrap 5, jQuery | Xây dựng giao diện web động, thân thiện người dùng |
| **Database**           | Microsoft SQL Server 2019+    | Lưu trữ toàn bộ dữ liệu sản phẩm, người dùng, đơn hàng |
| **Security**           | Spring Security               | Quản lý xác thực, phân quyền, mã hóa mật khẩu |
| **Email Service**      | Spring Mail (Gmail SMTP, OTP) | Gửi mã xác thực, thông báo người dùng qua email |
| **Build Tool**         | Apache Maven 3.9+             | Quản lý dependencies và build dự án tự động |

---

## 🚀 Hướng dẫn cài đặt (Getting Started)

### 1. Yêu cầu hệ thống
Trước khi cài đặt, cần chuẩn bị các công cụ sau:

| Thành phần                  | Phiên bản khuyến nghị | Ghi chú                                  |
| --------------------------- | --------------------- | ---------------------------------------- |
| **JDK**                     | 17+                   | Thiết lập biến môi trường                |
| **SQL Server**              | 2019 hoặc mới hơn     | Dùng để lưu trữ dữ liệu ứng dụng         |
| **Maven**                   | 3.9+                  | Quản lý dependencies                     |
| **IntelliJ IDEA / Eclipse** | Mới nhất              | IDE để chạy và debug (tùy chọn)          |

### 2. Cài đặt
```bash
git clone https://github.com/DangTranAnhQuan/DoAnLapTrinhWeb.git
cd DoAnLapTrinhWeb
```

### 3. Cấu hình Database
Chạy `oneshop.sql` và `dulieumau.sql` trong SSMS.

### 4. Cấu hình ứng dụng
Cập nhật file `application.properties` với thông tin SQL Server và Mail.
```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=OneShop;encrypt=false;trustServerCertificate=true;sendStringParametersAsUnicode=true
spring.datasource.username=your-username
spring.datasource.password=your-password
spring.datasource.driver-class-name=com.microsoft.sqlserver.jdbc.SQLServerDriver
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.SQLServerDialect

spring.jpa.hibernate.ddl-auto=none
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.servlet.encoding.charset=UTF-8
server.servlet.encoding.enabled=true
server.servlet.encoding.force=true

spring.thymeleaf.cache=false

spring.jpa.hibernate.naming.physical-strategy=org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
spring.jpa.hibernate.naming.implicit-strategy=org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl

server.port=8080

spring.servlet.multipart.max-file-size=20MB
spring.servlet.multipart.max-request-size=21MB

spring.mail.host=smtp.gmail.com
spring.mail.port=465
spring.mail.username=your-email@gmail.com (admin)
spring.mail.password=your-google-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=true
spring.mail.properties.mail.smtp.ssl.trust=smtp.gmail.com
spring.mail.properties.mail.transport.protocol=smtp

shop.sepay.bank-code=VietinBank
shop.sepay.account-no=your-account-number
shop.sepay.account-name=your-account-name

app.mail.from-name=name-app-password
app.mail.from-address=your-email@gmail.com (admin)

server.forward-headers-strategy=framework
```

### 5. Chạy ứng dụng
<details>
<summary><strong>Sử dụng IDE (IntelliJ IDEA)</strong></summary>

**Bước 1:** Mở dự án trong IntelliJ IDEA

**Bước 2:** Cấu hình Run Application nhanh và tiện hơn

- Vào `Run` → `Edit Configurations` → `Add New Configuration` → `Application` → `Main class`
- Chọn OneShopApplication, có thể chỉnh `Name` cho phù hợp
- Sau đó, nhấn `OK`

**Bước 3:** Click `Run` để khởi chạy

</details>

Mở trình duyệt và truy cập các URL sau:

| Trang             | URL                                           |
| ----------------- | --------------------------------------------- |
| **Trang chủ**     | http://localhost:8080/                        |
| **Admin Panel**   | http://localhost:8080/admin/dashboard         |

---

## 📂 Cấu trúc thư mục
```
OneShop
 ┣ src/main/java/nhom17/OneShop
 ┃ ┣ config           # Cấu hình Spring Security, MVC
 ┃ ┣ controller       # Xử lý request (Admin, User)
 ┃ ┃ ┣ admin          
 ┃ ┃ ┣ user
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

---

## 🎥 Video Demo chạy dự án
Link video: (https://youtu.be/eg9ckERGOBQ)

---

## 🤝 Đóng góp

| Thành viên | GitHub |
|-------------|---------|
| **Đinh Nguyễn Đức Duy** | [@Shiro74-coder](https://github.com/Shiro74-coder) |
| **Đặng Trần Anh Quân** | [@DangTranAnhQuan](https://github.com/DangTranAnhQuan) |
| **Trần An Thiên** | [@TranAnThien](https://github.com/TranAnThien) |

---

<div align="center">

**OneShop** - Website bán mỹ phẩm  
Đồ án Lập trình WEB  

</div>
