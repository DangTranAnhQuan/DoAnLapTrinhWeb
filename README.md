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

Dự án được phân chia thành hai khu vực chính: giao diện cho khách hàng (User) và trang quản trị (Admin).

### 1. Chức năng chung
- Đăng ký, đăng nhập, đăng xuất, quên mật khẩu, xác thực OTP, mã hóa mật khẩu.
- Tìm kiếm và lọc sản phẩm.

### 2. Giao diện Người dùng (User)
- Xem, thêm giỏ hàng, thanh toán (COD/VietinBank), áp mã giảm giá, quản lý tài khoản, đơn hàng, đánh giá, chat.

### 3. Trang Quản trị (Admin)
- Dashboard, quản lý người dùng, sản phẩm, danh mục, đơn hàng, khuyến mãi, vận chuyển, kho hàng, hỗ trợ chat.

---

## 🛠️ Công nghệ sử dụng

- **Backend:** Java 17, Spring Boot 3.x  
- **Frontend:** Thymeleaf, Bootstrap 5, jQuery  
- **Database:** SQL Server  
- **Security:** Spring Security  
- **Email:** Spring Mail (OTP)  
- **Build Tool:** Maven

---

## 🚀 Hướng dẫn cài đặt (Getting Started)

### 1. Yêu cầu (Prerequisites)
JDK 17+, Maven, SQL Server 2019+, Git, IDE Java.

### 2. Cài đặt (Installation)
```bash
git clone https://github.com/DangTranAnhQuan/DoAnLapTrinhWeb.git
cd DoAnLapTrinhWeb
git checkout Duy
```

### 3. Cấu hình Database
Chạy `oneshop.sql` và `dulieumau.sql` trong SSMS.

### 4. Cấu hình ứng dụng
Cập nhật file `application.properties` với thông tin SQL Server và Mail.

### 5. Chạy ứng dụng
```bash
mvn spring-boot:run
```

Mở:
- [User](http://localhost:8080)
- [Admin](http://localhost:8080/admin)

---

## 📂 Cấu trúc thư mục
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

---

## 🎥 Video Demo chạy dự án
Link video: *(chưa cập nhật)*

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
