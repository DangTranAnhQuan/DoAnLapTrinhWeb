-- =============================================
-- SỬ DỤNG DATABASE VÀ XÓA DỮ LIỆU CŨ
-- =============================================
USE OneShop;
GO

-- Xóa dữ liệu theo thứ tự ngược lại (từ bảng con đến bảng cha)
DELETE FROM TinNhanChat;
DELETE FROM PhienChat;
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
DELETE FROM LienHe;
DELETE FROM PhiApDungTungTinh;
DELETE FROM PhiVanChuyen;
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
DBCC CHECKIDENT ('TinNhanChat', RESEED, 0);
DBCC CHECKIDENT ('LichSuTrangThaiDon', RESEED, 0);
DBCC CHECKIDENT ('VanChuyen', RESEED, 0);
DBCC CHECKIDENT ('DonHang', RESEED, 0);
DBCC CHECKIDENT ('PhieuNhap', RESEED, 0);
DBCC CHECKIDENT ('SanPham', RESEED, 0);
DBCC CHECKIDENT ('MaXacThuc', RESEED, 0);
DBCC CHECKIDENT ('DiaChi', RESEED, 0);
DBCC CHECKIDENT ('LienHe', RESEED, 0);
DBCC CHECKIDENT ('NguoiDung', RESEED, 0);
DBCC CHECKIDENT ('PhiVanChuyen', RESEED, 0);
DBCC CHECKIDENT ('NhaVanChuyen', RESEED, 0);
DBCC CHECKIDENT ('NhaCungCap', RESEED, 0);
DBCC CHECKIDENT ('ThuongHieu', RESEED, 0);
DBCC CHECKIDENT ('DanhMuc', RESEED, 0);
DBCC CHECKIDENT ('HangThanhVien', RESEED, 0);
GO

-- =============================================
-- THÊM DỮ LIỆU MẪU (ĐÃ VIẾT LẠI VÀ BỔ SUNG)
-- =============================================

-- 1. VaiTro (Bắt buộc)
INSERT INTO VaiTro (MaVaiTro, TenVaiTro) VALUES
(1, N'Admin'),
(2, N'User');
GO

-- 2. HangThanhVien (Nhiều hơn)
INSERT INTO HangThanhVien (TenHang, DiemToiThieu, PhanTramGiamGia) VALUES
(N'Thành viên Mới', 0, 0),
(N'Thành viên Bạc', 500, 2),
(N'Thành viên Vàng', 2000, 5),
(N'Thành viên Bạch Kim', 6000, 8);
GO

-- 3. DanhMuc (Giữ nguyên tên ảnh)
INSERT INTO DanhMuc (TenDanhMuc, HinhAnh) VALUES
(N'Chăm sóc da mặt', 'categories/cat-skincare.png'),
(N'Trang điểm', 'categories/cat-makeup.png'),
(N'Chăm sóc tóc', 'categories/cat-haircare.png'),
(N'Chăm sóc cơ thể', 'categories/cat-bodycare.png'),
(N'Nước hoa', 'categories/cat-fragrance.png');
GO

-- 4. ThuongHieu (Giữ nguyên tên ảnh)
INSERT INTO ThuongHieu (TenThuongHieu, MoTa, HinhAnh) VALUES
(N'Innisfree', N'Thương hiệu mỹ phẩm thiên nhiên từ đảo Jeju, Hàn Quốc.', 'brands/innisfree.jpg'),
(N'La Roche-Posay', N'Thương hiệu dược mỹ phẩm hàng đầu của Pháp.', 'brands/larocheposay.jpg'),
(N'L''Oréal', N'Tập đoàn mỹ phẩm hàng đầu thế giới.', 'brands/loreal.jpg'),
(N'Shiseido', N'Thương hiệu mỹ phẩm cao cấp từ Nhật Bản.', 'brands/shiseido.jpg'),
(N'Maybelline', N'Thương hiệu trang điểm số 1 thế giới từ New York.', 'brands/maybelline.jpg'),
(N'The Ordinary', N'Thương hiệu chăm sóc da với các thành phần khoa học.', 'brands/theordinary.png');
GO

-- 5. NhaCungCap (Nhiều hơn)
INSERT INTO NhaCungCap(TenNCC, SDT, DiaChi) VALUES
(N'Công ty TNHH Sắc Đẹp Toàn Cầu', '02811112222', N'111 Sư Vạn Hạnh, Q10, TP.HCM'),
(N'Nhà phân phối H&H', '02499998888', N'222 Bà Triệu, Hai Bà Trưng, Hà Nội'),
(N'Công ty Cổ phần Mỹ phẩm An Toàn', '02361234567', N'55 Nguyễn Văn Linh, Hải Châu, Đà Nẵng'),
(N'Nhà nhập khẩu K-Beauty', '02877778888', N'Khu đô thị Phú Mỹ Hưng, Quận 7, TP.HCM');
GO

-- =============================================
-- 6. NhaVanChuyen (5 nhà vận chuyển)
-- =============================================
INSERT INTO NhaVanChuyen(TenNVC, SoDienThoai, Website) VALUES
(N'J&T Express', '19001088', 'https://jtexpress.vn'),
(N'Ninja Van', '19009889', 'https://ninjavan.co'),
(N'Best Express', '19001031', 'https://best-inc.vn'),
(N'Viettel Post', '19008095', 'https://viettelpost.com.vn'),
(N'Giao Hàng Tiết Kiệm', '19006092', 'https://ghtk.vn');
GO

-- =============================================
-- 7. PhiVanChuyen & PhiApDungTungTinh (Viết lại theo 3 miền)
-- =============================================

-- ===== J&T Express (MaNVC = 1) =====
-- Cung cấp: Tiết Kiệm, Nhanh
-- Gói 1: Tiết Kiệm - Nội thành HCM
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Nội thành HCM (J&T)', 1, N'Tiết Kiệm', 16000, 2, 3, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (1, N'TP. Hồ Chí Minh');
-- Gói 2: Tiết Kiệm - Miền Nam (J&T)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Miền Nam (J&T)', 1, N'Tiết Kiệm', 22000, 2, 4, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (2, N'TP. Cần Thơ'), (2, N'An Giang'), (2, N'Cà Mau'), (2, N'Đồng Nai'), (2, N'Đồng Tháp'), (2, N'Tây Ninh'), (2, N'Vĩnh Long');
-- Gói 3: Tiết Kiệm - Miền Trung (J&T)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Miền Trung (J&T)', 1, N'Tiết Kiệm', 30000, 3, 5, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (3, N'TP. Đà Nẵng'), (3, N'TP. Huế'), (3, N'Đắk Lắk'), (3, N'Gia Lai'), (3, N'Hà Tĩnh'), (3, N'Khánh Hòa'), (3, N'Lâm Đồng'), (3, N'Nghệ An'), (3, N'Quảng Ngãi'), (3, N'Quảng Trị'), (3, N'Thanh Hóa');
-- Gói 4: Tiết Kiệm - Miền Bắc (J&T)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Miền Bắc (J&T)', 1, N'Tiết Kiệm', 35000, 4, 6, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (4, N'TP. Hà Nội'), (4, N'TP. Hải Phòng'), (4, N'Bắc Ninh'), (4, N'Cao Bằng'), (4, N'Điện Biên'), (4, N'Hưng Yên'), (4, N'Lai Châu'), (4, N'Lạng Sơn'), (4, N'Lào Cai'), (4, N'Ninh Bình'), (4, N'Phú Thọ'), (4, N'Quảng Ninh'), (4, N'Sơn La'), (4, N'Thái Nguyên'), (4, N'Tuyên Quang');

-- Gói 5: Nhanh - Nội thành HCM (J&T)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Nhanh - Nội thành HCM (J&T)', 1, N'Nhanh', 25000, 1, 1, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (5, N'TP. Hồ Chí Minh');
-- Gói 6: Nhanh - Miền Nam (J&T)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Nhanh - Miền Nam (J&T)', 1, N'Nhanh', 35000, 1, 2, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (6, N'TP. Cần Thơ'), (6, N'An Giang'), (6, N'Cà Mau'), (6, N'Đồng Nai'), (6, N'Đồng Tháp'), (6, N'Tây Ninh'), (6, N'Vĩnh Long');
-- Gói 7: Nhanh - Miền Trung (J&T)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Nhanh - Miền Trung (J&T)', 1, N'Nhanh', 45000, 2, 3, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (7, N'TP. Đà Nẵng'), (7, N'TP. Huế'), (7, N'Đắk Lắk'), (7, N'Gia Lai'), (7, N'Hà Tĩnh'), (7, N'Khánh Hòa'), (7, N'Lâm Đồng'), (7, N'Nghệ An'), (7, N'Quảng Ngãi'), (7, N'Quảng Trị'), (7, N'Thanh Hóa');
-- Gói 8: Nhanh - Miền Bắc (J&T)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Nhanh - Miền Bắc (J&T)', 1, N'Nhanh', 55000, 2, 4, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (8, N'TP. Hà Nội'), (8, N'TP. Hải Phòng'), (8, N'Bắc Ninh'), (8, N'Cao Bằng'), (8, N'Điện Biên'), (8, N'Hưng Yên'), (8, N'Lai Châu'), (8, N'Lạng Sơn'), (8, N'Lào Cai'), (8, N'Ninh Bình'), (8, N'Phú Thọ'), (8, N'Quảng Ninh'), (8, N'Sơn La'), (8, N'Thái Nguyên'), (8, N'Tuyên Quang');
GO

-- ===== Ninja Van (MaNVC = 2) =====
-- Cung cấp: Tiêu Chuẩn
-- Gói 9: Tiêu Chuẩn - Nội thành HCM (Ninja)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiêu Chuẩn - Nội thành HCM (Ninja)', 2, N'Tiêu Chuẩn', 18000, 1, 2, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (9, N'TP. Hồ Chí Minh');
-- Gói 10: Tiêu Chuẩn - Miền Nam (Ninja)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiêu Chuẩn - Miền Nam (Ninja)', 2, N'Tiêu Chuẩn', 24000, 2, 3, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (10, N'TP. Cần Thơ'), (10, N'An Giang'), (10, N'Cà Mau'), (10, N'Đồng Nai'), (10, N'Đồng Tháp'), (10, N'Tây Ninh'), (10, N'Vĩnh Long');
-- Gói 11: Tiêu Chuẩn - Miền Trung (Ninja)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiêu Chuẩn - Miền Trung (Ninja)', 2, N'Tiêu Chuẩn', 32000, 3, 5, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (11, N'TP. Đà Nẵng'), (11, N'TP. Huế'), (11, N'Đắk Lắk'), (11, N'Gia Lai'), (11, N'Hà Tĩnh'), (11, N'Khánh Hòa'), (11, N'Lâm Đồng'), (11, N'Nghệ An'), (11, N'Quảng Ngãi'), (11, N'Quảng Trị'), (11, N'Thanh Hóa');
-- Gói 12: Tiêu Chuẩn - Miền Bắc (Ninja)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiêu Chuẩn - Miền Bắc (Ninja)', 2, N'Tiêu Chuẩn', 38000, 4, 6, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (12, N'TP. Hà Nội'), (12, N'TP. Hải Phòng'), (12, N'Bắc Ninh'), (12, N'Cao Bằng'), (12, N'Điện Biên'), (12, N'Hưng Yên'), (12, N'Lai Châu'), (12, N'Lạng Sơn'), (12, N'Lào Cai'), (12, N'Ninh Bình'), (12, N'Phú Thọ'), (12, N'Quảng Ninh'), (12, N'Sơn La'), (12, N'Thái Nguyên'), (12, N'Tuyên Quang');
GO

-- ===== Best Express (MaNVC = 3) =====
-- Cung cấp: Tiết Kiệm, Tiêu Chuẩn
-- Gói 13: Tiết Kiệm - Nội thành HCM (Best)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Nội thành HCM (Best)', 3, N'Tiết Kiệm', 15000, 2, 3, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (13, N'TP. Hồ Chí Minh');
-- Gói 14: Tiết Kiệm - Miền Nam (Best)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Miền Nam (Best)', 3, N'Tiết Kiệm', 20000, 3, 5, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (14, N'TP. Cần Thơ'), (14, N'An Giang'), (14, N'Cà Mau'), (14, N'Đồng Nai'), (14, N'Đồng Tháp'), (14, N'Tây Ninh'), (14, N'Vĩnh Long');
-- Gói 15: Tiết Kiệm - Miền Trung (Best)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Miền Trung (Best)', 3, N'Tiết Kiệm', 28000, 4, 6, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (15, N'TP. Đà Nẵng'), (15, N'TP. Huế'), (15, N'Đắk Lắk'), (15, N'Gia Lai'), (15, N'Hà Tĩnh'), (15, N'Khánh Hòa'), (15, N'Lâm Đồng'), (15, N'Nghệ An'), (15, N'Quảng Ngãi'), (15, N'Quảng Trị'), (15, N'Thanh Hóa');
-- Gói 16: Tiết Kiệm - Miền Bắc (Best)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Miền Bắc (Best)', 3, N'Tiết Kiệm', 33000, 5, 7, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (16, N'TP. Hà Nội'), (16, N'TP. Hải Phòng'), (16, N'Bắc Ninh'), (16, N'Cao Bằng'), (16, N'Điện Biên'), (16, N'Hưng Yên'), (16, N'Lai Châu'), (16, N'Lạng Sơn'), (16, N'Lào Cai'), (16, N'Ninh Bình'), (16, N'Phú Thọ'), (16, N'Quảng Ninh'), (16, N'Sơn La'), (16, N'Thái Nguyên'), (16, N'Tuyên Quang');

-- Gói 17: Tiêu Chuẩn - Nội thành HCM (Best)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiêu Chuẩn - Nội thành HCM (Best)', 3, N'Tiêu Chuẩn', 20000, 1, 2, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (17, N'TP. Hồ Chí Minh');
-- Gói 18: Tiêu Chuẩn - Miền Nam (Best)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiêu Chuẩn - Miền Nam (Best)', 3, N'Tiêu Chuẩn', 25000, 2, 4, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (18, N'TP. Cần Thơ'), (18, N'An Giang'), (18, N'Cà Mau'), (18, N'Đồng Nai'), (18, N'Đồng Tháp'), (18, N'Tây Ninh'), (18, N'Vĩnh Long');
-- Gói 19: Tiêu Chuẩn - Miền Trung (Best)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiêu Chuẩn - Miền Trung (Best)', 3, N'Tiêu Chuẩn', 33000, 3, 5, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (19, N'TP. Đà Nẵng'), (19, N'TP. Huế'), (19, N'Đắk Lắk'), (19, N'Gia Lai'), (19, N'Hà Tĩnh'), (19, N'Khánh Hòa'), (19, N'Lâm Đồng'), (19, N'Nghệ An'), (19, N'Quảng Ngãi'), (19, N'Quảng Trị'), (19, N'Thanh Hóa');
-- Gói 20: Tiêu Chuẩn - Miền Bắc (Best)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiêu Chuẩn - Miền Bắc (Best)', 3, N'Tiêu Chuẩn', 39000, 4, 6, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (20, N'TP. Hà Nội'), (20, N'TP. Hải Phòng'), (20, N'Bắc Ninh'), (20, N'Cao Bằng'), (20, N'Điện Biên'), (20, N'Hưng Yên'), (20, N'Lai Châu'), (20, N'Lạng Sơn'), (20, N'Lào Cai'), (20, N'Ninh Bình'), (20, N'Phú Thọ'), (20, N'Quảng Ninh'), (20, N'Sơn La'), (20, N'Thái Nguyên'), (20, N'Tuyên Quang');
GO

-- ===== Viettel Post (MaNVC = 4) =====
-- Cung cấp: Tiết Kiệm, Nhanh
-- Gói 21: Tiết Kiệm - Nội thành HCM (VTP)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Nội thành HCM (VTP)', 4, N'Tiết Kiệm', 17000, 2, 3, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (21, N'TP. Hồ Chí Minh');
-- Gói 22: Tiết Kiệm - Miền Nam (VTP)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Miền Nam (VTP)', 4, N'Tiết Kiệm', 23000, 2, 4, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (22, N'TP. Cần Thơ'), (22, N'An Giang'), (22, N'Cà Mau'), (22, N'Đồng Nai'), (22, N'Đồng Tháp'), (22, N'Tây Ninh'), (22, N'Vĩnh Long');
-- Gói 23: Tiết Kiệm - Miền Trung (VTP)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Miền Trung (VTP)', 4, N'Tiết Kiệm', 30000, 3, 5, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (23, N'TP. Đà Nẵng'), (23, N'TP. Huế'), (23, N'Đắk Lắk'), (23, N'Gia Lai'), (23, N'Hà Tĩnh'), (23, N'Khánh Hòa'), (23, N'Lâm Đồng'), (23, N'Nghệ An'), (23, N'Quảng Ngãi'), (23, N'Quảng Trị'), (23, N'Thanh Hóa');
-- Gói 24: Tiết Kiệm - Miền Bắc (VTP)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Miền Bắc (VTP)', 4, N'Tiết Kiệm', 36000, 4, 6, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (24, N'TP. Hà Nội'), (24, N'TP. Hải Phòng'), (24, N'Bắc Ninh'), (24, N'Cao Bằng'), (24, N'Điện Biên'), (24, N'Hưng Yên'), (24, N'Lai Châu'), (24, N'Lạng Sơn'), (24, N'Lào Cai'), (24, N'Ninh Bình'), (24, N'Phú Thọ'), (24, N'Quảng Ninh'), (24, N'Sơn La'), (24, N'Thái Nguyên'), (24, N'Tuyên Quang');

-- Gói 25: Nhanh - Nội thành HCM (VTP)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Nhanh - Nội thành HCM (VTP)', 4, N'Nhanh', 28000, 1, 1, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (25, N'TP. Hồ Chí Minh');
-- Gói 26: Nhanh - Miền Nam (VTP)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Nhanh - Miền Nam (VTP)', 4, N'Nhanh', 38000, 1, 2, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (26, N'TP. Cần Thơ'), (26, N'An Giang'), (26, N'Cà Mau'), (26, N'Đồng Nai'), (26, N'Đồng Tháp'), (26, N'Tây Ninh'), (26, N'Vĩnh Long');
-- Gói 27: Nhanh - Miền Trung (VTP)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Nhanh - Miền Trung (VTP)', 4, N'Nhanh', 48000, 2, 3, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (27, N'TP. Đà Nẵng'), (27, N'TP. Huế'), (27, N'Đắk Lắk'), (27, N'Gia Lai'), (27, N'Hà Tĩnh'), (27, N'Khánh Hòa'), (27, N'Lâm Đồng'), (27, N'Nghệ An'), (27, N'Quảng Ngãi'), (27, N'Quảng Trị'), (27, N'Thanh Hóa');
-- Gói 28: Nhanh - Miền Bắc (VTP)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Nhanh - Miền Bắc (VTP)', 4, N'Nhanh', 58000, 2, 4, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (28, N'TP. Hà Nội'), (28, N'TP. Hải Phòng'), (28, N'Bắc Ninh'), (28, N'Cao Bằng'), (28, N'Điện Biên'), (28, N'Hưng Yên'), (28, N'Lai Châu'), (28, N'Lạng Sơn'), (28, N'Lào Cai'), (28, N'Ninh Bình'), (28, N'Phú Thọ'), (28, N'Quảng Ninh'), (28, N'Sơn La'), (28, N'Thái Nguyên'), (28, N'Tuyên Quang');
GO

-- ===== Giao Hàng Tiết Kiệm (MaNVC = 5) =====
-- Cung cấp: Tiết Kiệm, Nhanh
-- Gói 29: Tiết Kiệm - Nội thành HCM (GHTK)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Nội thành HCM (GHTK)', 5, N'Tiết Kiệm', 17000, 2, 3, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (29, N'TP. Hồ Chí Minh');
-- Gói 30: Tiết Kiệm - Miền Nam (GHTK)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Miền Nam (GHTK)', 5, N'Tiết Kiệm', 21000, 2, 4, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (30, N'TP. Cần Thơ'), (30, N'An Giang'), (30, N'Cà Mau'), (30, N'Đồng Nai'), (30, N'Đồng Tháp'), (30, N'Tây Ninh'), (30, N'Vĩnh Long');
-- Gói 31: Tiết Kiệm - Miền Trung (GHTK)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Miền Trung (GHTK)', 5, N'Tiết Kiệm', 29000, 3, 5, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (31, N'TP. Đà Nẵng'), (31, N'TP. Huế'), (31, N'Đắk Lắk'), (31, N'Gia Lai'), (31, N'Hà Tĩnh'), (31, N'Khánh Hòa'), (31, N'Lâm Đồng'), (31, N'Nghệ An'), (31, N'Quảng Ngãi'), (31, N'Quảng Trị'), (31, N'Thanh Hóa');
-- Gói 32: Tiết Kiệm - Miền Bắc (GHTK)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Tiết Kiệm - Miền Bắc (GHTK)', 5, N'Tiết Kiệm', 34000, 4, 6, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (32, N'TP. Hà Nội'), (32, N'TP. Hải Phòng'), (32, N'Bắc Ninh'), (32, N'Cao Bằng'), (32, N'Điện Biên'), (32, N'Hưng Yên'), (32, N'Lai Châu'), (32, N'Lạng Sơn'), (32, N'Lào Cai'), (32, N'Ninh Bình'), (32, N'Phú Thọ'), (32, N'Quảng Ninh'), (32, N'Sơn La'), (32, N'Thái Nguyên'), (32, N'Tuyên Quang');

-- Gói 33: Nhanh - Nội thành HCM (GHTK)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Nhanh - Nội thành HCM (GHTK)', 5, N'Nhanh', 27000, 1, 1, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (33, N'TP. Hồ Chí Minh');
-- Gói 34: Nhanh - Miền Nam (GHTK)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Nhanh - Miền Nam (GHTK)', 5, N'Nhanh', 36000, 1, 2, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (34, N'TP. Cần Thơ'), (34, N'An Giang'), (34, N'Cà Mau'), (34, N'Đồng Nai'), (34, N'Đồng Tháp'), (34, N'Tây Ninh'), (34, N'Vĩnh Long');
-- Gói 35: Nhanh - Miền Trung (GHTK)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Nhanh - Miền Trung (GHTK)', 5, N'Nhanh', 46000, 2, 3, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (35, N'TP. Đà Nẵng'), (35, N'TP. Huế'), (35, N'Đắk Lắk'), (35, N'Gia Lai'), (35, N'Hà Tĩnh'), (35, N'Khánh Hòa'), (35, N'Lâm Đồng'), (35, N'Nghệ An'), (35, N'Quảng Ngãi'), (35, N'Quảng Trị'), (35, N'Thanh Hóa');
-- Gói 36: Nhanh - Miền Bắc (GHTK)
INSERT INTO PhiVanChuyen(TenGoiCuoc, MaNVC, PhuongThucVanChuyen, ChiPhi, NgayGiaoSomNhat, NgayGiaoMuonNhat, DonViThoiGian) VALUES (N'Gói Nhanh - Miền Bắc (GHTK)', 5, N'Nhanh', 57000, 2, 4, N'Ngày');
INSERT INTO PhiApDungTungTinh(MaChiPhiVC, TenTinhThanh) VALUES (36, N'TP. Hà Nội'), (36, N'TP. Hải Phòng'), (36, N'Bắc Ninh'), (36, N'Cao Bằng'), (36, N'Điện Biên'), (36, N'Hưng Yên'), (36, N'Lai Châu'), (36, N'Lạng Sơn'), (36, N'Lào Cai'), (36, N'Ninh Bình'), (36, N'Phú Thọ'), (36, N'Quảng Ninh'), (36, N'Sơn La'), (36, N'Thái Nguyên'), (36, N'Tuyên Quang');
GO


-- 8. KhuyenMai (Nhiều hơn)
INSERT INTO KhuyenMai(MaKhuyenMai, TenChienDich, KieuApDung, GiaTri, BatDauLuc, KetThucLuc, TrangThai, TongTienToiThieu, GiamToiDa) VALUES
('KHAOHE', N'Khảo hè giảm 15%', 1, 15, '2025-06-01', '2025-07-01', 1, 300000, 100000),
('GIAM20K', N'Giảm 20K cho đơn từ 200K', 0, 20000, '2025-01-01', '2025-12-31', 1, 200000, NULL),
('FREESHIPMAX', N'Miễn phí vận chuyển tối đa 25K', 0, 25000, '2025-01-01', '2025-12-31', 1, 150000, 25000),
('HETHAN', N'Voucher 10.10 đã hết hạn', 0, 100000, '2024-10-10', '2024-10-11', 0, 500000, NULL);
GO

-- 9. NguoiDung (Nhiều hơn, với điểm tích lũy)
INSERT INTO NguoiDung (Email, TenDangNhap, MatKhau, HoTen, SoDienThoai, MaVaiTro, MaHangThanhVien, DiemTichLuy) VALUES
('admin@oneshop.com', 'admin', '$2a$10$lSDfM3BVszF21J4ej8c8Qu0g8.e3JtNDxbiqUlvT.gfJiV2ikc3iW', N'Admin Chính', '0901234567', 1, 4, 0), -- Hạng Bạch Kim (manual)
('lethilan@gmail.com', 'lethilan', '$2a$10$pMk7ufY9lnfjJZy5nTIMzushp586n2ZLJlYL7BDGoTgVmUAGQmbc6', N'Lê Thị Lan', '0901111222', 2, 3, 2100), -- Hạng Vàng
('phamhung@gmail.com', 'phamhung', '$2a$10$iOGnqWf.rE8N0gYY/TnYoOrl8gnyiHBrQirJSoCJOewfSgqasKppa', N'Phạm Hùng', '0902222333', 2, 2, 750), -- Hạng Bạc
('dinhmai@gmail.com', 'dinhmai', '$2a$10$iOGnqWf.rE8N0gYY/TnYoOrl8gnyiHBrQirJSoCJOewfSgqasKppa', N'Đinh Thị Mai', '0903333444', 2, 1, 0), -- Hạng Mới
('trantuan@gmail.com', 'trantuan', '$2a$10$iOGnqWf.rE8N0gYY/TnYoOrl8gnyiHBrQirJSoCJOewfSgqasKppa', N'Trần Anh Tuấn', '0904444555', 2, 4, 6200), -- Hạng Bạch Kim
('vubinh@gmail.com', 'vubinh', '$2a$10$iOGnqWf.rE8N0gYY/TnYoOrl8gnyiHBrQirJSoCJOewfSgqasKppa', N'Vũ An Bình', '0905555666', 2, 1, 150), -- Hạng Mới
('nguyenchau@gmail.com', 'nguyenchau', '$2a$10$iOGnqWf.rE8N0gYY/TnYoOrl8gnyiHBrQirJSoCJOewfSgqasKppa', N'Nguyễn Minh Châu', '0906666777', 2, 2, 1200); -- Hạng Bạc
GO

-- 10. SanPham (Giữ nguyên tên ảnh)
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

-- 11. PhieuNhap (Nhiều hơn)
INSERT INTO PhieuNhap(MaNCC, NgayTao) VALUES 
(1, GETDATE()), 
(2, DATEADD(day, -10, GETDATE())),
(3, DATEADD(day, -20, GETDATE())),
(4, DATEADD(day, -30, GETDATE()));
GO

-- 12. DiaChi (Nhiều hơn, dựa trên vn-divisions.json)
-- User 2 (Lê Thị Lan) - Hà Nội, Ba Đình, Vĩnh Phúc
INSERT INTO DiaChi(MaNguoiDung, TenNguoiNhan, SoDienThoai, TinhThanh, QuanHuyen, PhuongXa, SoNhaDuong) VALUES
(2, N'Lê Thị Lan', '0901111222', N'TP. Hà Nội', N'Quận Ba Đình', N'Phường Vĩnh Phúc', N'Số 10, ngõ 55 Hoàng Hoa Thám');
-- User 3 (Phạm Hùng) - TP.HCM, TP Thủ Đức, Linh Trung
INSERT INTO DiaChi(MaNguoiDung, TenNguoiNhan, SoDienThoai, TinhThanh, QuanHuyen, PhuongXa, SoNhaDuong) VALUES
(3, N'Phạm Hùng', '0902222333', N'TP. Hồ Chí Minh', N'TP Thủ Đức', N'Phường Linh Trung', N'Ký túc xá khu A, ĐHQG');
-- User 4 (Đinh Thị Mai) - Đà Nẵng, Hải Châu, Hải Châu 1
INSERT INTO DiaChi(MaNguoiDung, TenNguoiNhan, SoDienThoai, TinhThanh, QuanHuyen, PhuongXa, SoNhaDuong) VALUES
(4, N'Đinh Thị Mai', '0903333444', N'TP. Đà Nẵng', N'Quận Hải Châu', N'Phường Hải Châu 1', N'200 Bạch Đằng, ven sông Hàn');
-- User 5 (Trần Anh Tuấn) - Hải Phòng, Lê Chân
INSERT INTO DiaChi(MaNguoiDung, TenNguoiNhan, SoDienThoai, TinhThanh, QuanHuyen, PhuongXa, SoNhaDuong) VALUES
(5, N'Trần Anh Tuấn', '0904444555', N'TP. Hải Phòng', N'Quận Lê Chân', N'Phường An Dương', N'125 Lạch Tray');
-- User 6 (Vũ An Bình) - Cần Thơ, Ninh Kiều
INSERT INTO DiaChi(MaNguoiDung, TenNguoiNhan, SoDienThoai, TinhThanh, QuanHuyen, PhuongXa, SoNhaDuong) VALUES
(6, N'Vũ An Bình', '0905555666', N'TP. Cần Thơ', N'Quận Ninh Kiều', N'Phường An Khánh', N'33 đường 3/2');
-- User 7 (Nguyễn Minh Châu) - Nghệ An, TP Vinh (Không có trong json, tự thêm)
INSERT INTO DiaChi(MaNguoiDung, TenNguoiNhan, SoDienThoai, TinhThanh, QuanHuyen, PhuongXa, SoNhaDuong) VALUES
(7, N'Nguyễn Minh Châu', '0906666777', N'Nghệ An', N'TP. Vinh', N'Phường Hưng Bình', N'45 Lý Thường Kiệt');
GO

-- 13. SanPhamYeuThich (Nhiều hơn)
INSERT INTO SanPhamYeuThich (MaNguoiDung, MaSanPham) VALUES
(2, 1), (2, 6), (3, 2), (5, 6), (5, 8), (7, 5);
GO

-- 14. ChiTietPhieuNhap (Nhiều hơn)
INSERT INTO ChiTietPhieuNhap(MaPhieuNhap, MaSanPham, SoLuong, GiaNhap) VALUES
(1, 1, 50, 100000),
(1, 2, 30, 600000),
(2, 3, 100, 80000),
(2, 4, 100, 200000),
(3, 5, 40, 350000),
(3, 6, 20, 700000),
(4, 7, 100, 180000),
(4, 8, 50, 350000);
GO

-- 15. KhoHang (Tăng số lượng)
INSERT INTO KhoHang(MaSanPham, SoLuongTon, NgayNhapGanNhat)
SELECT MaSanPham, CAST(RAND(CHECKSUM(NEWID()))*150 AS INT) + 30, GETDATE()
FROM SanPham;
GO

-- 16. GioHang (Nhiều hơn)
-- User 4 (Đinh Thị Mai)
INSERT INTO GioHang (MaNguoiDung, MaSanPham, SoLuong, DonGia) VALUES
(4, 1, 1, 150000),
(4, 7, 2, 220000);
-- User 7 (Nguyễn Minh Châu)
INSERT INTO GioHang (MaNguoiDung, MaSanPham, SoLuong, DonGia) VALUES
(7, 3, 1, 120000);
GO

-- 17. DonHang (Nhiều hơn, đa dạng trạng thái)
-- Đơn 1: Lê Thị Lan, đã giao (Hà Nội, Tiêu chuẩn Ninja Van)
INSERT INTO DonHang(MaNguoiDung, TrangThai, PhuongThucThanhToan, TrangThaiThanhToan, TienHang, PhiVanChuyen, TongTien, TenNguoiNhan, SoDienThoaiNhan, DiaChiNhan, MaDiaChiNhan, PhuongThucVanChuyen, MaKhuyenMai) VALUES
(2, N'Đã giao', N'COD', N'Đã thanh toán', 870000, 22000, 892000, N'Lê Thị Lan', '0901111222', N'Số 10, ngõ 55 Hoàng Hoa Thám, Phường Vĩnh Phúc, Quận Ba Đình, TP. Hà Nội', 1, N'Tiêu Chuẩn', NULL);
-- Đơn 2: Phạm Hùng, đang giao (HCM, Hỏa tốc J&T, có giảm giá 20k)
INSERT INTO DonHang(MaNguoiDung, TrangThai, PhuongThucThanhToan, TrangThaiThanhToan, TienHang, PhiVanChuyen, TongTien, TenNguoiNhan, SoDienThoaiNhan, DiaChiNhan, MaDiaChiNhan, PhuongThucVanChuyen, MaKhuyenMai) VALUES
(3, N'Đang giao', N'ONLINE', N'Đã thanh toán', 450000, 40000, 470000, N'Phạm Hùng', '0902222333', N'Ký túc xá khu A, ĐHQG, Phường Linh Trung, TP Thủ Đức, TP. Hồ Chí Minh', 2, N'Hỏa Tốc', 'GIAM20K');
-- Đơn 3: Lê Thị Lan, đang xử lý (Địa chỉ mới ở Huế, Tiết kiệm Best)
INSERT INTO DonHang(MaNguoiDung, TrangThai, PhuongThucThanhToan, TrangThaiThanhToan, TienHang, PhiVanChuyen, TongTien, TenNguoiNhan, SoDienThoaiNhan, DiaChiNhan, MaDiaChiNhan, PhuongThucVanChuyen, MaKhuyenMai) VALUES
(2, N'Đang xử lý', N'ONLINE', N'Chưa thanh toán', 800000, 18000, 818000, N'Lê Thị Lan (VP Huế)', '0901111222', N'15 Lê Lợi, Phường Vĩnh Ninh, TP Huế, TP. Huế', 3, N'Tiết Kiệm', NULL);
-- Đơn 4: Trần Anh Tuấn, đã giao (Hải Phòng, Tiêu chuẩn GHTK, giảm giá 15%)
INSERT INTO DonHang(MaNguoiDung, TrangThai, PhuongThucThanhToan, TrangThaiThanhToan, TienHang, PhiVanChuyen, TongTien, TenNguoiNhan, SoDienThoaiNhan, DiaChiNhan, MaDiaChiNhan, PhuongThucVanChuyen, MaKhuyenMai) VALUES
(5, N'Đã giao', N'ONLINE', N'Đã thanh toán', 570000, 21000, 505500, N'Trần Anh Tuấn', '0904444555', N'125 Lạch Tray, Phường An Dương, Quận Lê Chân, TP. Hải Phòng', 4, N'Tiêu Chuẩn', 'KHAOHE');
-- Đơn 5: Vũ An Bình, đã hủy (Cần Thơ, Tiết kiệm Viettel)
INSERT INTO DonHang(MaNguoiDung, TrangThai, PhuongThucThanhToan, TrangThaiThanhToan, TienHang, PhiVanChuyen, TongTien, TenNguoiNhan, SoDienThoaiNhan, DiaChiNhan, MaDiaChiNhan, PhuongThucVanChuyen, MaKhuyenMai) VALUES
(6, N'Đã hủy', N'COD', N'Chưa thanh toán', 120000, 19000, 139000, N'Vũ An Bình', '0905555666', N'33 đường 3/2, Phường An Khánh, Quận Ninh Kiều, TP. Cần Thơ', 5, N'Tiết Kiệm', NULL);
-- Đơn 6: Nguyễn Minh Châu, đang xử lý (Nghệ An, Tiết kiệm GHTK)
INSERT INTO DonHang(MaNguoiDung, TrangThai, PhuongThucThanhToan, TrangThaiThanhToan, TienHang, PhiVanChuyen, TongTien, TenNguoiNhan, SoDienThoaiNhan, DiaChiNhan, MaDiaChiNhan, PhuongThucVanChuyen, MaKhuyenMai) VALUES
(7, N'Đang xử lý', N'COD', N'Chưa thanh toán', 1020000, 24000, 1044000, N'Nguyễn Minh Châu', '0906666777', N'45 Lý Thường Kiệt, Phường Hưng Bình, TP. Vinh, Nghệ An', 6, N'Tiết Kiệm', NULL);
-- Đơn 7: Phạm Hùng, trả hàng (HCM, Tiêu chuẩn J&T)
INSERT INTO DonHang(MaNguoiDung, TrangThai, PhuongThucThanhToan, TrangThaiThanhToan, TienHang, PhiVanChuyen, TongTien, TenNguoiNhan, SoDienThoaiNhan, DiaChiNhan, MaDiaChiNhan, PhuongThucVanChuyen, MaKhuyenMai) VALUES
(3, N'Trả hàng-Hoàn tiền', N'ONLINE', N'Đã thanh toán', 280000, 20000, 300000, N'Phạm Hùng', '0902222333', N'Ký túc xá khu A, ĐHQG, Phường Linh Trung, TP Thủ Đức, TP. Hồ Chí Minh', 2, N'Tiêu Chuẩn', NULL);
-- Đơn 8: Đinh Thị Mai, đã giao (Đà Nẵng, Tiêu chuẩn Viettel)
INSERT INTO DonHang(MaNguoiDung, TrangThai, PhuongThucThanhToan, TrangThaiThanhToan, TienHang, PhiVanChuyen, TongTien, TenNguoiNhan, SoDienThoaiNhan, DiaChiNhan, MaDiaChiNhan, PhuongThucVanChuyen, MaKhuyenMai) VALUES
(4, N'Đã giao', N'COD', N'Chưa thanh toán', 750000, 23000, 773000, N'Đinh Thị Mai', '0903333444', N'200 Bạch Đằng, ven sông Hàn, Phường Hải Châu 1, Quận Hải Châu, TP. Đà Nẵng', 3, N'Tiêu Chuẩn', NULL);
GO

-- 18. DonHang_ChiTiet (Nhiều hơn)
-- Đơn 1 (Tiền hàng 870k)
INSERT INTO DonHang_ChiTiet(MaDonHang, MaSanPham, TenSanPham, DonGia, SoLuong) VALUES
(1, 2, N'Kem Dưỡng Ẩm Vichy Aqualia Thermal', 750000, 1),
(1, 3, N'Nước Tẩy Trang Garnier Micellar Water', 120000, 1);
-- Đơn 2 (Tiền hàng 450k)
INSERT INTO DonHang_ChiTiet(MaDonHang, MaSanPham, TenSanPham, DonGia, SoLuong) VALUES
(2, 5, N'Tinh Chất Trị Mụn Eucerin Pro Acne', 450000, 1);
-- Đơn 3 (Tiền hàng 800k)
INSERT INTO DonHang_ChiTiet(MaDonHang, MaSanPham, TenSanPham, DonGia, SoLuong) VALUES
(3, 6, N'BHA 2% Paula''s Choice Liquid Exfoliant', 800000, 1);
-- Đơn 4 (Tiền hàng 570k)
INSERT INTO DonHang_ChiTiet(MaDonHang, MaSanPham, TenSanPham, DonGia, SoLuong) VALUES
(4, 1, N'Sữa Rửa Mặt Cỏ Mềm Trà Tràm', 150000, 1),
(4, 8, N'Kem Chống Nắng Vichy Capital Soleil', 420000, 1);
-- Đơn 5 (Tiền hàng 120k)
INSERT INTO DonHang_ChiTiet(MaDonHang, MaSanPham, TenSanPham, DonGia, SoLuong) VALUES
(5, 3, N'Nước Tẩy Trang Garnier Micellar Water', 120000, 1);
-- Đơn 6 (Tiền hàng 1020k)
INSERT INTO DonHang_ChiTiet(MaDonHang, MaSanPham, TenSanPham, DonGia, SoLuong) VALUES
(6, 6, N'BHA 2% Paula''s Choice Liquid Exfoliant', 800000, 1),
(6, 7, N'Dưỡng Thể Cỏ Mềm Hương Gạo', 220000, 1);
-- Đơn 7 (Tiền hàng 280k)
INSERT INTO DonHang_ChiTiet(MaDonHang, MaSanPham, TenSanPham, DonGia, SoLuong) VALUES
(7, 4, N'Dung Dịch Chống Nắng Hada Labo', 280000, 1);
-- Đơn 8 (Tiền hàng 750k)
INSERT INTO DonHang_ChiTiet(MaDonHang, MaSanPham, TenSanPham, DonGia, SoLuong) VALUES
(8, 2, N'Kem Dưỡng Ẩm Vichy Aqualia Thermal', 750000, 1);
GO

-- 19. DanhGia (Giữ nguyên)
INSERT INTO DanhGia(MaSanPham, MaNguoiDung, DiemDanhGia, BinhLuan) VALUES
(2, 2, 5, N'Dùng rất thích, cấp ẩm tốt, da mềm mịn.'),
(3, 2, 4, N'Tẩy trang sạch, không bị khô da, sẽ mua lại.');
GO

-- 20. VanChuyen (Nhiều hơn)
-- Đơn 1 (Đã giao) dùng Ninja Van (MaNVC = 2)
INSERT INTO VanChuyen(MaDonHang, MaNVC, MaVanDon, GuiLuc, GiaoLuc, TrangThai)
VALUES (1, 2, 'NJV111111', DATEADD(day, -2, GETDATE()), DATEADD(day, -1, GETDATE()), N'Đã giao');
-- Đơn 2 (Đang giao) dùng J&T (MaNVC = 1)
INSERT INTO VanChuyen(MaDonHang, MaNVC, MaVanDon, GuiLuc, GiaoLuc, TrangThai)
VALUES (2, 1, 'JT222222', GETDATE(), NULL, N'Đang giao');
-- Đơn 4 (Đã giao) dùng GHTK (MaNVC = 5)
INSERT INTO VanChuyen(MaDonHang, MaNVC, MaVanDon, GuiLuc, GiaoLuc, TrangThai)
VALUES (4, 5, 'GHTK333333', DATEADD(day, -5, GETDATE()), DATEADD(day, -3, GETDATE()), N'Đã giao');
-- Đơn 7 (Trả hàng) dùng J&T (MaNVC = 1)
INSERT INTO VanChuyen(MaDonHang, MaNVC, MaVanDon, GuiLuc, GiaoLuc, TrangThai)
VALUES (7, 1, 'JT444444', DATEADD(day, -10, GETDATE()), DATEADD(day, -8, GETDATE()), N'Trả hàng');
-- Đơn 8 (Đã giao) dùng Viettel (MaNVC = 4)
INSERT INTO VanChuyen(MaDonHang, MaNVC, MaVanDon, GuiLuc, GiaoLuc, TrangThai)
VALUES (8, 4, 'VT555555', DATEADD(day, -3, GETDATE()), GETDATE(), N'Đã giao');
GO

-- 21. LichSuTrangThaiDon (Nhiều hơn, MaQuanTriVien = 1 (Admin))
-- Đơn 1
INSERT INTO LichSuTrangThaiDon(MaDonHang, TuTrangThai, DenTrangThai, MaQuanTriVien, ThoiDiemThayDoi) VALUES
(1, N'Đang xử lý', N'Đã xác nhận', 1, DATEADD(day, -3, GETDATE())),
(1, N'Đã xác nhận', N'Đang giao', 1, DATEADD(day, -2, GETDATE())),
(1, N'Đang giao', N'Đã giao', 1, DATEADD(day, -1, GETDATE()));
-- Đơn 2
INSERT INTO LichSuTrangThaiDon(MaDonHang, TuTrangThai, DenTrangThai, MaQuanTriVien, ThoiDiemThayDoi) VALUES
(2, N'Đang xử lý', N'Đã xác nhận', 1, DATEADD(hour, -2, GETDATE())),
(2, N'Đã xác nhận', N'Đang giao', 1, GETDATE());
-- Đơn 4
INSERT INTO LichSuTrangThaiDon(MaDonHang, TuTrangThai, DenTrangThai, MaQuanTriVien, ThoiDiemThayDoi) VALUES
(4, N'Đang xử lý', N'Đã xác nhận', 1, DATEADD(day, -6, GETDATE())),
(4, N'Đã xác nhận', N'Đang giao', 1, DATEADD(day, -5, GETDATE())),
(4, N'Đang giao', N'Đã giao', 1, DATEADD(day, -3, GETDATE()));
-- Đơn 5
INSERT INTO LichSuTrangThaiDon(MaDonHang, TuTrangThai, DenTrangThai, MaQuanTriVien, ThoiDiemThayDoi) VALUES
(5, N'Đang xử lý', N'Đã hủy', 1, DATEADD(day, -1, GETDATE()));
-- Đơn 7
INSERT INTO LichSuTrangThaiDon(MaDonHang, TuTrangThai, DenTrangThai, MaQuanTriVien, ThoiDiemThayDoi) VALUES
(7, N'Đang xử lý', N'Đã xác nhận', 1, DATEADD(day, -12, GETDATE())),
(7, N'Đã xác nhận', N'Đang giao', 1, DATEADD(day, -10, GETDATE())),
(7, N'Đang giao', N'Đã giao', 1, DATEADD(day, -8, GETDATE())),
(7, N'Đã giao', N'Trả hàng-Hoàn tiền', 1, DATEADD(day, -7, GETDATE()));
-- Đơn 8
INSERT INTO LichSuTrangThaiDon(MaDonHang, TuTrangThai, DenTrangThai, MaQuanTriVien, ThoiDiemThayDoi) VALUES
(8, N'Đang xử lý', N'Đã xác nhận', 1, DATEADD(day, -4, GETDATE())),
(8, N'Đã xác nhận', N'Đang giao', 1, DATEADD(day, -3, GETDATE())),
(8, N'Đang giao', N'Đã giao', 1, GETDATE());
GO

-- 22. LienHe (Mới)
INSERT INTO LienHe(MaNguoiDung, ChuDe, NoiDung, TrangThai) VALUES
(3, N'Hỏi về chính sách đại lý', N'Xin chào, tôi muốn hỏi về chính sách chiết khấu khi nhập sỉ sản phẩm của Paula''s Choice.', N'Mới'),
(NULL, N'Báo lỗi website', N'Tôi không thể thêm sản phẩm vào giỏ hàng, nút bấm bị mờ.', N'Mới'),
(2, N'Khiếu nại vận chuyển', N'Đơn hàng #1 của tôi shipper giao hàng rất chậm và thái độ không tốt.', N'Đã xử lý');
GO

-- 23. PhienChat & TinNhanChat (Mới)
-- Phiên chat của User 4 (Đinh Thị Mai) với Admin (User 1)
DECLARE @SessionId NVARCHAR(100) = 'CHAT_USER_4';
INSERT INTO PhienChat(MaPhienChat, MaNguoiDung, TenKhach, EmailKhach, TinNhanDauTien, TinNhanCuoiCung, TrangThai, SoTinChuaDoc)
VALUES (@SessionId, 4, N'Đinh Thị Mai', 'dinh.mai@gmail.com', DATEADD(hour, -1, GETDATE()), GETDATE(), N'Đang mở', 1);

INSERT INTO TinNhanChat(MaPhienChat, MaNguoiDung, NoiDung, LoaiNguoiGui, ThoiGian, DaXem) VALUES
(@SessionId, 4, N'Shop ơi, tư vấn cho mình sản phẩm trị mụn với?', N'CUSTOMER', DATEADD(hour, -1, GETDATE()), 1),
(@SessionId, 1, N'Chào bạn, bạn có thể tham khảo Tinh Chất Eucerin Pro Acne (Mã 5) hoặc BHA 2% của Paula''s Choice (Mã 6) nhé!', N'ADMIN', DATEADD(minute, -58, GETDATE()), 1),
(@SessionId, 4, N'Da mình nhạy cảm thì dùng loại nào ok hơn ạ?', N'CUSTOMER', DATEADD(minute, -57, GETDATE()), 1),
(@SessionId, 1, N'Nếu da nhạy cảm, bạn nên bắt đầu với Eucerin trước nhé. Paula''s Choice BHA 2% khá mạnh đó ạ.', N'ADMIN', DATEADD(minute, -55, GETDATE()), 1),
(@SessionId, 4, N'Ok shop, mình cảm ơn.', N'CUSTOMER', GETDATE(), 0);
GO


PRINT N'✅ Đã import dữ liệu mẫu (đã viết lại và bổ sung) thành công!';
GO