-- =============================================
-- SỬ DỤNG DATABASE VÀ XÓA DỮ LIỆU CŨ
-- =============================================
USE OneShop;
GO

-- Xóa dữ liệu theo thứ tự ngược lại (từ bảng con đến bảng cha)
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

DELETE FROM DiaChi;
DELETE FROM LienHe;
DELETE FROM NhatKyHeThong;
DELETE FROM NguoiDung;
DELETE FROM KhuyenMai;
DELETE FROM NhaVanChuyen;
DELETE FROM NhaCungCap;
DELETE FROM ThuongHieu;
DELETE FROM DanhMuc;
DELETE FROM HangThanhVien;
DELETE FROM VaiTro;
GO

-- =============================================
-- RESET LẠI SỐ THỨ TỰ TỰ TĂNG (IDENTITY)
-- =============================================
DBCC CHECKIDENT ('LichSuTrangThaiDon', RESEED, 0);
DBCC CHECKIDENT ('VanChuyen', RESEED, 0);
DBCC CHECKIDENT ('DonHang', RESEED, 0);
DBCC CHECKIDENT ('PhieuNhap', RESEED, 0);
DBCC CHECKIDENT ('SanPham', RESEED, 0);
DBCC CHECKIDENT ('DiaChi', RESEED, 0);
DBCC CHECKIDENT ('LienHe', RESEED, 0);
DBCC CHECKIDENT ('NhatKyHeThong', RESEED, 0);
DBCC CHECKIDENT ('NguoiDung', RESEED, 0);
DBCC CHECKIDENT ('NhaVanChuyen', RESEED, 0);
DBCC CHECKIDENT ('NhaCungCap', RESEED, 0);
DBCC CHECKIDENT ('ThuongHieu', RESEED, 0);
DBCC CHECKIDENT ('DanhMuc', RESEED, 0);
DBCC CHECKIDENT ('HangThanhVien', RESEED, 0);
GO

-- =============================================
-- THÊM DỮ LIỆU MẪU
-- =============================================

-- 1. VaiTro (không có FK)
INSERT INTO VaiTro (MaVaiTro, TenVaiTro) VALUES
(1, N'Admin'),
(2, N'User');
GO

-- 2. HangThanhVien (không có FK)
INSERT INTO HangThanhVien (TenHang, DiemToiThieu, PhanTramGiamGia) VALUES
(N'Đồng', 0, 0),
(N'Bạc', 500, 3),
(N'Vàng', 1500, 5),
(N'Kim Cương', 5000, 10);
GO

-- 3. DanhMuc (không có FK)
INSERT INTO DanhMuc (TenDanhMuc, HinhAnh) VALUES
(N'Chăm sóc da mặt', 'categories/cat-skincare.png'),
(N'Trang điểm', 'categories/cat-makeup.png'),
(N'Chăm sóc tóc', 'categories/cat-haircare.png'),
(N'Chăm sóc cơ thể', 'categories/cat-bodycare.png'),
(N'Nước hoa', 'categories/cat-fragrance.png');
GO

-- 4. ThuongHieu (không có FK)
INSERT INTO ThuongHieu (TenThuongHieu, MoTa, HinhAnh) VALUES
(N'Innisfree', N'Thương hiệu mỹ phẩm thiên nhiên từ đảo Jeju, Hàn Quốc.', 'brands/innisfree.png'),
(N'La Roche-Posay', N'Thương hiệu dược mỹ phẩm hàng đầu của Pháp.', 'brands/larocheposay.png'),
(N'L''Oréal', N'Tập đoàn mỹ phẩm hàng đầu thế giới.', 'brands/loreal.png'),
(N'Shiseido', N'Thương hiệu mỹ phẩm cao cấp từ Nhật Bản.', 'brands/shiseido.png'),
(N'Maybelline', N'Thương hiệu trang điểm số 1 thế giới từ New York.', 'brands/maybelline.png'),
(N'The Ordinary', N'Thương hiệu chăm sóc da với các thành phần khoa học.', 'brands/theordinary.png');
GO

-- 5. NhaCungCap (không có FK)
INSERT INTO NhaCungCap(TenNCC, SDT, DiaChi) VALUES
(N'Công ty TNHH Đẹp Việt', '02838118118', N'123 Nguyễn Trãi, Q1, TP.HCM'),
(N'Nhà phân phối An Khánh', '02439743974', N'456 Hai Bà Trưng, Hoàn Kiếm, Hà Nội');
GO

-- 6. NhaVanChuyen (không có FK)
INSERT INTO NhaVanChuyen(TenNVC, SoDienThoai, Website) VALUES
(N'Giao Hàng Nhanh', '1900636677', 'https://ghn.vn'),
(N'Giao Hàng Tiết Kiệm', '19006092', 'https://ghtk.vn'),
(N'Viettel Post', '19008095', 'https://viettelpost.com.vn');
GO

-- 7. KhuyenMai (không có FK)
INSERT INTO KhuyenMai(MaKhuyenMai, TenChienDich, KieuApDung, GiaTri, BatDauLuc, KetThucLuc, TrangThai) VALUES
('GIAM10K', N'Giảm 10k cho đơn hàng từ 100k', 0, 10000, '2025-01-01', '2025-12-31', 1),
('FREESHIP', N'Miễn phí vận chuyển', 0, 30000, '2025-01-01', '2025-12-31', 1);
GO

-- 8. NguoiDung (có FK: VaiTro, HangThanhVien)
INSERT INTO NguoiDung (Email, TenDangNhap, MatKhau, HoTen, SoDienThoai, MaVaiTro, MaHangThanhVien) VALUES
('admin@oneshop.com', 'admin', '$2a$10$lSDfM3BVszF21J4ej8c8Qu0g8.e3JtNDxbiqUlvT.gfJiV2ikc3iW', N'Quản Trị Viên', '0900000001', 1, 4),
('user@example.com', 'user', '$2a$10$pMk7ufY9lnfjJZy5nTIMzushp586n2ZLJlYL7BDGoTgVmUAGQmbc6', N'Người Dùng Mẫu', '0900000002', 2, 2),
('nguyenvana@gmail.com', 'nguyenvana', '$2a$10$iOGnqWf.rE8N0gYY/TnYoOrl8gnyiHBrQirJSoCJOewfSgqasKppa', N'Nguyễn Văn An', '0912345678', 2, 3),
('tranbichb@gmail.com', 'tranbichb', '$2a$10$iOGnqWf.rE8N0gYY/TnYoOrl8gnyiHBrQirJSoCJOewfSgqasKppa', N'Trần Thị Bích', '0987654321', 2, 1);
GO

-- 9. SanPham (có FK: DanhMuc, ThuongHieu)
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
(N'Tinh Chất Trà Xanh Innisfree Green Tea Seed Serum', 1, 1, N'Dưỡng ẩm và chống oxy hóa cho da.', 450000, 550000, 'products/product-10.png'),
(N'Toner Innisfree Green Tea Balancing Skin', 1, 1, N'Cân bằng độ ẩm và làm dịu làn da.', 280000, 350000, 'products/product-11.png'),
(N'Kem Dưỡng La Roche-Posay Cicaplast Baume B5', 1, 2, N'Phục hồi da nhạy cảm, giảm kích ứng.', 350000, 420000, 'products/product-12.png'),
(N'Serum L''Oréal Revitalift Hyaluronic Acid', 1, 3, N'Cấp ẩm và làm mịn da tức thì.', 420000, 520000, 'products/product-13.png'),
(N'Nước Hoa Hồng Shiseido Treatment Softener', 1, 4, N'Làm mềm da, hỗ trợ hấp thụ dưỡng chất.', 950000, 1100000, 'products/product-14.png'),
(N'Kem Nền Maybelline Fit Me Matte + Poreless', 2, 5, N'Làm đều màu da, kiểm soát dầu tốt.', 200000, 250000, 'products/product-15.png'),
(N'Serum The Ordinary Retinol 1% in Squalane', 1, 6, N'Giảm nếp nhăn, tái tạo bề mặt da.', 210000, 280000, 'products/product-16.png'),
(N'Sữa Rửa Mặt Innisfree Jeju Volcanic Pore Foam', 1, 1, N'Làm sạch bã nhờn, thu nhỏ lỗ chân lông.', 220000, 280000, 'products/product-17.png'),
(N'Kem Chống Nắng Shiseido Clear Sunscreen Stick SPF 50+', 1, 4, N'Dạng thỏi tiện lợi, bảo vệ da khỏi tia UV.', 900000, 1100000, 'products/product-18.png'),
(N'Son Dưỡng Môi Maybelline Baby Lips', 2, 5, N'Dưỡng ẩm môi mềm mịn, màu nhẹ tự nhiên.', 80000, 120000, 'products/product-19.png'),
(N'Nước Hoa L''Oréal Libre Eau de Parfum', 5, 3, N'Hương thơm quyến rũ, sang trọng.', 2300000, 2800000, 'products/product-20.png'),
(N'Kem Dưỡng Innisfree Brightening Pore Cream', 1, 1, N'Làm sáng da, giảm thâm nám.', 420000, 500000, 'products/product-21.png'),
(N'Serum La Roche-Posay Hyalu B5', 1, 2, N'Cấp nước và phục hồi da mịn màng.', 600000, 750000, 'products/product-22.png'),
(N'Dầu Gội Shiseido Tsubaki Premium Repair', 3, 4, N'Phục hồi tóc khô xơ và hư tổn.', 320000, 400000, 'products/product-23.png'),
(N'Sữa Tắm L''Oréal Paris Elseve Extraordinary Oil', 4, 3, N'Làm mềm mượt da và lưu hương lâu.', 180000, 230000, 'products/product-24.png'),
(N'Phấn Má Maybelline Fit Me Blush', 2, 5, N'Màu sắc tự nhiên, dễ tán và bám lâu.', 160000, 210000, 'products/product-25.png'),
(N'Nước Hoa Shiseido Zen Eau de Parfum', 5, 4, N'Hương hoa cỏ thanh lịch, nữ tính.', 2500000, 3200000, 'products/product-26.png'),
(N'Mặt Nạ Ngủ Innisfree Aloe Revital', 1, 1, N'Dưỡng ẩm chuyên sâu trong khi ngủ.', 300000, 380000, 'products/product-27.png'),
(N'Tẩy Trang La Roche-Posay Micellar Water', 1, 2, N'Làm sạch dịu nhẹ, phù hợp da nhạy cảm.', 350000, 450000, 'products/product-28.png'),
(N'Kem Dưỡng Tóc L''Oréal Elseve Smooth Intense', 3, 3, N'Giúp tóc suôn mượt và dễ chải.', 150000, 200000, 'products/product-29.png'),
(N'Nước Hoa The Ordinary Floral Essence', 5, 6, N'Hương thơm nhẹ nhàng, thanh khiết.', 1800000, 2300000, 'products/product-30.png');
GO

-- 10. PhieuNhap (có FK: NhaCungCap)
INSERT INTO PhieuNhap(MaNCC, NgayTao) VALUES 
(1, GETDATE()), 
(2, GETDATE());
GO

-- 11. DiaChi (có FK: NguoiDung)
INSERT INTO DiaChi(MaNguoiDung, TenNguoiNhan, SoDienThoai, TinhThanh, QuanHuyen, PhuongXa, SoNhaDuong) VALUES
(2, N'Người Dùng Mẫu', '0900000002', N'TP. Hồ Chí Minh', N'Quận 1', N'Phường Bến Nghé', N'123 Lê Lợi'),
(3, N'Nguyễn Văn An', '0912345678', N'Hà Nội', N'Quận Ba Đình', N'Phường Điện Biên', N'456 Hoàng Diệu');
GO

-- 12. SanPhamYeuThich (có FK: NguoiDung, SanPham)
INSERT INTO SanPhamYeuThich (MaNguoiDung, MaSanPham) VALUES
(2, 1), (2, 3), (2, 5);
GO

-- 13. ChiTietPhieuNhap (có FK: PhieuNhap, SanPham)
INSERT INTO ChiTietPhieuNhap(MaPhieuNhap, MaSanPham, SoLuong, GiaNhap) VALUES
(1, 1, 100, 120000), 
(1, 2, 50, 250000),
(2, 3, 200, 90000), 
(2, 4, 80, 200000);
GO

-- 14. KhoHang (có FK: SanPham)
INSERT INTO KhoHang(MaSanPham, SoLuongTon, NgayNhapGanNhat)
SELECT MaSanPham, CAST(RAND(CHECKSUM(NEWID()))*100 AS INT) + 20, GETDATE() 
FROM SanPham;
GO

-- 15. GioHang (có FK: NguoiDung, SanPham)
INSERT INTO GioHang (MaNguoiDung, MaSanPham, SoLuong, DonGia) VALUES
(2, 2, 1, 350000),
(2, 4, 2, 280000);
GO

-- 16. DonHang (có FK: NguoiDung, KhuyenMai, DiaChi)
INSERT INTO DonHang(MaNguoiDung, TrangThai, PhuongThucThanhToan, TrangThaiThanhToan, TienHang, PhiVanChuyen, TongTien, TenNguoiNhan, SoDienThoaiNhan, DiaChiNhan, MaDiaChiNhan) VALUES
(3, N'Đã giao', N'COD', N'Đã thanh toán', 530000, 30000, 560000, N'Nguyễn Văn An', '0912345678', N'456 Hoàng Diệu, Phường Điện Biên, Quận Ba Đình, Hà Nội', 2),
(4, N'Đang xử lý', N'ONLINE', N'Đã thanh toán', 280000, 0, 280000, N'Trần Thị Bích', '0987654321', N'789 Nguyễn Huệ, Phường Bến Nghé, Quận 1, TP.HCM', NULL);
GO

-- 17. DanhGia (có FK: SanPham, NguoiDung)
INSERT INTO DanhGia(MaSanPham, MaNguoiDung, DiemDanhGia, BinhLuan) VALUES
(1, 3, 5, N'Sản phẩm tuyệt vời, kiềm dầu tốt!'),
(2, 3, 4, N'Chống nắng ổn, hơi nâng tone một chút.');
GO

-- 18. DonHang_ChiTiet (có FK: DonHang, SanPham)
INSERT INTO DonHang_ChiTiet(MaDonHang, MaSanPham, TenSanPham, DonGia, SoLuong) VALUES
(1, 1, N'Serum The Ordinary Niacinamide 10% + Zinc 1%', 180000, 1),
(1, 2, N'Kem Chống Nắng La Roche-Posay Anthelios', 350000, 1),
(2, 4, N'Mặt Nạ Trà Xanh Innisfree', 280000, 1);
GO

PRINT N'✅ Đã import dữ liệu mẫu thành công!';
GO