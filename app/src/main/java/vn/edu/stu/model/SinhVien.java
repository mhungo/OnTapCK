package vn.edu.stu.model;

public class SinhVien {
    private int ma;
    private String ten;
    private int malop;

    public SinhVien(int ma, String ten, int malop) {
        this.ma = ma;
        this.ten = ten;
        this.malop = malop;
    }

    public SinhVien() {
    }

    public int getMa() {
        return ma;
    }

    public void setMa(int ma) {
        this.ma = ma;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public int getMalop() {
        return malop;
    }

    public void setMalop(int malop) {
        this.malop = malop;
    }

    @Override
    public String toString() {
        return "Ma: " + ma + "\n"
                + "Ten: " + ten + "\n"
                + "Ma lop: " + malop;
    }
}
