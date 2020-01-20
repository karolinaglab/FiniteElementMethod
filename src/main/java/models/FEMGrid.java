package models;

import java.util.ArrayList;
import java.util.List;

public class FEMGrid {

    private List<Node> nodes;
    private List<Element> elements;

    private double[][] globalH;
    private double[][] globalC;
    private double[] globalP;

    public FEMGrid() {

        this.nodes = new ArrayList<>();
        this.elements = new ArrayList<>();
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void setElements(List<Element> elements) {
        this.elements = elements;
    }

    public double[][] getGlobalH() {
        return globalH;
    }

    public void setGlobalH(double[][] globalH) {
        this.globalH = globalH;
    }

    public double[][] getGlobalC() {
        return globalC;
    }

    public void setGlobalC(double[][] globalC) {
        this.globalC = globalC;
    }

    public double[] getGlobalP() {
        return globalP;
    }

    public void setGlobalP(double[] globalP) {
        this.globalP = globalP;
    }

    public void printGrid() {
        nodes.forEach(node -> {
            System.out.println("Node " + (nodes.indexOf(node) + 1) + ": (" + node.getX() + "," + node.getY() + ") boundary condition = " + node.isBoundaryCondition() + ", " + node.getId());
        });
    }

    public void printElementsDetails() {
        elements.forEach(element -> {
            double[] ID = element.getID();
            List<Node> nodes = element.getNodes();
            System.out.println("Element " + (elements.indexOf(element) + 1) + " ID: [" + ID[0] + ", " + ID[1] + ", " + ID[2] + ", " + ID[3] + "]");

            //PRINT ELEMENT NODES LIST
            System.out.println("ELEMENT NODES " + (elements.indexOf(element) + 1));
            for (Node node : nodes) {
                System.out.println("ID: " + node.getId() + ", X: " + node.getX() + ", Y: " + node.getY() + ", Node temperature: " + node.getTemperature());
            }
        });
    }
}
