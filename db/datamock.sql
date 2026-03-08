USE quanlycuahangsach;

INSERT INTO `PhanQuyen` (`MaCode`, `TenQuyen`, `MoTa`) VALUES 
('ADMIN', 'Quản trị viên', 'Toàn quyền hệ thống'),
('NHANVIEN_BANHANG', 'Nhân viên bán hàng', 'Lập hóa đơn, quản lý khách hàng'),
('NHANVIEN_KHO', 'Nhân viên kho', 'Nhập hàng, kiểm kho, quản lý sách');

INSERT INTO `TaiKhoan` (`MaQuyen`, `TenDangNhap`, `MatKhau`, `TrangThai`) VALUES 
(1, 'admin', 'password123', 'HoatDong'),
(2, 'nv_banhang', 'password123', 'HoatDong'),
(3, 'nv_kho', 'password123', 'HoatDong');

INSERT INTO `NhanVien` (`MaTaiKhoan`, `HoTen`, `SoDienThoai`) VALUES 
(1, 'Nguyễn Admin', '0901111222'),
(2, 'Trần Thị Bán Hàng', '0902222333'),
(3, 'Lê Văn Kho', '0903333444');

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

-- Liên kết Tác giả - Sách
INSERT INTO `TacGia_Sach` (`MaSach`, `MaTacGia`) VALUES (1, 1), (2, 2), (3, 3), (4, 4);

INSERT INTO `KhachHang` (`HoTen`, `SoDienThoai`, `DiaChi`, `DiemTichLuy`) VALUES 
('Nguyễn Văn A', '0911222333', '123 Lê Lợi, TP.HCM', 100),
('Trần Thị B', '0944555666', '456 Nguyễn Huệ, TP.HCM', 50),
('Khách vãng lai', '0000000000', NULL, 0);


INSERT INTO `KhuyenMai` (`MaCode`, `TenKM`, `PhanTramGiam`, `SoTienGiam`, `DonHangToiThieu`, `NgayBatDau`, `NgayKetThuc`) VALUES 
('GIAM10', 'Giảm giá 10% khai trương', 10.00, 0, 100000, '2024-01-01', '2026-12-31'),
('CHO_MOI', 'Tặng 20k cho khách mới', 0.00, 20000, 50000, '2024-01-01', '2026-12-31');

-- Tạo 1 hóa đơn mẫu (Lưu ý: Không insert cột ThanhTien vì đây là cột GENERATED ALWAYS AS)
INSERT INTO `HoaDon` (`MaNV`, `MaKH`, `MaKM`, `TongTien`, `TienGiam`, `LoaiHoaDon`, `TrangThai`) VALUES 
(2, 1, 1, 180000, 18000, 'TaiQuay', 'HoanThanh');

-- Chi tiết hóa đơn (2 quyển sách - Bỏ qua cột ThanhTien)
INSERT INTO `ChiTietHoaDon` (`MaHD`, `MaSach`, `SoLuong`, `DonGia`) VALUES 
(1, 1, 1, 85000),
(1, 2, 1, 95000);

-- Thanh toán cho hóa đơn trên
INSERT INTO `ThanhToan` (`MaHD`, `PhuongThuc`, `SoTien`) VALUES (1, 'TienMat', 162000);


-- Phiếu nhập hàng (Bỏ qua cột ThanhTien trong ChiTiet)
INSERT INTO `PhieuNhap` (`MaNV`, `MaNCC`, `TongTien`, `TrangThai`) VALUES 
(3, 1, 5000000, 'HoanThanh');

INSERT INTO `ChiTietPhieuNhap` (`MaPN`, `MaSach`, `SoLuong`, `GiaNhap`) VALUES 
(1, 1, 100, 50000);

-- Phiếu trả hàng từ khách
INSERT INTO `PhieuTraKhachHang` (`MaHD`, `MaNV`, `LyDo`, `TienHoan`) VALUES 
(1, 2, 'Sách bị rách trang', 85000);

INSERT INTO `ChiTietTraKhachHang` (`MaPTK`, `MaSach`, `SoLuong`, `TinhTrangSach`) VALUES 
(1, 1, 1, 'Rách trang 50');

-- Lịch sử kho (Ghi nhận một giao dịch mẫu)
INSERT INTO `LichSuKho` (`MaSach`, `LoaiGiaoDich`, `LoaiChungTu`, `MaChungTu`, `SoLuongThayDoi`, `GhiChu`) VALUES 
(1, 'BAN_HANG', 'HOADON', 1, -1, 'Bán cho Nguyễn Văn A'),
(1, 'NHAP_HANG', 'PHIEUNHAP', 1, 100, 'Nhập hàng đợt 1');