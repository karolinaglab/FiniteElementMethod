package functions;

import models.*;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MatrixCalculations {

    public static double[][] countJacobian(Element element, double[] ksiShapeFunctions, double[] etaShapeFunctions) {
        double[][] jacobian = new double[2][2];
        double dXdKsi = 0;
        double dYdKsi = 0;
        double dXdEta = 0;
        double dYdEta = 0;
        List<Node> elementNodes = element.getNodes();

        for (int i = 0; i < ksiShapeFunctions.length; i++) {
            dXdKsi += ksiShapeFunctions[i] * elementNodes.get(i).getX();
            dYdKsi += ksiShapeFunctions[i] * elementNodes.get(i).getY();
            dXdEta += etaShapeFunctions[i] * elementNodes.get(i).getX();
            dYdEta += etaShapeFunctions[i] * elementNodes.get(i).getY();
        }

        jacobian[0][0] = dXdKsi;
        jacobian[0][1] = dYdKsi;
        jacobian[1][0] = dXdEta;
        jacobian[1][1] = dYdEta;

        return jacobian;
    }




    public static double[][] countMatrixH(Element element, double[][] ksiShapeFunctions, double[][] etaShapeFunctions, double conductivity) {

        double[][] H = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                H[i][j] = 0;
            }
        }

        for (int m = 0; m < 4; m++) {

            double[][] jacobian;
            jacobian = countJacobian(element, ksiShapeFunctions[m], etaShapeFunctions[m]);

            double det = jacobian[0][0] * jacobian[1][1] - jacobian[0][1] * jacobian[1][0];

            double[][] dNdXYderivatives = new double[2][4];
            double[][] reversedJacobian = reverseMatrix(jacobian);

            for (int i = 0; i < 4; i++) {
                dNdXYderivatives[0][i] = (1 / det) * (reversedJacobian[0][0] * ksiShapeFunctions[m][i] + reversedJacobian[0][1] * etaShapeFunctions[m][i]);
                dNdXYderivatives[1][i] = (1 / det) * (reversedJacobian[1][0] * ksiShapeFunctions[m][i] + reversedJacobian[1][1] * etaShapeFunctions[m][i]);
            }

            double[][] X;
            X = vectorsMultiplication(dNdXYderivatives[0], dNdXYderivatives[0]);

            double[][] Y;
            Y = vectorsMultiplication(dNdXYderivatives[1], dNdXYderivatives[1]);

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    H[i][j] += conductivity * (X[i][j] + Y[i][j]) * det;
                }
            }
        }

        return H;
    }


    public static double[][] countMatrixC(Element element, double[][] ksiShapeFunctions, double[][] etaShapeFunctions,double[][] shapeFunctions, double density, double specificHeat) {
        double[][] C = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                C[i][j] = 0;
            }
        }

        for (int m = 0; m < 4; m++) {

            double[][] jacobian;
            jacobian = countJacobian(element, ksiShapeFunctions[m], etaShapeFunctions[m]);

            double det = jacobian[0][0] * jacobian[1][1] - jacobian[0][1] * jacobian[1][0];

            double[][] N;
            N = vectorsMultiplication(shapeFunctions[m], shapeFunctions[m]);

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    C[i][j] += specificHeat * density * N[i][j] * det;
                }
            }
        }

        return C;
    }

    public static double[][] countMatrixHBC(Element element, double alpha) {
        double[][] HBC = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                HBC[i][j] = 0;
            }
        }

        //Integration points list
        List<IntegrationPoint> integrationPoints = new ArrayList<>();
        integrationPoints.add(new IntegrationPoint(-0.5773502692, -1));
        integrationPoints.add(new IntegrationPoint(0.5773502692, -1));
        integrationPoints.add(new IntegrationPoint(1, -0.5773502692));
        integrationPoints.add(new IntegrationPoint(1, 0.5773502692));
        integrationPoints.add(new IntegrationPoint(0.5773502692, 1));
        integrationPoints.add(new IntegrationPoint(-0.5773502692, 1));
        integrationPoints.add(new IntegrationPoint(-1, 0.5773502692));
        integrationPoints.add(new IntegrationPoint(-1, -0.5773502692));

        // Shape functions for integration points
        double[][] shapeFunctions = GaussInterpolation.shapeFunctionMatrix(integrationPoints);

        for (int i = 0; i < 4; i++) {
            Node node1;
            Node node2;
            double length;
            if (i == 3) {
                node1 = element.getNodes().get(i);
                node2 = element.getNodes().get(0);
                double x = node1.getX() - node2.getX();
                double y = node1.getY() - node2.getY();
                length = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));

            } else {
                node1 = element.getNodes().get(i);
                node2 = element.getNodes().get(i+1);
                double x = node1.getX() - node2.getX();
                double y = node1.getY() - node2.getY();
                length = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
            }

            double det = length / 2;
            if(node1.isBoundaryCondition() && node2.isBoundaryCondition()) {
                double[][] H1 = vectorsMultiplication(shapeFunctions[i*2], shapeFunctions[i*2]);
                double[][] H2 = vectorsMultiplication(shapeFunctions[i*2 + 1], shapeFunctions[i*2 + 1]);

                for (int m = 0; m < 4; m++) {
                    for (int n = 0; n < 4; n++) {
                        HBC[m][n] += alpha * (H1[m][n] + H2[m][n]) * det;
                    }
                }
            }
        }
        return HBC;
    }

    public static double[] countVectorP(Element element, double alpha, double ambientTemperature) {
        double[] P = new double[4];
        Arrays.fill(P, 0);

        //Integration points list
        List<IntegrationPoint> integrationPoints = new ArrayList<>();
        integrationPoints.add(new IntegrationPoint(-0.5773502692, -1));
        integrationPoints.add(new IntegrationPoint(0.5773502692, -1));
        integrationPoints.add(new IntegrationPoint(1, -0.5773502692));
        integrationPoints.add(new IntegrationPoint(1, 0.5773502692));
        integrationPoints.add(new IntegrationPoint(0.5773502692, 1));
        integrationPoints.add(new IntegrationPoint(-0.5773502692, 1));
        integrationPoints.add(new IntegrationPoint(-1, 0.5773502692));
        integrationPoints.add(new IntegrationPoint(-1, -0.5773502692));

        // Shape functions for integration points
        double[][] shapeFunctions = GaussInterpolation.shapeFunctionMatrix(integrationPoints);

        for (int i = 0; i < 4; i++) {
            Node node1;
            Node node2;
            double length;
            if (i == 3) {
                node1 = element.getNodes().get(i);
                node2 = element.getNodes().get(0);
                double x = node1.getX() - node2.getX();
                double y = node1.getY() - node2.getY();
                length = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));

            } else {
                node1 = element.getNodes().get(i);
                node2 = element.getNodes().get(i+1);
                double x = node1.getX() - node2.getX();
                double y = node1.getY() - node2.getY();
                length = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
            }

            double det = length / 2;
            if(node1.isBoundaryCondition() && node2.isBoundaryCondition()) {
                double[] P1 = shapeFunctions[i*2];
                double[] P2 = shapeFunctions[i*2 + 1];

                for (int m = 0; m < 4; m++) {
                   P[m] += (-1) * alpha * (P1[m] + P2[m]) * det * ambientTemperature;
                }
            }
        }
        return P;
    }

    public static double[][] countMatrixHforIntegrationPoint(double[][] jacobian, double[] ksiShapeFunctions, double[] etaShapeFunctions) {

        double[][] H = new double[4][4];

        double det = jacobian[0][0] * jacobian[1][1] - jacobian[0][1] * jacobian[1][0];

        double[][] dNdXYderivatives = new double[2][4];
        double[][] reversedJacobian = reverseMatrix(jacobian);

        for (int i = 0; i < 4; i++) {
            dNdXYderivatives[0][i] = (1 /det) * (reversedJacobian[0][0] * ksiShapeFunctions[i] + reversedJacobian[0][1] * etaShapeFunctions[i]);
            dNdXYderivatives[1][i] = (1 /det) * (reversedJacobian[1][0] * ksiShapeFunctions[i] + reversedJacobian[1][1] * etaShapeFunctions[i]);
        }

        double[][] X = new double[4][4];
        X = vectorsMultiplication(dNdXYderivatives[0], dNdXYderivatives[0]);

        double[][] Y = new double[4][4];
        Y = vectorsMultiplication(dNdXYderivatives[1], dNdXYderivatives[1]);

        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                H[i][j] = 30 * (X[i][j] + Y[i][j]);
            }
        }

        return H;
    }



    public static void matrixAggregation(List<Element> elements, double[][] globalH, double[][] globalC, double[] globalP) {

        for (Element element : elements) {
            double[] ID = element.getID();
            for (int i = 0; i < ID.length; i++) {
                globalP[(int) ID[i] - 1] += element.getP()[i];
                for (int j = 0; j < ID.length; j++) {
                    globalH[(int) ID[i] - 1][(int) ID[j] -1] += element.getH()[i][j] + element.getHBC()[i][j];
                    globalC[(int) ID[i] - 1][(int) ID[j] -1] += element.getC()[i][j];
                }
            }
        }

    }

    public static void printMatrix(double[][] matrix, int m, int n) {
        for (int i = 0; i < m; i++) {
            DecimalFormat decimalFormat = new DecimalFormat("#.###");
            for (int j = 0; j < n; j++) {
                System.out.printf("%10s" , decimalFormat.format(matrix[i][j]));
            }
            System.out.println();
        }
    }

    public static double[][] reverseMatrix(double[][] matrix) {
        double[][] reversed = new double[2][2];
        reversed[0][0] = matrix[1][1];
        reversed[0][1] = -1 * matrix[0][1];
        reversed[1][0] = -1 * matrix[1][0];
        reversed[1][1] = matrix[0][0];

        return reversed;
    }

    public static double[][] vectorsMultiplication(double[] vector1, double[] vector2) {
        int m = vector1.length;
        int n = vector2.length;

        double[][] matrix = new double[m][n];

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                matrix[i][j] = vector1[i] * vector2[j];
            }
        }

        return matrix;
    }

}
