package functions;

import models.IntegrationPoint;

import java.util.ArrayList;
import java.util.List;

public class GaussInterpolation {
    public static List<IntegrationPoint> setIntegrationPoints(List<Double> pointsList) {

        List<IntegrationPoint> integrationPoints = new ArrayList<>();
        for (int i = 0; i < pointsList.size(); i++) {
            for (double ksi : pointsList) {
                double eta = pointsList.get(i);
                integrationPoints.add(new IntegrationPoint(ksi, eta));
            }
        }
        return integrationPoints;
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


    public static double[][] countKsiDerivatives(List<IntegrationPoint> integrationPoints) {

        int n = integrationPoints.size();
        double[][] derivatives = new double [n][4];

        for (int i = 0; i < n; i++) {
            double eta = integrationPoints.get(i).getEta();
            derivatives[i][0] = ksiShapeFunction1(eta);
            derivatives[i][1] = ksiShapeFunction2(eta);
            derivatives[i][2] = ksiShapeFunction3(eta);
            derivatives[i][3] = ksiShapeFunction4(eta);
        }

        return derivatives;
    }

    public static double[][] countEtaDerivatives(List<IntegrationPoint> integrationPoints) {

        int n = integrationPoints.size();
        double[][] derivatives = new double [n][4];

        for (int i = 0; i < n; i++) {
            double ksi = integrationPoints.get(i).getKsi();
            derivatives[i][0] = etaShapeFunction1(ksi);
            derivatives[i][1] = etaShapeFunction2(ksi);
            derivatives[i][2] = etaShapeFunction3(ksi);
            derivatives[i][3] = etaShapeFunction4(ksi);
        }

        return derivatives;
    }

    public static double[][] shapeFunctionMatrix(List<IntegrationPoint> integrationPoints) {
        int n = integrationPoints.size();
        double[][] shapeFunctions= new double [n][4];

        for (int i = 0; i < n; i++) {
            double ksi = integrationPoints.get(i).getKsi();
            double eta = integrationPoints.get(i).getEta();
            shapeFunctions[i][0] = shapeFunction1(ksi, eta);
            shapeFunctions[i][1] = shapeFunction2(ksi, eta);
            shapeFunctions[i][2] = shapeFunction3(ksi, eta);
            shapeFunctions[i][3] = shapeFunction4(ksi, eta);
        }

        return shapeFunctions;
    }
}
