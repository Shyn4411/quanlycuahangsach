package dto;

public class TacGia_SachDTO {
    private int maSach;
    private int maTacGia;

    public TacGia_SachDTO() {}

    public TacGia_SachDTO(int maSach, int maTacGia) {
        this.maSach = maSach;
        this.maTacGia = maTacGia;
    }

    public int getMaSach() { return maSach; }
    public void setMaSach(int maSach) { this.maSach = maSach; }

    public int getMaTacGia() { return maTacGia; }
    public void setMaTacGia(int maTacGia) { this.maTacGia = maTacGia; }

    @Override
    public String toString() {
        return "TacGia_SachDTO{" +
                "maSach=" + maSach +
                ", maTacGia=" + maTacGia +
                '}';
    }
}
