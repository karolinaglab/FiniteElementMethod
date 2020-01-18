import functions.Calculations;
import functions.NodeAndElementGenerator;
import models.*;
import org.la4j.LinearAlgebra;
import org.la4j.Matrix;
import org.la4j.Vector;

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
        List<IntegrationPoint> integrationPoints = Calculations.setIntegrationPoints(globalData.getpC());

        double[][] ksiMatrix = Calculations.countKsiDerivatives(integrationPoints);
        double[][] etaMatrix = Calculations.countEtaDerivatives(integrationPoints);
        double[][] shapeFunctions = Calculations.shapeFunctionMatrix(integrationPoints);


        System.out.println("KSI MATRIX");
        Calculations.printMatrix(ksiMatrix, integrationPoints.size(), 4);
        System.out.println("ETA MATRIX");
        Calculations.printMatrix(etaMatrix, integrationPoints.size(), 4);
        System.out.println("SHAPE FUNCTIONS MATRIX");
        Calculations.printMatrix(shapeFunctions, integrationPoints.size(), 4);


        // H MATRIX

        for (int i = 0; i < elements.size(); i++) {
            System.out.println("ELEMENT " + (i + 1) + " H matrix");
            elements.get(i).setH(Calculations.countMatrixH(elements.get(i), ksiMatrix, etaMatrix, globalData.getConductivity()));
            Calculations.printMatrix(elements.get(i).getH(), 4, 4);
            System.out.println();
        }


        // C MATRIX

        for (int i = 0; i < elements.size(); i++) {
            System.out.println("ELEMENT " + (i + 1) + " C matrix");
            elements.get(i).setC(Calculations.countMatrixC(elements.get(i), ksiMatrix, etaMatrix, shapeFunctions, globalData.getDensity(), globalData.getSpecificHeat()));
            Calculations.printMatrix(elements.get(i).getC(), 4, 4);
            System.out.println();
        }


        // HBC MATRIX

        for (int i = 0; i < elements.size(); i++) {
            System.out.println("ELEMENT " + (i + 1) + " HBC matrix");
            elements.get(i).setHBC(Calculations.countMatrixHBC(elements.get(i), globalData.getAlpha()));
            Calculations.printMatrix(elements.get(i).getHBC(), 4, 4);
            System.out.println();
        }


        // VECTOR P

        for (int i = 0; i < elements.size(); i++) {
            System.out.println("ELEMENT " + (i + 1) + " Vector P");
            elements.get(i).setP(Calculations.countVectorP(elements.get(i), globalData.getAlpha(), globalData.getAmbientTemperature()));
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

        Calculations.matrixAggregation(elements, globalH, globalC, globalP);
        System.out.println("GLOBAL H: ");
        Calculations.printMatrix(globalH, size, size);
        System.out.println("GLOBAL C: ");
        Calculations.printMatrix(globalC, size, size);
        System.out.println("GLOBAL P: ");
        System.out.println(Arrays.toString(globalP));

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

        for (int i = 0; i < numberOfSteps; i++) {
            double[] initialTemperatures = new double[nodes.size()];
            for (int j = 0; j < nodes.size(); j++) {
                initialTemperatures[j] = femGrid.getNodes().get(j).getTemperature();
            }

            Matrix H = Matrix.from2DArray(finalH);
            Matrix C = Matrix.from2DArray(finalC);
            Vector temperatures = Vector.fromArray(initialTemperatures);
            Vector P = Vector.fromArray(globalP);

            Vector C_t = C.multiply(temperatures);
            C_t = C_t.subtract(P);

            Vector nextTemperatures = C_t.multiply(H.withInverter(LinearAlgebra.GAUSS_JORDAN).inverse());

            for (int j = 0; j < globalData.getNumberOfNodes(); j++) {
                femGrid.getNodes().get(j).setTemperature(nextTemperatures.get(j));
            }
            System.out.println("Time [s] = " + (simulationStepTime * (i+1)) + ": min temp = " + nextTemperatures.min() + " max temp = " + nextTemperatures.max());
        }
    }
}

