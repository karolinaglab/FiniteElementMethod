package models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GlobalData {

    private double height;
    private double width;
    private double nH;
    private double nW;
    private List<Double> pC;
    private List<Double> weights;

    //liczba węzłów
    private double nN;

    //liczba elementów
    private double nE;


    public GlobalData() {

        JSONParser jsonParser = new JSONParser();

        try {
            //Read JSON file
            Object obj = jsonParser.parse(new FileReader(System.getProperty("user.dir") + "/src/main/java/mes.json"));

            JSONObject jsonObject = (JSONObject) obj;

            this.height = (double) jsonObject.get("H");
            this.width = (double) jsonObject.get("W");
            this.nH = (double) jsonObject.get("nH");
            this.nW = (double) jsonObject.get("nW");
            this.nN = this.nH * this.nW;
            this.nE = (this.nH - 1) * (this.nW - 1);

            JSONArray jsonArrayPC = (JSONArray) jsonObject.get("pC");
            this.pC = new ArrayList<>();
            for (Object o : jsonArrayPC) {
                pC.add((double) o);
            }

            JSONArray jsonArrayW = (JSONArray) jsonObject.get("weights");
            this.weights = new ArrayList<>();
            for (Object o : jsonArrayW) {
                weights.add((double) o);
            }

        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public GlobalData(double height, double width, double nH, double nW, List<Double> pc, List<Double> weights) {
        this.height = height;
        this.width = width;
        this.nH = nH;
        this.nW = nW;
        this.nN = this.nH * this.nW;
        this.nE = (this.nH - 1) * (this.nW - 1);
        this.pC = pc;
        this. weights = weights;
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
        return weights;
    }

    public void printGlobalData() {
        System.out.println("Height = " + height);
        System.out.println("Width = " + width);
        System.out.println("nH = " + nH);
        System.out.println("nW = " + nW);
        System.out.println("nN = " + nN);
        System.out.println("nE = " + nE);
        System.out.print("pC: [");
        for (int i = 0; i < pC.size(); i++) {
            if(i != pC.size() - 1) System.out.print(pC.get(i) + ",");
            else System.out.println(pC.get(i) + "]");
        }

        System.out.print("Weights: [");
        for (int i = 0; i < weights.size(); i++) {
            if(i != weights.size() - 1) System.out.print(weights.get(i) + ",");
            else System.out.println(weights.get(i) + "]");
        }

    }
}
