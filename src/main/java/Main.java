import functions.MatrixCalculations;
import functions.GaussInterpolation;
import functions.NodeAndElementGenerator;
import models.*;
import org.la4j.LinearAlgebra;
import org.la4j.Matrix;
import org.la4j.Vector;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {

        Scanner scan = new Scanner(System.in);
        System.out.println("Which test case do you choose?");
        int choice = scan.nextInt();

        String[] data = new String[4];
        FEMGrid femGrid = new FEMGrid();
        GlobalData globalData = new GlobalData(choice);

        globalData.printGlobalData();

        // FEM Grid
        List<Node> nodes = NodeAndElementGenerator.generateNodes(globalData.getHeight(), globalData.getWidth(), globalData.getnW(), globalData.getnH(), globalData.getInitialTemperature());
        femGrid.setNodes(nodes);
        femGrid.printGrid();

        List<Element> elements = NodeAndElementGenerator.generateElements(globalData.getnH(), globalData.getNumberOfElements(), nodes);
        femGrid.setElements(elements);
        femGrid.printElementsDetails();


        // GaussInterpolation
        List<IntegrationPoint> integrationPoints = GaussInterpolation.setIntegrationPoints(globalData.getpC());

        double[][] ksiMatrix = GaussInterpolation.countKsiDerivatives(integrationPoints);
        double[][] etaMatrix = GaussInterpolation.countEtaDerivatives(integrationPoints);
        double[][] shapeFunctions = GaussInterpolation.shapeFunctionMatrix(integrationPoints);


        System.out.println("KSI MATRIX");
        MatrixCalculations.printMatrix(ksiMatrix, integrationPoints.size(), 4);
        System.out.println("ETA MATRIX");
        MatrixCalculations.printMatrix(etaMatrix, integrationPoints.size(), 4);
        System.out.println("SHAPE FUNCTIONS MATRIX");
        MatrixCalculations.printMatrix(shapeFunctions, integrationPoints.size(), 4);


        // H MATRIX

        for (int i = 0; i < elements.size(); i++) {
            System.out.println("ELEMENT " + (i + 1) + " H matrix");
            elements.get(i).setH(MatrixCalculations.countMatrixH(elements.get(i), ksiMatrix, etaMatrix, globalData.getConductivity()));
            MatrixCalculations.printMatrix(elements.get(i).getH(), 4, 4);
            System.out.println();
        }


        // C MATRIX

        for (int i = 0; i < elements.size(); i++) {
            System.out.println("ELEMENT " + (i + 1) + " C matrix");
            elements.get(i).setC(MatrixCalculations.countMatrixC(elements.get(i), ksiMatrix, etaMatrix, shapeFunctions, globalData.getDensity(), globalData.getSpecificHeat()));
            MatrixCalculations.printMatrix(elements.get(i).getC(), 4, 4);
            System.out.println();
        }


        // HBC MATRIX

        for (int i = 0; i < elements.size(); i++) {
            System.out.println("ELEMENT " + (i + 1) + " HBC matrix");
            elements.get(i).setHBC(MatrixCalculations.countMatrixHBC(elements.get(i), globalData.getAlpha()));
            MatrixCalculations.printMatrix(elements.get(i).getHBC(), 4, 4);
            System.out.println();
        }


        // VECTOR P

        for (int i = 0; i < elements.size(); i++) {
            System.out.println("ELEMENT " + (i + 1) + " Vector P");
            elements.get(i).setP(MatrixCalculations.countVectorP(elements.get(i), globalData.getAlpha(), globalData.getAmbientTemperature()));
            System.out.println(Arrays.toString(elements.get(i).getP()));
            System.out.println();
        }

        // MATRIX AGGREGATION

        int size = (int) (globalData.getNumberOfNodes());
        double[][] globalH = new double[size][size];
        double[][] globalC = new double[size][size];
        double[] globalP = new double[size];
        for (int i = 0; i < size; i++) {
            globalP[i] = 0;
            for (int j = 0; j < size; j++) {
                globalH[i][j] = 0;
                globalC[i][j] = 0;
            }
        }

        MatrixCalculations.matrixAggregation(elements, globalH, globalC, globalP);
        femGrid.setGlobalH(globalH);
        femGrid.setGlobalC(globalC);
        femGrid.setGlobalP(globalP);
        System.out.println("GLOBAL H: ");
       // Calculations.printMatrix(globalH, size, size);
        System.out.println("GLOBAL C: ");
       // Calculations.printMatrix(globalC, size, size);
        System.out.println("GLOBAL P: ");
       // System.out.println(Arrays.toString(globalP));

        double[][] finalH = new double[size][size];
        double[][] finalC = new double[size][size];
        double simulationStepTime = globalData.getSimulationStepTime();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                finalH[i][j] = globalH[i][j] + (globalC[i][j] / simulationStepTime);
                finalC[i][j] = globalC[i][j] / simulationStepTime;
            }
        }

        double simulationTime = globalData.getSimulationTime();
        int numberOfSteps = (int) (simulationTime / simulationStepTime);

        Matrix H = Matrix.from2DArray(finalH);
        H = H.withInverter(LinearAlgebra.GAUSS_JORDAN).inverse();
        for (int i = 0; i < numberOfSteps; i++) {
            double[] initialTemperatures = new double[nodes.size()];
            for (int j = 0; j < nodes.size(); j++) {
                initialTemperatures[j] = femGrid.getNodes().get(j).getTemperature();
            }


            Matrix C = Matrix.from2DArray(finalC);
            Vector temperatures = Vector.fromArray(initialTemperatures);
            Vector P = Vector.fromArray(globalP);

            Vector C_t = C.multiply(temperatures);
            C_t = C_t.subtract(P);

            Vector nextTemperatures = C_t.multiply(H);

            for (int j = 0; j < globalData.getNumberOfNodes(); j++) {
                femGrid.getNodes().get(j).setTemperature(nextTemperatures.get(j));
            }
            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            System.out.println("Time [s] = " + (simulationStepTime * (i+1)) + ": min temp = " + decimalFormat.format(nextTemperatures.min()) + " max temp = " + decimalFormat.format(nextTemperatures.max()));
        }
    }
}

