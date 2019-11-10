import functions.NodeAndElementGenerator;
import models.FEMGrid;
import models.GlobalData;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        String[] data = new String[4];
        FEMGrid femGrid = new FEMGrid();

        JSONParser jsonParser = new JSONParser();

        try
        {
            //Read JSON file
            Object obj = jsonParser.parse(new FileReader(System.getProperty("user.dir") + "/src/main/java/mes.json"));

            JSONObject jsonObject = (JSONObject) obj;

            double H = (double) jsonObject.get("H");
            double W = (double) jsonObject.get("W");
            double nH = (double) jsonObject.get("nH");
            double nW = (double) jsonObject.get("nW");

            JSONArray jsonArrayPC = (JSONArray) jsonObject.get("pC");
            List<Double> pC = new ArrayList<>();
            for (Object o : jsonArrayPC) {
                pC.add((double) o);
            }

            JSONArray jsonArrayW = (JSONArray) jsonObject.get("wagi");
            List<Double> wagi = new ArrayList<>();
            for (Object o : jsonArrayW) {
                wagi.add((double) o);
            }

            GlobalData globalData = new GlobalData(H, W, nH, nW, pC, wagi);

            femGrid.setNodes(NodeAndElementGenerator.generateNodes(globalData.getHeight(),globalData.getWidth(),globalData.getnW(),globalData.getnH()));
            femGrid.printGrid();

            femGrid.setElements(NodeAndElementGenerator.generateElements(globalData.getnH(), globalData.getnE()));
            femGrid.printElementsDetails();

            globalData.printGlobalData();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
}

