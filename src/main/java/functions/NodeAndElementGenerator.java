package functions;

import models.Element;
import models.Node;

import java.util.ArrayList;
import java.util.List;


public class NodeAndElementGenerator {

    public static List<Node> generateNodes(double Height, double Width, double nW, double nH) {

        double deltaX = Width / (nW - 1);
        double deltaY = Height / (nH - 1);
        double x;
        double y;
        boolean BC;
        List<Node> nodesList = new ArrayList<>();

        for (double i = 0; i < nW; i++) {
            for (double j = 0; j < nH; j++) {
                x = i * deltaX;
                y = j * deltaY;

                if (x == 0 || y == 0 || x == Width || y == Height) {
                    BC = true;
                } else {
                    BC = false;
                }
                Node nodeToAdd = new Node(x,y,BC);
                nodesList.add(nodeToAdd);
            }
        }

        return nodesList;
    }

    public static List<Element> generateElements(double nH, double nE) {

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
            elementsList.add(elementToAdd);
        }

        return elementsList;
    }

}



