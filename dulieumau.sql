-- =============================================
-- SỬ DỤNG DATABASE VÀ XÓA DỮ LIỆU CŨ
-- =============================================
USE OneShop;

DELETE FROM DonHang_ChiTiet;
DELETE FROM LichSuTrangThaiDon;
DELETE FROM VanChuyen;
DELETE FROM DonHang;
DELETE FROM GioHang;
DELETE FROM SanPhamYeuThich;
DELETE FROM ChiTietPhieuNhap;
DELETE FROM PhieuNhap;
DELETE FROM KhoHang;
DELETE FROM DanhGia;
DELETE FROM SanPham;
DELETE FROM MaXacThuc;
DELETE FROM DiaChi;
DELETE FROM NguoiDung;
DELETE FROM KhuyenMai;
DELETE FROM NhaVanChuyen;
DELETE FROM NhaCungCap;
DELETE FROM ThuongHieu;
DELETE FROM DanhMuc;
DELETE FROM HangThanhVien;
DELETE FROM VaiTro;

-- =============================================
-- RESET LẠI SỐ THỨ TỰ TỰ TĂNG (IDENTITY)
-- =============================================
DBCC CHECKIDENT ('LichSuTrangThaiDon', RESEED, 0);
DBCC CHECKIDENT ('VanChuyen', RESEED, 0);
DBCC CHECKIDENT ('DonHang', RESEED, 0);
DBCC CHECKIDENT ('PhieuNhap', RESEED, 0);
DBCC CHECKIDENT ('SanPham', RESEED, 0);
DBCC CHECKIDENT ('MaXacThuc', RESEED, 0);
DBCC CHECKIDENT ('DiaChi', RESEED, 0);
DBCC CHECKIDENT ('NguoiDung', RESEED, 0);
DBCC CHECKIDENT ('NhaVanChuyen', RESEED, 0);
DBCC CHECKIDENT ('NhaCungCap', RESEED, 0);
DBCC CHECKIDENT ('ThuongHieu', RESEED, 0);
DBCC CHECKIDENT ('DanhMuc', RESEED, 0);
DBCC CHECKIDENT ('HangThanhVien', RESEED, 0);

-- =============================================
-- THÊM DỮ LIỆU MẪU
-- =============================================

-- Bảng không có khóa ngoại
INSERT INTO VaiTro (MaVaiTro, TenVaiTro) VALUES
(1, 'Admin'),
(2, 'User');

INSERT INTO HangThanhVien (TenHang, DiemToiThieu, PhanTramGiamGia) VALUES
(N'Đồng', 0, 0),
(N'Bạc', 500, 3),
(N'Vàng', 1500, 5),
(N'Kim Cương', 5000, 10);

INSERT INTO DanhMuc (TenDanhMuc, HinhAnh) VALUES
(N'Chăm sóc da mặt', 'categories/cat-skincare.png'),
(N'Trang điểm', 'categories/cat-makeup.png'),
(N'Chăm sóc tóc', 'categories/cat-haircare.png'),
(N'Chăm sóc cơ thể', 'categories/cat-bodycare.png'),
(N'Nước hoa', 'categories/cat-fragrance.png');

INSERT INTO ThuongHieu (TenThuongHieu, MoTa, HinhAnh) VALUES
(N'Innisfree', N'Thương hiệu mỹ phẩm thiên nhiên từ đảo Jeju, Hàn Quốc.', 'brands/innisfree.png'),
(N'La Roche-Posay', N'Thương hiệu dược mỹ phẩm hàng đầu của Pháp.', 'brands/larocheposay.png'),
(N'L''Oréal', N'Tập đoàn mỹ phẩm hàng đầu thế giới.', 'brands/loreal.png'),
(N'Shiseido', N'Thương hiệu mỹ phẩm cao cấp từ Nhật Bản.', 'brands/shiseido.png'),
(N'Maybelline', N'Thương hiệu trang điểm số 1 thế giới từ New York.', 'brands/maybelline.png'),
(N'The Ordinary', N'Thương hiệu chăm sóc da với các thành phần khoa học.', 'brands/theordinary.png');

INSERT INTO NhaCungCap(TenNCC, SDT, DiaChi) VALUES
(N'Công ty TNHH Đẹp Việt', '02838118118', N'123 Nguyễn Trãi, Q1, TP.HCM'),
(N'Nhà phân phối An Khánh', '02439743974', N'456 Hai Bà Trưng, Hoàn Kiếm, Hà Nội');

INSERT INTO NhaVanChuyen(TenNVC, SoDienThoai, Website) VALUES
(N'Giao Hàng Nhanh', '1900636677', 'https://ghn.vn'),
(N'Giao Hàng Tiết Kiệm', '19006092', 'https://ghtk.vn'),
(N'Viettel Post', '19008095', 'https://viettelpost.com.vn');

INSERT INTO KhuyenMai(MaKhuyenMai, TenChienDich, KieuApDung, GiaTri, BatDauLuc, KetThucLuc, TrangThai) VALUES
('GIAM10K', N'Giảm 10k cho đơn hàng từ 100k', 0, 10000, '2025-01-01', '2025-12-31', 1), -- Thêm TrangThai = 1
('FREESHIP', N'Miễn phí vận chuyển', 0, 30000, '2025-01-01', '2025-12-31', 1); -- Thêm TrangThai = 1


-- Bảng có khóa ngoại (Cấp 1)
INSERT INTO NguoiDung (Email, TenDangNhap, MatKhau, HoTen, SoDienThoai, MaVaiTro, MaHangThanhVien) VALUES
('admin@oneshop.com', 'admin', '$2a$10$lSDfM3BVszF21J4ej8c8Qu0g8.e3JtNDxbiqUlvT.gfJiV2ikc3iW', N'Quản Trị Viên', '0900000001', 1, 4),
('user@example.com', 'user', '$2a$10$pMk7ufY9lnfjJZy5nTIMzushp586n2ZLJlYL7BDGoTgVmUAGQmbc6', N'Người Dùng Mẫu', '0900000002', 2, 2),
('nguyenvana@gmail.com', 'nguyenvana', '$2a$10$iOGnqWf.rE8N0gYY/TnYoOrl8gnyiHBrQirJSoCJOewfSgqasKppa', N'Nguyễn Văn An', '0912345678', 2, 3),
('tranbichb@gmail.com', 'tranbichb', '$2a$10$iOGnqWf.rE8N0gYY/TnYoOrl8gnyiHBrQirJSoCJOewfSgqasKppa', N'Trần Thị Bích', '0987654321', 2, 1);

INSERT INTO SanPham (TenSanPham, MaDanhMuc, MaThuongHieu, MoTa, GiaBan, GiaNiemYet, HinhAnh) VALUES
(N'Serum The Ordinary Niacinamide 10% + Zinc 1%', 1, 6, N'Serum kiềm dầu và giảm mụn hiệu quả.', 180000, 250000, 'products/product-01.png'),
(N'Kem Chống Nắng La Roche-Posay Anthelios', 1, 2, N'Kem chống nắng kiểm soát dầu, không gây nhờn rít.', 350000, 450000, 'products/product-02.png'),
(N'Son Lì Maybelline Superstay Matte Ink', 2, 5, N'Son kem lì lâu trôi, màu sắc đa dạng.', 150000, 200000, 'products/product-03.png'),
(N'Mặt Nạ Trà Xanh Innisfree', 1, 1, N'Mặt nạ đất sét giúp làm sạch sâu và se khít lỗ chân lông.', 280000, 350000, 'products/product-04.png'),
(N'Dầu Gội L''Oréal Elseve Total Repair 5', 3, 3, N'Phục hồi 5 dấu hiệu tóc hư tổn.', 120000, 150000, 'products/product-05.png'),
(N'Phấn Nước Shiseido Synchro Skin', 2, 4, N'Lớp nền mỏng nhẹ, tự nhiên và bền màu.', 900000, 1200000, 'products/product-06.png'),
(N'Serum Cấp Nước The Ordinary Hyaluronic Acid 2% + B5', 1, 6, N'Cung cấp độ ẩm sâu cho làn da căng mọng.', 200000, 280000, 'products/product-07.png'),
(N'Sữa Rửa Mặt La Roche-Posay Effaclar', 1, 2, N'Sữa rửa mặt dạng gel cho da dầu, nhạy cảm.', 290000, 380000, 'products/product-08.png'),
(N'Mascara Maybelline Lash Sensational', 2, 5, N'Làm dày và cong mi, hiệu ứng quạt xòe.', 160000, 210000, 'products/product-09.png'),
(N'Tinh Chất Trà Xanh Innisfree Green Tea Seed Serum', 1, 1, N'Dưỡng ẩm và chống oxy hóa cho da.', 450000, 550000, 'products/product-10.png');

INSERT INTO PhieuNhap(MaNCC, NgayTao) VALUES 
(1, GETDATE()), 
(2, GETDATE());

-- Bảng có khóa ngoại (Cấp 2)
INSERT INTO DiaChi(MaNguoiDung, TenNguoiNhan, SoDienThoai, TinhThanh, QuanHuyen, PhuongXa, SoNhaDuong) VALUES
(2, N'Người Dùng Mẫu', '0900000002', N'TP. Hồ Chí Minh', N'Quận 1', N'Phường Bến Nghé', N'123 Lê Lợi'),
(3, N'Nguyễn Văn An', '0912345678', N'Hà Nội', N'Quận Ba Đình', N'Phường Điện Biên', N'456 Hoàng Diệu');

INSERT INTO SanPhamYeuThich (MaNguoiDung, MaSanPham) VALUES
(2, 1), (2, 3), (2, 5);

INSERT INTO ChiTietPhieuNhap(MaPhieuNhap, MaSanPham, SoLuong, GiaNhap) VALUES
(1, 1, 100, 120000), (1, 2, 50, 250000),
(2, 3, 200, 90000), (2, 4, 80, 200000);

INSERT INTO KhoHang(MaSanPham, SoLuongTon)
SELECT MaSanPham, CAST(RAND(CONVERT(varbinary, newid()))*100 AS INT) + 20 FROM SanPham;

INSERT INTO GioHang (MaNguoiDung, MaSanPham, SoLuong, DonGia)
SELECT 2, 2, 1, GiaBan FROM SanPham WHERE MaSanPham = 2;
INSERT INTO GioHang (MaNguoiDung, MaSanPham, SoLuong, DonGia)
SELECT 2, 4, 2, GiaBan FROM SanPham WHERE MaSanPham = 4;

INSERT INTO DonHang(MaNguoiDung, TrangThai, PhuongThucThanhToan, TrangThaiThanhToan, TienHang, TongTien, TenNguoiNhan, SoDienThoaiNhan, DiaChiNhan) VALUES
(3, N'Đã giao', N'COD', N'Đã thanh toán', 530000, 560000, N'Nguyễn Văn An', '0912345678', N'456 Hoàng Diệu, Phường Điện Biên, Quận Ba Đình, Hà Nội'),
(4, N'Đang xử lý', N'ONLINE', N'Đã thanh toán', 280000, 280000, N'Trần Thị Bích', '0987654321', N'789 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP.HCM');

INSERT INTO DanhGia(MaSanPham, MaNguoiDung, DiemDanhGia, BinhLuan) VALUES
(1, 3, 5, N'Sản phẩm tuyệt vời, kiềm dầu tốt!'),
(2, 3, 4, N'Chống nắng ổn, hơi nâng tone một chút.');

-- Bảng có khóa ngoại (Cấp 3)
INSERT INTO DonHang_ChiTiet(MaDonHang, MaSanPham, TenSanPham, DonGia, SoLuong) VALUES
(1, 1, N'Serum The Ordinary Niacinamide 10% + Zinc 1%', 180000, 1),
(1, 2, N'Kem Chống Nắng La Roche-Posay Anthelios', 350000, 1),
(2, 4, N'Mặt Nạ Trà Xanh Innisfree', 280000, 1);