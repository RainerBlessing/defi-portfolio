package models;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CoinPriceModel implements java.io.Serializable {

    public String lastTimeStamp="1589179630";

    TreeMap<String, List<List<String>>> coinPriceList;

    public CoinPriceModel() {

        this.coinPriceList = new TreeMap<>();

        List<List<String>> initialList = new ArrayList<>();
        List<String> initialDFIList = new ArrayList<>();
        initialDFIList.add("1589179630000");
        initialDFIList.add("0.092523");
        initialList.add(initialDFIList);

        coinPriceList.put("DFIEUR",initialList);

        initialList = new ArrayList<>();
        initialDFIList = new ArrayList<>();
        initialDFIList.add("1589179630000");
        initialDFIList.add("0.1");

        coinPriceList.put("DFIUSD",initialList);

        initialList = new ArrayList<>();
        initialDFIList = new ArrayList<>();
        initialDFIList.add("1589179630000");
        initialDFIList.add("0.0973");

        coinPriceList.put("DFICHF",initialList);

        coinPriceList.put("BTCEUR", new ArrayList<>());
        coinPriceList.put("BTCUSD", new ArrayList<>());
        coinPriceList.put("BTCCHF", new ArrayList<>());

        coinPriceList.put("ETHEUR", new ArrayList<>());
        coinPriceList.put("ETHUSD", new ArrayList<>());
        coinPriceList.put("ETHCHF", new ArrayList<>());

        coinPriceList.put("USDTEUR", new ArrayList<>());
        coinPriceList.put("USDTUSD", new ArrayList<>());
        coinPriceList.put("USDTCHF", new ArrayList<>());

        coinPriceList.put("LTCEUR", new ArrayList<>());
        coinPriceList.put("LTCUSD", new ArrayList<>());
        coinPriceList.put("LTCCHF", new ArrayList<>());

        coinPriceList.put("BCHEUR", new ArrayList<>());
        coinPriceList.put("BCHUSD", new ArrayList<>());
        coinPriceList.put("BCHCHF", new ArrayList<>());

        coinPriceList.put("DOGEEUR", new ArrayList<>());
        coinPriceList.put("DOGEUSD", new ArrayList<>());
        coinPriceList.put("DOGECHF", new ArrayList<>());

        coinPriceList.put("USDCEUR", new ArrayList<>());
        coinPriceList.put("USDCUSD", new ArrayList<>());
        coinPriceList.put("USDCCHF", new ArrayList<>());

    }

    public TreeMap<String, List<List<String>>> GetKeyMap(){
        return this.coinPriceList;
    }

    public void SetKeyMap(TreeMap<String, List<List<String>>> coinPriceList){
        this.coinPriceList=coinPriceList;
    }

}
