package models;

public class Node {

    private double x;
    private double y;
    private int id;
    private double t;

    // 0 - nie ma warunku brzegowego   1 - jest
    private boolean BC;

    public Node(double x, double y, boolean BC, int id) {
        this.x = x;
        this.y = y;
        this.t = 0;
        this.BC = BC;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getT() {
        return t;
    }

    public void setT(double t) {
        this.t = t;
    }

    public boolean isBC() {
        return BC;
    }

    public void setBC(boolean BC) {
        this.BC = BC;
    }
}
