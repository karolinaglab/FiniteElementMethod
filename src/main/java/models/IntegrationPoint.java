package models;

public final class IntegrationPoint {

    private final double ksi;
    private final double eta;

    public IntegrationPoint(double ksi, double eta) {
        this.ksi = ksi;
        this.eta = eta;
    }

    public double getKsi() {
        return ksi;
    }

    public double getEta() {
        return eta;
    }
}
