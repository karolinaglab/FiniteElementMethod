package models;

import java.util.ArrayList;
import java.util.List;

public class Element {

    private double[] ID = new double[4];

    private List<Node> nodes = new ArrayList<>();

    private double[][] H = new double[4][4];


    public Element(double[] ID) {
        this.ID = ID;
    }

    public Element(double id1, double id2, double id3, double id4) {
        this.ID[0] = id1;
        this.ID[1] = id2;
        this.ID[2] = id3;
        this.ID[3] = id4;
    }

    public double[] getID() {
        return ID;
    }

    public void setID(double[] ID) {
        this.ID = ID;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public double[][] getH() {
        return H;
    }

    public void setH(double[][] h) {
        H = h;
    }
}
