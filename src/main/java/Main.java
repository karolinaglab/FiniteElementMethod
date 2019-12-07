import functions.GaussInterpolation;
import functions.NodeAndElementGenerator;
import models.*;

import java.util.ArrayList;
import java.util.List;


public class Main {
    public static void main(String[] args) {

        String[] data = new String[4];
        FEMGrid femGrid = new FEMGrid();
        GlobalData globalData = new GlobalData();

        globalData.printGlobalData();

        // FEM Grid
        List<Node> nodes = NodeAndElementGenerator.generateNodes(globalData.getHeight(),globalData.getWidth(),globalData.getnW(),globalData.getnH());
        femGrid.setNodes(nodes);
        femGrid.printGrid();

        List<Element> elements = NodeAndElementGenerator.generateElements(globalData.getnH(), globalData.getnE(), nodes);
        femGrid.setElements(elements);
        femGrid.printElementsDetails();


        // GaussInterpolation
        List<InterpolationPoint> interpolationPoints = GaussInterpolation.setInterpolationPoints(globalData.getpC());

        double[][] ksiMatrix = GaussInterpolation.countKsiDerivatives(interpolationPoints);
        double[][] etaMatrix = GaussInterpolation.countEtaDerivatives(interpolationPoints);
        double[][] shapeFunctions = GaussInterpolation.shapeFunctionMatrix(interpolationPoints);


        System.out.println("KSI MATRIX");
        GaussInterpolation.printMatrix(ksiMatrix, interpolationPoints.size(), 4);
        System.out.println("ETA MATRIX");
        GaussInterpolation.printMatrix(etaMatrix, interpolationPoints.size(), 4);
        System.out.println("SHAPE FUNCTIONS MATRIX");
        GaussInterpolation.printMatrix(shapeFunctions, interpolationPoints.size(), 4);


       // H MATRIX

        for (int i = 0; i < elements.size(); i++) {
            System.out.println("ELEMENT " + (i+1) + " H matrix");
            elements.get(i).setH(GaussInterpolation.countMatrixH(elements.get(i), ksiMatrix, etaMatrix));
            GaussInterpolation.printMatrix(elements.get(i).getH(), 4, 4);
            System.out.println();
        }
    }
}

