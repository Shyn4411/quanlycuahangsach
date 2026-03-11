USE quanlycuahangsach;

INSERT INTO `PhanQuyen` (`MaCode`, `TenQuyen`, `MoTa`) VALUES 
('ADMIN', 'Quản trị viên', 'Toàn quyền hệ thống'),
('NHANVIEN_BANHANG', 'Nhân viên bán hàng', 'Lập hóa đơn, quản lý khách hàng'),
('NHANVIEN_KHO', 'Nhân viên kho', 'Nhập hàng, kiểm kho, quản lý sách'),
('KHACH_HANG', 'Khách hàng', 'Quyền dành cho khách hàng mua sắm online');

INSERT INTO `TaiKhoan` (`MaQuyen`, `TenDangNhap`, `MatKhau`, `TrangThai`) VALUES 
(1, 'admin', '123456', 'HOAT_DONG'),
(2, 'nhanvienbanhang', '123456', 'HOAT_DONG'),
(3, 'nhanvienkho', '123456', 'HOAT_DONG'),
(4, 'khachhangtest', '123456', 'HOAT_DONG'),
(4, 'khachhangtest2', '123456', 'KHOA');

INSERT INTO `CauHinh` VALUES 
('TEN_SHOP', 'BOOKSTORE', 'Tên hiển thị trên hóa đơn'),
('DIA_CHI', '5 An Dương Vương Quận 5 TPHCM', 'Địa chỉ cửa hàng'),
('HOTLINE', '0987654321', 'Số điện thoại hỗ trợ'),
('VAT', '10', 'Thuế giá trị gia tăng (%)');


INSERT INTO `NhanVien` (`MaTaiKhoan`, `HoTen`, `SoDienThoai`) VALUES 
(1, 'ADMIN', '0908070605'),
(2, 'Nguyễn Thị Hương', '0102030405'),
(3, 'Nguyễn Thành Dương', '0504030201');

INSERT INTO `KhachHang` (`MaTaiKhoan`, `HoTen`, `SoDienThoai`, `DiaChi`, `DiemTichLuy`, `TrangThai`) VALUES 
(4, 'Khúc Thị Hương', '0903090309', '5 An Dương Vương Quận 5 TPHCM', 500, 'HOAT_DONG'),
(5, 'Nguyễn Thị Lan Anh', '0306030603', '7 An Dương Vương Quận 5 TPHCM', 0, 'NGUNG_HOAT_DONG');


INSERT INTO `TheLoai` (`TenLoai`) VALUES ('Văn học'), ('Kinh tế'), ('Kỹ năng sống'), ('Thiếu nhi');

INSERT INTO `TacGia` (`TenTacGia`) VALUES ('Nguyễn Nhật Ánh'), ('Dale Carnegie'), ('Tony Buổi Sáng'), ('Paulo Coelho');

INSERT INTO `NhaXuatBan` (`TenNXB`) VALUES ('NXB Trẻ'), ('NXB Kim Đồng'), ('NXB Tổng hợp TP.HCM'), ('NXB Hội Nhà Văn');

INSERT INTO `NhaCungCap` (`TenNCC`, `SoDienThoai`, `DiaChi`) VALUES 
('Công ty Sách Fahasa', '0281234567', 'Quận 1, TP.HCM'),
('Nhà sách Phương Nam', '0287654321', 'Quận 3, TP.HCM');

INSERT INTO `Sach` (`TenSach`, `MaLoai`, `MaNXB`, `GiaGoc`, `GiaBan`, `SoLuongTon`) VALUES 
('Cho tôi xin một vé đi tuổi thơ', 1, 1, 50000, 85000, 100),
('Đắc Nhân Tâm', 3, 3, 60000, 95000, 50),
('Trên đường băng', 3, 1, 45000, 80000, 120),
('Nhà Giả Kim', 1, 4, 55000, 89000, 80);

INSERT INTO `TacGia_Sach` (`MaSach`, `MaTacGia`) VALUES (1, 1), (2, 2), (3, 3), (4, 4);

INSERT INTO `KhuyenMai` (`MaCode`, `TenKM`, `PhanTramGiam`, `SoTienGiam`, `DonHangToiThieu`, `NgayBatDau`, `NgayKetThuc`) VALUES 
('GIAM10', 'Giảm giá 10% khai trương', 10.00, 0, 100000, '2024-01-01', '2026-12-31'),
('CHO_MOI', 'Tặng 20k cho khách mới', 0.00, 20000, 50000, '2024-01-01', '2026-12-31');


INSERT INTO `HoaDon` (`MaNV`, `MaKH`, `MaKM`, `TongTien`, `TienGiam`, `LoaiHoaDon`, `TrangThai`) VALUES 
(2, 1, 1, 180000, 18000, 'TAI_QUAY', 'HOAN_THANH');


INSERT INTO `ChiTietHoaDon` (`MaHD`, `MaSach`, `SoLuong`, `DonGia`) VALUES 
(1, 1, 1, 85000),
(1, 2, 1, 95000);

INSERT INTO `ThanhToan` (`MaHD`, `PhuongThuc`, `SoTien`) VALUES (1, 'TIEN_MAT', 162000);

INSERT INTO `PhieuNhap` (`MaNV`, `MaNCC`, `TongTien`, `TrangThai`) VALUES 
(3, 1, 5000000, 'HOAN_THANH');

INSERT INTO `ChiTietPhieuNhap` (`MaPN`, `MaSach`, `SoLuong`, `GiaNhap`) VALUES 
(1, 1, 100, 50000);

INSERT INTO `PhieuTraKhachHang` (`MaHD`, `MaNV`, `LyDo`, `TienHoan`) VALUES 
(1, 2, 'Sách bị rách trang', 85000);

INSERT INTO `ChiTietTraKhachHang` (`MaPTK`, `MaSach`, `SoLuong`, `TinhTrangSach`) VALUES 
(1, 1, 1, 'Rách trang 50');

INSERT INTO `LichSuKho` (`MaSach`, `LoaiGiaoDich`, `LoaiChungTu`, `MaChungTu`, `SoLuongThayDoi`, `GhiChu`) VALUES 
(1, 'BAN_HANG', 'HOA_DON', 1, -1, 'Bán cho Khúc Thị Hương'),
(1, 'NHAP_HANG', 'PHIEU_NHAP', 1, 100, 'Nhập hàng đợt 1');