import functions.GaussInterpolation;
import functions.NodeAndElementGenerator;
import models.FEMGrid;
import models.GlobalData;
import models.InterpolationPoint;

import java.util.List;


public class Main {
    public static void main(String[] args) {

        String[] data = new String[4];
        FEMGrid femGrid = new FEMGrid();
        GlobalData globalData = new GlobalData();

        globalData.printGlobalData();

        // FEM Grid
        femGrid.setNodes(NodeAndElementGenerator.generateNodes(globalData.getHeight(),globalData.getWidth(),globalData.getnW(),globalData.getnH()));
        femGrid.printGrid();

        femGrid.setElements(NodeAndElementGenerator.generateElements(globalData.getnH(), globalData.getnE()));
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

    }
}

