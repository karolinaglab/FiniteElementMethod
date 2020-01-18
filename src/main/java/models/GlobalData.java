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

    private double ambientTemperature;  // [st.C]
    private double initialTemperature; // [st.C]
    private double simulationTime;   // [s]
    private double simulationStepTime;  // [s]
    private double height;  // [m]
    private double width;   // [m]
    private double nH;
    private double nW;
    private List<Double> pC;
    private List<Double> weights;
    private double conductivity;  // [W/m*st.C]
    private double density;       // [kg/m^3]
    private double specificHeat;  // [J/kg*st.C]
    private double alpha;
    //liczba węzłów
    private double numberOfNodes;

    //liczba elementów
    private double numberOfElements;


    public GlobalData(int choice) {

        String path = "";
        switch (choice) {
            case 1:
                path = "/src/main/java/mes.json";
                break;
            case 2:
                path = "/src/main/java/mes2.json";
                break;
        }
        JSONParser jsonParser = new JSONParser();
        try {
            //Read JSON file
            Object obj = jsonParser.parse(new FileReader(System.getProperty("user.dir") + path));

            JSONObject jsonObject = (JSONObject) obj;
            this.ambientTemperature = (double) jsonObject.get("ambientTemperature");
            this.initialTemperature = (double) jsonObject.get("initialTemperature");
            this.simulationTime = (double) jsonObject.get("simulationTime");
            this.simulationStepTime = (double) jsonObject.get("simulationStepTime");
            this.height = (double) jsonObject.get("H");
            this.width = (double) jsonObject.get("W");
            this.nH = (double) jsonObject.get("nH");
            this.nW = (double) jsonObject.get("nW");
            this.conductivity = (double) jsonObject.get("k");
            this.specificHeat = (double) jsonObject.get("specificHeat");
            this.density = (double) jsonObject.get("density");
            this.alpha = (double) jsonObject.get("alpha");
            this.numberOfNodes = this.nH * this.nW;
            this.numberOfElements = (this.nH - 1) * (this.nW - 1);

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

    public double getNumberOfNodes() {
        return numberOfNodes;
    }

    public double getNumberOfElements() {
        return numberOfElements;
    }

    public List<Double> getpC() {
        return pC;
    }

    public List<Double> getWeights() {
        return weights;
    }

    public double getConductivity() {
        return conductivity;
    }

    public double getDensity() {
        return density;
    }

    public double getSpecificHeat() {
        return specificHeat;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getAmbientTemperature() {
        return ambientTemperature;
    }

    public double getSimulationTime() {
        return simulationTime;
    }

    public double getSimulationStepTime() {
        return simulationStepTime;
    }

    public double getInitialTemperature() {
        return initialTemperature;
    }

    public void printGlobalData() {
        System.out.println("Ambient temperature = " + ambientTemperature);
        System.out.println("Initial temperature = " + initialTemperature);
        System.out.println("Simulation time = " + simulationTime);
        System.out.println("Simulation step time = " + simulationStepTime);
        System.out.println("Height = " + height);
        System.out.println("Width = " + width);
        System.out.println("nH = " + nH);
        System.out.println("nW = " + nW);
        System.out.println("nN = " + numberOfNodes);
        System.out.println("nE = " + numberOfElements);
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

        System.out.println("k = " + conductivity);
        System.out.println("density = " + density);
        System.out.println("specificHeat = " + specificHeat);
        System.out.println("alpha = " + alpha);
    }
}
