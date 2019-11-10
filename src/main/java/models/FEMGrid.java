package models;

import java.util.ArrayList;
import java.util.List;

public class FEMGrid {

    private List<Node> nodes;
    private List<Element> elements;

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

    public void printGrid() {
        nodes.forEach(node -> {
            System.out.println("Node " + (nodes.indexOf(node) + 1) + ": (" + node.getX() + "," + node.getY() + ") BC = " + node.isBC());
        });
    }

    public void printElementsDetails() {
        elements.forEach(element -> {
            double[] ID = element.getID();
            System.out.println("Element " + (elements.indexOf(element) + 1) + " ID: [" + ID[0] + ", " + ID[1] + ", " + ID[2] + ", " + ID[3] + "]");
        });
    }
}
