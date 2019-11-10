package models;

import java.util.List;

public class GlobalData {

    private double height;
    private double width;
    private double nH;
    private double nW;
    private List<Double> pC;
    private List<Double> wagi;

    //liczba węzłów
    private double nN;

    //liczba elementów
    private double nE;


    public GlobalData(double height, double width, double nH, double nW, List<Double> pc, List<Double> wagi) {
        this.height = height;
        this.width = width;
        this.nH = nH;
        this.nW = nW;
        this.nN = this.nH * this.nW;
        this.nE = (this.nH - 1) * (this.nW - 1);
        this.pC = pc;
        this. wagi = wagi;
    }

    public GlobalData(String height, String width, String nH, String nW) {
        this.height = Double.parseDouble(height);
        this.width = Double.parseDouble(width);
        this.nH = Integer.parseInt(nH);
        this.nW = Integer.parseInt(nW);
        this.nN = this.nH * this.nW;
        this.nE = (this.nH - 1) * (this.nW - 1);
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getnH() {
        return nH;
    }

    public double getnW() {
        return nW;
    }

    public double getnN() {
        return nN;
    }

    public double getnE() {
        return nE;
    }

    public List<Double> getpC() {
        return pC;
    }

    public List<Double> getWagi() {
        return wagi;
    }

    public void printGlobalData() {
        System.out.println("H = " + height);
        System.out.println("w = " + width);
        System.out.println("nH = " + nH);
        System.out.println("nW = " + nW);
        System.out.println("nN = " + nN);
        System.out.println("nE = " + nE);
        System.out.print("pC: [");
        for (int i = 0; i < pC.size(); i++) {
            if(i != pC.size() - 1) System.out.print(pC.get(i) + ",");
            else System.out.println(pC.get(i) + "]");
        }

        System.out.print("wagi: [");
        for (int i = 0; i < wagi.size(); i++) {
            if(i != wagi.size() - 1) System.out.print(wagi.get(i) + ",");
            else System.out.println(wagi.get(i) + "]");
        }

    }
}
