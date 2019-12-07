package functions;

import models.Element;
import models.InterpolationPoint;
import models.Node;

import java.util.ArrayList;
import java.util.List;

public class GaussInterpolation {


    public static List<InterpolationPoint> setInterpolationPoints(List<Double> pointsList) {

        List<InterpolationPoint> interpolationPoints = new ArrayList<>();
        for (int i = 0; i < pointsList.size(); i++) {
            for (int j = 0; j < pointsList.size(); j++) {
                double ksi = pointsList.get(j);
                double eta = pointsList.get(i);
                interpolationPoints.add(new InterpolationPoint(ksi, eta));
            }
        }
        return interpolationPoints;
    }

    public static double shapeFunction1(double ksi, double eta) {
        return 0.25 * (1 - ksi) * (1 - eta);
    }

    public static double shapeFunction2(double ksi, double eta) {
        return 0.25 * (1 + ksi) * (1 - eta);
    }

    public static double shapeFunction3(double ksi, double eta) {
        return 0.25 * (1 + ksi) * (1 + eta);
    }

    public static double shapeFunction4(double ksi, double eta) {
        return 0.25 * (1 - ksi) * (1 + eta);
    }


    public static double ksiShapeFunction1(double eta) { return -0.25 * (1 - eta); }

    public static double ksiShapeFunction2(double eta) {
        return 0.25 * (1 - eta);
    }

    public static double ksiShapeFunction3(double eta) {
        return 0.25 * (1 + eta);
    }

    public static double ksiShapeFunction4(double eta) {
        return -0.25 * (1 + eta);
    }


    public static double etaShapeFunction1(double ksi) {
        return -0.25 * (1 - ksi);
    }

    public static double etaShapeFunction2(double ksi) {
        return -0.25 * (1 + ksi);
    }

    public static double etaShapeFunction3(double ksi) {
        return 0.25 * (1 + ksi);
    }

    public static double etaShapeFunction4(double ksi) {
        return 0.25 * (1 - ksi);
    }


    public static double[][] countKsiDerivatives(List<InterpolationPoint> interpolationPoints) {

        int n = interpolationPoints.size();
        double[][] derivatives = new double [n][4];

        for (int i = 0; i < n; i++) {
            double eta = interpolationPoints.get(i).getEta();
            derivatives[i][0] = ksiShapeFunction1(eta);
            derivatives[i][1] = ksiShapeFunction2(eta);
            derivatives[i][2] = ksiShapeFunction3(eta);
            derivatives[i][3] = ksiShapeFunction4(eta);
        }

        return derivatives;
    }

    public static double[][] countEtaDerivatives(List<InterpolationPoint> interpolationPoints) {

        int n = interpolationPoints.size();
        double[][] derivatives = new double [n][4];

        for (int i = 0; i < n; i++) {
            double ksi = interpolationPoints.get(i).getKsi();
            derivatives[i][0] = etaShapeFunction1(ksi);
            derivatives[i][1] = etaShapeFunction2(ksi);
            derivatives[i][2] = etaShapeFunction3(ksi);
            derivatives[i][3] = etaShapeFunction4(ksi);
        }

        return derivatives;
    }

    public static double[][] shapeFunctionMatrix(List<InterpolationPoint> interpolationPoints) {
        int n = interpolationPoints.size();
        double[][] shapeFunctions= new double [n][4];

        for (int i = 0; i < n; i++) {
            double ksi = interpolationPoints.get(i).getKsi();
            double eta = interpolationPoints.get(i).getEta();
            shapeFunctions[i][0] = shapeFunction1(ksi, eta);
            shapeFunctions[i][1] = shapeFunction2(ksi, eta);
            shapeFunctions[i][2] = shapeFunction3(ksi, eta);
            shapeFunctions[i][3] = shapeFunction4(ksi, eta);
        }

        return shapeFunctions;
    }


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




    public static double[][] countMatrixH(Element element, double[][] ksiShapeFunctions, double[][] etaShapeFunctions) {

        double[][] H = new double[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                H[i][j] = 0;
            }
        }

        for (int k = 0; k < 4; k++) {

            double[][] jacobian;
            jacobian = countJacobian(element, ksiShapeFunctions[k], etaShapeFunctions[k]);

            double det = jacobian[0][0] * jacobian[1][1] - jacobian[0][1] * jacobian[1][0];

            double[][] dNdXYderivatives = new double[2][4];
            double[][] reversedJacobian = reverseMatrix(jacobian);

            for (int i = 0; i < 4; i++) {
                dNdXYderivatives[0][i] = (1 / det) * (reversedJacobian[0][0] * ksiShapeFunctions[k][i] + reversedJacobian[0][1] * etaShapeFunctions[k][i]);
                dNdXYderivatives[1][i] = (1 / det) * (reversedJacobian[1][0] * ksiShapeFunctions[k][i] + reversedJacobian[1][1] * etaShapeFunctions[k][i]);
            }

            double[][] X;
            X = vectorsMultiplication(dNdXYderivatives[0], dNdXYderivatives[0]);

            double[][] Y;
            Y = vectorsMultiplication(dNdXYderivatives[1], dNdXYderivatives[1]);

            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    H[i][j] += 30 * (X[i][j] + Y[i][j]) * det;
                }
            }
        }

        return H;
    }



    public static double[][] countMatrixHforInterpolationPoint(double[][] jacobian, double[] ksiShapeFunctions, double[] etaShapeFunctions) {

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



    public static void printMatrix(double[][] matrix, int m, int n) {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(matrix[i][j] + "   ");
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
