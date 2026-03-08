-- ========================================================
-- Host: localhost         Database: QuanLyCuaHangSach
-- ========================================================
CREATE DATABASE IF NOT EXISTS `QuanLyCuaHangSach` 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `QuanLyCuaHangSach`;

-- Tạm vô hiệu hóa foreign key để reset bảng an toàn
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `ChiTietTraNhaCungCap`, `PhieuTraNhaCungCap`, `ChiTietTraKhachHang`, 
                     `PhieuTraKhachHang`, `ChiTietPhieuNhap`, `PhieuNhap`, `ThanhToan`, `ChiTietHoaDon`, 
                     `HoaDon`, `KhuyenMai`, `LichSuKho`, `TacGia_Sach`, `Sach`, `NhaCungCap`, 
                     `NhaXuatBan`, `TacGia`, `TheLoai`, `KhachHang`, `NhanVien`, 
                     `TaiKhoan`, `PhanQuyen`;
SET FOREIGN_KEY_CHECKS = 1;

-- --------------------------------------------------------
-- PHÂN HỆ 1: HỆ THỐNG & PHÂN QUYỀN
-- --------------------------------------------------------
CREATE TABLE `PhanQuyen` (
    `MaQuyen` INT AUTO_INCREMENT PRIMARY KEY,
    `MaCode` VARCHAR(50) NOT NULL UNIQUE,
    `TenQuyen` VARCHAR(100) NOT NULL,
    `MoTa` TEXT
);

CREATE TABLE `TaiKhoan` (
    `MaTaiKhoan` INT AUTO_INCREMENT PRIMARY KEY,
    `MaQuyen` INT NOT NULL,
    `TenDangNhap` VARCHAR(50) NOT NULL UNIQUE,
    `MatKhau` VARCHAR(255) NOT NULL,
    `TrangThai` ENUM('HoatDong', 'Khoa') DEFAULT 'HoatDong',
    `NgayTao` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `UpdatedAt` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_taikhoan_quyen` (`MaQuyen`),
    CONSTRAINT `FK_TaiKhoan_PhanQuyen` FOREIGN KEY (`MaQuyen`) REFERENCES `PhanQuyen` (`MaQuyen`)
);

CREATE TABLE `NhanVien` (
    `MaNV` INT AUTO_INCREMENT PRIMARY KEY,
    `MaTaiKhoan` INT NOT NULL UNIQUE,
    `MaNVCode` VARCHAR(20) NOT NULL UNIQUE,
    `HoTen` VARCHAR(100) NOT NULL,
    `SoDienThoai` VARCHAR(15),
    CONSTRAINT `FK_NhanVien_TaiKhoan` FOREIGN KEY (`MaTaiKhoan`) REFERENCES `TaiKhoan` (`MaTaiKhoan`) ON DELETE CASCADE
);

-- --------------------------------------------------------
-- PHÂN HỆ 2: DANH MỤC & KHÁCH HÀNG 
-- --------------------------------------------------------
CREATE TABLE `KhachHang` (
    `MaKH` INT AUTO_INCREMENT PRIMARY KEY,
    `HoTen` VARCHAR(100) NOT NULL,
    `SoDienThoai` VARCHAR(15) NOT NULL UNIQUE,
    `DiaChi` TEXT,
    `DiemTichLuy` INT DEFAULT 0 CHECK (`DiemTichLuy` >= 0),
    `TrangThai` ENUM('HoatDong', 'NgungHoatDong') DEFAULT 'HoatDong',
    `NgayTao` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `UpdatedAt` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX `idx_khachhang_trangthai` (`TrangThai`)
);

CREATE TABLE `TheLoai` (
    `MaLoai` INT AUTO_INCREMENT PRIMARY KEY,
    `TenLoai` VARCHAR(100) NOT NULL UNIQUE,
    `TrangThai` ENUM('HoatDong', 'NgungHoatDong') DEFAULT 'HoatDong'
);

CREATE TABLE `TacGia` (
    `MaTacGia` INT AUTO_INCREMENT PRIMARY KEY,
    `TenTacGia` VARCHAR(100) NOT NULL UNIQUE,
    `TrangThai` ENUM('HoatDong', 'NgungHoatDong') DEFAULT 'HoatDong'
);

CREATE TABLE `NhaXuatBan` (
    `MaNXB` INT AUTO_INCREMENT PRIMARY KEY,
    `TenNXB` VARCHAR(100) NOT NULL UNIQUE,
    `TrangThai` ENUM('HoatDong', 'NgungHoatDong') DEFAULT 'HoatDong'
);

CREATE TABLE `NhaCungCap` (
    `MaNCC` INT AUTO_INCREMENT PRIMARY KEY,
    `TenNCC` VARCHAR(255) NOT NULL,
    `SoDienThoai` VARCHAR(15),
    `DiaChi` TEXT,
    `TrangThai` ENUM('HoatDong', 'NgungHoatDong') DEFAULT 'HoatDong'
);

-- --------------------------------------------------------
-- PHÂN HỆ 3: QUẢN LÝ KHO SÁCH
-- --------------------------------------------------------
CREATE TABLE `Sach` (
    `MaSach` INT AUTO_INCREMENT PRIMARY KEY,
    `MaSachCode` VARCHAR(20) NOT NULL UNIQUE,
    `TenSach` VARCHAR(255) NOT NULL,
    `MaLoai` INT,
    `MaNXB` INT,
    `HinhAnh` VARCHAR(255),
    `GiaGoc` DECIMAL(18,0) NOT NULL DEFAULT 0 CHECK (`GiaGoc` >= 0),
    `GiaBan` DECIMAL(18,0) NOT NULL DEFAULT 0 CHECK (`GiaBan` >= 0),
    `SoLuongTon` INT DEFAULT 0 CHECK (`SoLuongTon` >= 0),
    `SoLuongLoi` INT DEFAULT 0 CHECK (`SoLuongLoi` >= 0),
    `TrangThai` ENUM('DangBan', 'NgungBan') DEFAULT 'DangBan',
    `NgayTao` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `UpdatedAt` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT `CHK_Sach_TonLoi` CHECK (`SoLuongTon` >= `SoLuongLoi`),

    INDEX `idx_sach_maloai` (`MaLoai`),
    INDEX `idx_sach_manxb` (`MaNXB`),
    INDEX `idx_sach_ten` (`TenSach`),
    INDEX `idx_sach_trangthai` (`TrangThai`),

    CONSTRAINT `FK_Sach_TheLoai` FOREIGN KEY (`MaLoai`) REFERENCES `TheLoai` (`MaLoai`),
    CONSTRAINT `FK_Sach_NhaXuatBan` FOREIGN KEY (`MaNXB`) REFERENCES `NhaXuatBan` (`MaNXB`)
);

CREATE TABLE `TacGia_Sach` (
    `MaSach` INT NOT NULL,
    `MaTacGia` INT NOT NULL,
    PRIMARY KEY (`MaSach`, `MaTacGia`),

    CONSTRAINT `FK_TGS_Sach` FOREIGN KEY (`MaSach`) REFERENCES `Sach`(`MaSach`) ON DELETE CASCADE,
    CONSTRAINT `FK_TGS_TacGia` FOREIGN KEY (`MaTacGia`) REFERENCES `TacGia`(`MaTacGia`) ON DELETE CASCADE
);

CREATE TABLE `LichSuKho` (
    `MaLichSu` INT AUTO_INCREMENT PRIMARY KEY,
    `MaSach` INT NOT NULL,
    `LoaiGiaoDich` ENUM('NHAP_HANG', 'BAN_HANG', 'KHACH_TRA', 'TRA_NCC', 'KIEM_KE') NOT NULL,
    `LoaiChungTu` ENUM('HOADON','PHIEUNHAP','PTKH','PTNCC') NOT NULL, 
    `MaChungTu` INT NOT NULL, 
    `SoLuongThayDoi` INT NOT NULL, 
    `NgayGioTao` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `GhiChu` VARCHAR(255),
    
    INDEX `idx_lichsu_sach_ngay` (`MaSach`, `NgayGioTao`),
    INDEX `idx_lichsu_chungtu` (`LoaiChungTu`, `MaChungTu`),
    
    CONSTRAINT `FK_LichSuKho_Sach` FOREIGN KEY (`MaSach`) REFERENCES `Sach` (`MaSach`) ON DELETE CASCADE
);

-- --------------------------------------------------------
-- PHÂN HỆ 4: NGHIỆP VỤ BÁN HÀNG
-- --------------------------------------------------------
CREATE TABLE `KhuyenMai` (
    `MaKM` INT AUTO_INCREMENT PRIMARY KEY,
    `MaCode` VARCHAR(50) NOT NULL UNIQUE,
    `TenKM` VARCHAR(255) NOT NULL,
    `PhanTramGiam` DECIMAL(5,2) DEFAULT 0.00 CHECK (`PhanTramGiam` >= 0 AND `PhanTramGiam` <= 100),
    `SoTienGiam` DECIMAL(18,0) DEFAULT 0 CHECK (`SoTienGiam` >= 0),
    `DonHangToiThieu` DECIMAL(18,0) DEFAULT 0 CHECK (`DonHangToiThieu` >= 0),
    `NgayBatDau` DATE NOT NULL,
    `NgayKetThuc` DATE NOT NULL,
    `TrangThai` ENUM('HoatDong', 'HetHan') DEFAULT 'HoatDong',
    
    CONSTRAINT `CHK_KhuyenMai_Ngay` CHECK (`NgayKetThuc` >= `NgayBatDau`)
);

CREATE TABLE `HoaDon` (
    `MaHD` INT AUTO_INCREMENT PRIMARY KEY,
    `MaHDCode` VARCHAR(20) NOT NULL UNIQUE,
    `MaNV` INT,
    `MaKH` INT,
    `MaKM` INT,
    `TongTien` DECIMAL(18,0) NOT NULL DEFAULT 0 CHECK (`TongTien` >= 0),
    `TienGiam` DECIMAL(18,0) DEFAULT 0 CHECK (`TienGiam` >= 0),
    `ThanhTien` DECIMAL(18,0) GENERATED ALWAYS AS (`TongTien` - `TienGiam`) STORED, CHECK (`ThanhTien` >= 0),
    `LoaiHoaDon` ENUM('TaiQuay','GiaoHang') DEFAULT 'TaiQuay',
    `TrangThai` ENUM('ChoXuLy', 'HoanThanh', 'DaHuy') DEFAULT 'ChoXuLy',
    `NgayTao` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `UpdatedAt` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- NGĂN CHẶN THANH TIỀN ÂM
    CONSTRAINT `CHK_HoaDon_Tien` CHECK (`TongTien` >= `TienGiam`),
    
    INDEX `idx_hoadon_trangthai` (`TrangThai`),
    INDEX `idx_hoadon_ngay` (`NgayTao`),
    INDEX `idx_hoadon_nv_ngay` (`MaNV`, `NgayTao`),
    INDEX `idx_hoadon_kh` (`MaKH`),
    
    CONSTRAINT `FK_HoaDon_NhanVien` FOREIGN KEY (`MaNV`) REFERENCES `NhanVien` (`MaNV`),
    CONSTRAINT `FK_HoaDon_KhachHang` FOREIGN KEY (`MaKH`) REFERENCES `KhachHang` (`MaKH`),
    CONSTRAINT `FK_HoaDon_KhuyenMai` FOREIGN KEY (`MaKM`) REFERENCES `KhuyenMai` (`MaKM`)
);

CREATE TABLE `ChiTietHoaDon` (
    `MaCTHD` INT AUTO_INCREMENT PRIMARY KEY,
    `MaHD` INT NOT NULL,
    `MaSach` INT NOT NULL,
    `SoLuong` INT NOT NULL DEFAULT 1 CHECK (`SoLuong` > 0),
    `DonGia` DECIMAL(18,0) NOT NULL CHECK (`DonGia` >= 0),
    `ThanhTien` DECIMAL(18,0) GENERATED ALWAYS AS (`SoLuong` * `DonGia`) STORED, 
    
    INDEX `idx_cthd_hd_sach` (`MaHD`, `MaSach`),
    CONSTRAINT `uk_cthd_hd_sach` UNIQUE (`MaHD`, `MaSach`), 
    
    CONSTRAINT `FK_CTHD_HoaDon` FOREIGN KEY (`MaHD`) REFERENCES `HoaDon` (`MaHD`) ON DELETE CASCADE,
    CONSTRAINT `FK_CTHD_Sach` FOREIGN KEY (`MaSach`) REFERENCES `Sach` (`MaSach`)
);

-- BẢNG THANH TOÁN
CREATE TABLE `ThanhToan` (
    `MaThanhToan` INT AUTO_INCREMENT PRIMARY KEY,
    `MaHD` INT NOT NULL,
    `PhuongThuc` ENUM('TienMat', 'ChuyenKhoan', 'The', 'ViDienTu') NOT NULL,
    `SoTien` DECIMAL(18,0) NOT NULL CHECK (`SoTien` > 0),
    `TrangThai` ENUM('ThanhCong', 'ThatBai') DEFAULT 'ThanhCong',
    `NgayThanhToan` DATETIME DEFAULT CURRENT_TIMESTAMP,

    INDEX `idx_thanhtoan_hd` (`MaHD`),
    CONSTRAINT `FK_ThanhToan_HoaDon` FOREIGN KEY (`MaHD`) REFERENCES `HoaDon`(`MaHD`) ON DELETE CASCADE
);

-- --------------------------------------------------------
-- PHÂN HỆ 5: NGHIỆP VỤ NHẬP HÀNG & ĐỔI TRẢ
-- --------------------------------------------------------
CREATE TABLE `PhieuNhap` (
    `MaPN` INT AUTO_INCREMENT PRIMARY KEY,
    `MaPNCode` VARCHAR(20) NOT NULL UNIQUE,
    `MaNV` INT,
    `MaNCC` INT NOT NULL,
    `TongTien` DECIMAL(18,0) NOT NULL DEFAULT 0 CHECK (`TongTien` >= 0),
    `TrangThai` ENUM('ChoXuLy', 'HoanThanh', 'DaHuy') DEFAULT 'ChoXuLy',
    `NgayTao` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `UpdatedAt` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX `idx_phieunhap_nv` (`MaNV`),
    INDEX `idx_phieunhap_ncc` (`MaNCC`),
    
    CONSTRAINT `FK_PhieuNhap_NhanVien` FOREIGN KEY (`MaNV`) REFERENCES `NhanVien` (`MaNV`),
    CONSTRAINT `FK_PhieuNhap_NhaCungCap` FOREIGN KEY (`MaNCC`) REFERENCES `NhaCungCap` (`MaNCC`)
);

CREATE TABLE `ChiTietPhieuNhap` (
    `MaCTPN` INT AUTO_INCREMENT PRIMARY KEY,
    `MaPN` INT NOT NULL,
    `MaSach` INT NOT NULL,
    `SoLuong` INT NOT NULL DEFAULT 1 CHECK (`SoLuong` > 0),
    `GiaNhap` DECIMAL(18,0) NOT NULL CHECK (`GiaNhap` >= 0),
    `ThanhTien` DECIMAL(18,0) GENERATED ALWAYS AS (`SoLuong` * `GiaNhap`) STORED,
    
    INDEX `idx_ctpn_pn_sach` (`MaPN`, `MaSach`),
    CONSTRAINT `uk_ctpn_pn_sach` UNIQUE (`MaPN`, `MaSach`), 
    
    CONSTRAINT `FK_CTPN_PhieuNhap` FOREIGN KEY (`MaPN`) REFERENCES `PhieuNhap` (`MaPN`) ON DELETE CASCADE,
    CONSTRAINT `FK_CTPN_Sach` FOREIGN KEY (`MaSach`) REFERENCES `Sach` (`MaSach`)
);

CREATE TABLE `PhieuTraKhachHang` (
    `MaPTK` INT AUTO_INCREMENT PRIMARY KEY,
    `MaPTKCode` VARCHAR(20) NOT NULL UNIQUE,
    `MaHD` INT NOT NULL, 
    `MaNV` INT NOT NULL, 
    `LyDo` VARCHAR(255),
    `TienHoan` DECIMAL(18,0) DEFAULT 0 CHECK (`TienHoan` >= 0),
    `NgayTao` DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX `idx_ptk_hd` (`MaHD`),
    CONSTRAINT `FK_PTKhachHang_HoaDon` FOREIGN KEY (`MaHD`) REFERENCES `HoaDon` (`MaHD`),
    CONSTRAINT `FK_PTKhachHang_NhanVien` FOREIGN KEY (`MaNV`) REFERENCES `NhanVien` (`MaNV`)
);

CREATE TABLE `ChiTietTraKhachHang` (
    `MaCTPTK` INT AUTO_INCREMENT PRIMARY KEY,
    `MaPTK` INT NOT NULL,
    `MaSach` INT NOT NULL,
    `SoLuong` INT NOT NULL DEFAULT 1 CHECK (`SoLuong` > 0),
    `TinhTrangSach` VARCHAR(100) DEFAULT 'Lỗi NSX',
    
    INDEX `idx_ctptk_ptk_sach` (`MaPTK`, `MaSach`),
    CONSTRAINT `uk_ctptk_ptk_sach` UNIQUE (`MaPTK`, `MaSach`), 
    
    CONSTRAINT `FK_CTPTK_PhieuTra` FOREIGN KEY (`MaPTK`) REFERENCES `PhieuTraKhachHang` (`MaPTK`) ON DELETE CASCADE,
    CONSTRAINT `FK_CTPTK_Sach` FOREIGN KEY (`MaSach`) REFERENCES `Sach` (`MaSach`)
);

CREATE TABLE `PhieuTraNhaCungCap` (
    `MaPTN` INT AUTO_INCREMENT PRIMARY KEY,
    `MaPTNCode` VARCHAR(20) NOT NULL UNIQUE,
    `MaNV` INT NOT NULL,
    `MaNCC` INT NOT NULL,
    `LyDo` VARCHAR(255) NOT NULL,
    `TongTienHoan` DECIMAL(18,0) DEFAULT 0 CHECK (`TongTienHoan` >= 0),
    `NgayTao` DATETIME DEFAULT CURRENT_TIMESTAMP,
    
    INDEX `idx_ptn_ncc` (`MaNCC`),
    CONSTRAINT `FK_PTNCC_NhanVien` FOREIGN KEY (`MaNV`) REFERENCES `NhanVien` (`MaNV`),
    CONSTRAINT `FK_PTNCC_NhaCungCap` FOREIGN KEY (`MaNCC`) REFERENCES `NhaCungCap` (`MaNCC`)
);

CREATE TABLE `ChiTietTraNhaCungCap` (
    `MaCTPTN` INT AUTO_INCREMENT PRIMARY KEY,
    `MaPTN` INT NOT NULL,
    `MaSach` INT NOT NULL,
    `SoLuong` INT NOT NULL DEFAULT 1 CHECK (`SoLuong` > 0),
    
    INDEX `idx_ctptn_ptn_sach` (`MaPTN`, `MaSach`),
    CONSTRAINT `uk_ctptn_ptn_sach` UNIQUE (`MaPTN`, `MaSach`), 
    
    CONSTRAINT `FK_CTPTN_PhieuTra` FOREIGN KEY (`MaPTN`) REFERENCES `PhieuTraNhaCungCap` (`MaPTN`) ON DELETE CASCADE,
    CONSTRAINT `FK_CTPTN_Sach` FOREIGN KEY (`MaSach`) REFERENCES `Sach` (`MaSach`)
);