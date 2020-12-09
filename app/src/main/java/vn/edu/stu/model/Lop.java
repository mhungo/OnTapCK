package vn.edu.stu.model;

public class Lop {
    private int malop;
    private String tenop;


    public Lop(int malop, String tenop) {
        this.malop = malop;
        this.tenop = tenop;
    }

    public Lop() {
    }

    public int getMalop() {
        return malop;
    }

    public void setMalop(int malop) {
        this.malop = malop;
    }

    public String getTenop() {
        return tenop;
    }

    public void setTenop(String tenop) {
        this.tenop = tenop;
    }

    @Override
    public String toString() {
        return tenop;
    }
}
