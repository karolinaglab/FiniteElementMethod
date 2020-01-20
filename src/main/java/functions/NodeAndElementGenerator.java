package functions;

import models.Element;
import models.Node;

import java.util.ArrayList;
import java.util.List;


public class NodeAndElementGenerator {

    public static List<Node> generateNodes(double height, double width, double nW, double nH, double initialTemperature) {

        double deltaX = width / (nW - 1);
        double deltaY = height / (nH - 1);
        double x;
        double y;
        boolean boundaryCondition;
        List<Node> nodesList = new ArrayList<>();

        int id = 1;
        for (double i = 0; i < nW; i++) {
            for (double j = 0; j < nH; j++) {
                x = i * deltaX;
                y = j * deltaY;

                boundaryCondition = x == 0 || y == 0 || x == width || y == height;
                Node nodeToAdd = new Node(x,y,boundaryCondition, initialTemperature, id);
                nodesList.add(nodeToAdd);
                id++;
            }
        }

        return nodesList;
    }


    public static List<Element> generateElements(double nH, double nE, List<Node> nodes) {

        List<Element> elementsList = new ArrayList<>();

        for (int i = 1; elementsList.size() < nE; i++) {

            if (i%nH == 0) {
                i++;
            }
            int id1 = i;
            double id2 = id1 + nH;
            double id3 = id2 + 1;
            int id4 = id1 + 1;

            Element elementToAdd = new Element(id1,id2,id3,id4);

            //Add list of nodes to element
            for (int m = 0; m < 4; m++) {
                for (Node node : nodes) {
                    if (node.getId() == elementToAdd.getID()[m]) {
                        elementToAdd.getNodes().add(node);
                    }
                }
            }

            elementsList.add(elementToAdd);
        }

        return elementsList;
    }

}



