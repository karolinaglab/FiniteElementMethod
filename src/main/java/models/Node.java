package models;

public class Node {

    private double x;
    private double y;
    private int id;
    private double temperature;

    // 0 - nie ma warunku brzegowego   1 - jest
    private boolean boundaryCondition;

    public Node(double x, double y, boolean boundaryCondition, double initialTemperature, int id) {
        this.x = x;
        this.y = y;
        this.temperature = initialTemperature;
        this.boundaryCondition = boundaryCondition;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getTemperature() {
        return temperature;
    }

    public boolean isBoundaryCondition() {
        return boundaryCondition;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }
}
