package portfolio.controllers;

import com.litesoftwares.coingecko.CoinGeckoApiClient;
import com.litesoftwares.coingecko.impl.CoinGeckoApiClientImpl;
import portfolio.models.CoinPriceModel;
import java.io.*;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class CoinPriceController {

    private CoinPriceModel coinPriceModel;
    String strCoinPriceData = SettingsController.getInstance().DEFI_PORTFOLIO_HOME + SettingsController.getInstance().strCoinPriceData;

        private static CoinPriceController OBJ;

        static {
            OBJ = new CoinPriceController();
        }

        public static CoinPriceController getInstance() {
            return OBJ;
        }
    public CoinPriceController() {
        getCoinPriceLocal(this.strCoinPriceData);
    }

    public void updateCoinPriceData() {

        CoinPriceModel coinPrice = getCoinPriceLocal(this.strCoinPriceData);
        CoinGeckoApiClient client = new CoinGeckoApiClientImpl();

        long currentTimeStamp = new Timestamp(System.currentTimeMillis()).getTime() / 1000L;
        try {

            if (!TransactionController.getInstance().getDate(Long.toString(currentTimeStamp), "Daily").equals(TransactionController.getInstance().getDate(coinPrice.lastTimeStamp, "Daily"))) {

                if (client.getCoinMarketChartRangeById("defichain", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices().size() > 0) {

                    //Update DFI
                    TreeMap<String, List<List<String>>> coinPriceList;
                    coinPriceList = coinPrice.GetKeyMap();

                    //Update DFI
                    coinPriceList.get("DFIEUR").addAll(client.getCoinMarketChartRangeById("defichain", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("DFIUSD").addAll(client.getCoinMarketChartRangeById("defichain", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("DFICHF").addAll(client.getCoinMarketChartRangeById("defichain", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

                    //Update BTC
                    coinPriceList.get("BTCEUR").addAll(client.getCoinMarketChartRangeById("bitcoin", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("BTCUSD").addAll(client.getCoinMarketChartRangeById("bitcoin", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("BTCCHF").addAll(client.getCoinMarketChartRangeById("bitcoin", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

                    //Update ETH
                    coinPriceList.get("ETHEUR").addAll(client.getCoinMarketChartRangeById("ethereum", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("ETHUSD").addAll(client.getCoinMarketChartRangeById("ethereum", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("ETHCHF").addAll(client.getCoinMarketChartRangeById("ethereum", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

                    //Update USDT
                    coinPriceList.get("USDTEUR").addAll(client.getCoinMarketChartRangeById("tether", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("USDTUSD").addAll(client.getCoinMarketChartRangeById("tether", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("USDTCHF").addAll(client.getCoinMarketChartRangeById("tether", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

                    //Update LTC
                    coinPriceList.get("LTCEUR").addAll(client.getCoinMarketChartRangeById("litecoin", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("LTCUSD").addAll(client.getCoinMarketChartRangeById("litecoin", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("LTCCHF").addAll(client.getCoinMarketChartRangeById("litecoin", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

                    //Update BCH
                    coinPriceList.get("BCHEUR").addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("BCHUSD").addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("BCHCHF").addAll(client.getCoinMarketChartRangeById("bitcoin-cash", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

                    //Update BCH
                    coinPriceList.get("DOGEEUR").addAll(client.getCoinMarketChartRangeById("dogecoin", "eur", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("DOGEUSD").addAll(client.getCoinMarketChartRangeById("dogecoin", "usd", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());
                    coinPriceList.get("DOGECHF").addAll(client.getCoinMarketChartRangeById("dogecoin", "chf", coinPrice.lastTimeStamp, Long.toString(currentTimeStamp)).getPrices());

                    coinPrice.SetKeyMap(coinPriceList);
                    coinPrice.lastTimeStamp = Long.toString(currentTimeStamp);

                }
            }else{
                if (currentTimeStamp > Long.parseLong(coinPrice.lastTimeStamp)){
                    //Update DFI
                    TreeMap<String, List<List<String>>> coinPriceList;
                    coinPriceList = coinPrice.GetKeyMap();

                    //Update DFI
                    List<String> newPriceDFIEUR = new ArrayList<>();
                    newPriceDFIEUR.add(Long.toString(currentTimeStamp*1000L));
                    newPriceDFIEUR.add(client.getPrice("defichain", "eur").get("defichain").get("eur").toString());
                    coinPriceList.get("DFIEUR").remove(coinPriceList.get("DFIEUR").size()-1);
                    coinPriceList.get("DFIEUR").add(newPriceDFIEUR);

                    List<String> newPriceDFIUSD = new ArrayList<>();
                    newPriceDFIUSD.add(Long.toString(currentTimeStamp*1000L));
                    newPriceDFIUSD.add(client.getPrice("defichain", "usd").get("defichain").get("usd").toString());
                    coinPriceList.get("DFIUSD").remove(coinPriceList.get("DFIUSD").size()-1);
                    coinPriceList.get("DFIUSD").add(newPriceDFIUSD);

                    List<String> newPriceDFICHF = new ArrayList<>();
                    newPriceDFICHF.add(Long.toString(currentTimeStamp*1000L));
                    newPriceDFICHF.add(client.getPrice("defichain", "chf").get("defichain").get("chf").toString());
                    coinPriceList.get("DFICHF").remove(coinPriceList.get("DFICHF").size()-1);
                    coinPriceList.get("DFICHF").add(newPriceDFICHF);

                    //Update BTC
                    List<String> newPriceBTCEUR = new ArrayList<>();
                    newPriceBTCEUR.add(Long.toString(currentTimeStamp*1000L));
                    newPriceBTCEUR.add(client.getPrice("bitcoin", "eur").get("bitcoin").get("eur").toString());
                    coinPriceList.get("BTCEUR").remove(coinPriceList.get("BTCEUR").size()-1);
                    coinPriceList.get("BTCEUR").add(newPriceBTCEUR);

                    List<String> newPriceBTCUSD = new ArrayList<>();
                    newPriceBTCUSD.add(Long.toString(currentTimeStamp*1000L));
                    newPriceBTCUSD.add(client.getPrice("bitcoin", "usd").get("bitcoin").get("usd").toString());
                    coinPriceList.get("BTCUSD").remove(coinPriceList.get("BTCUSD").size()-1);
                    coinPriceList.get("BTCUSD").add(newPriceBTCUSD);

                    List<String> newPriceBTCCHF = new ArrayList<>();
                    newPriceBTCCHF.add(Long.toString(currentTimeStamp*1000L));
                    newPriceBTCCHF.add(client.getPrice("bitcoin", "chf").get("bitcoin").get("chf").toString());
                    coinPriceList.get("BTCCHF").remove(coinPriceList.get("BTCCHF").size()-1);
                    coinPriceList.get("BTCCHF").add(newPriceBTCCHF);

                    //Update ETH

                    List<String> newPriceETHEUR = new ArrayList<>();
                    newPriceETHEUR.add(Long.toString(currentTimeStamp*1000L));
                    newPriceETHEUR.add(client.getPrice("ethereum", "eur").get("ethereum").get("eur").toString());
                    coinPriceList.get("ETHEUR").remove(coinPriceList.get("ETHEUR").size()-1);
                    coinPriceList.get("ETHEUR").add(newPriceETHEUR);

                    List<String> newPriceETHUSD = new ArrayList<>();
                    newPriceETHUSD.add(Long.toString(currentTimeStamp*1000L));
                    newPriceETHUSD.add(client.getPrice("ethereum", "usd").get("ethereum").get("usd").toString());
                    coinPriceList.get("ETHUSD").remove(coinPriceList.get("ETHUSD").size()-1);
                    coinPriceList.get("ETHUSD").add(newPriceETHUSD);

                    List<String> newPriceETHCHF = new ArrayList<>();
                    newPriceETHCHF.add(Long.toString(currentTimeStamp*1000L));
                    newPriceETHCHF.add(client.getPrice("ethereum", "chf").get("ethereum").get("chf").toString());
                    coinPriceList.get("ETHCHF").remove(coinPriceList.get("ETHCHF").size()-1);
                    coinPriceList.get("ETHCHF").add(newPriceETHCHF);


                    //Update USDT

                    List<String> newPriceUSDTEUR = new ArrayList<>();
                    newPriceUSDTEUR.add(Long.toString(currentTimeStamp*1000L));
                    newPriceUSDTEUR.add(client.getPrice("tether", "eur").get("tether").get("eur").toString());
                    coinPriceList.get("USDTEUR").remove(coinPriceList.get("USDTEUR").size()-1);
                    coinPriceList.get("USDTEUR").add(newPriceUSDTEUR);

                    List<String> newPriceUSDTUSD = new ArrayList<>();
                    newPriceUSDTUSD.add(Long.toString(currentTimeStamp*1000L));
                    newPriceUSDTUSD.add(client.getPrice("tether", "usd").get("tether").get("usd").toString());
                    coinPriceList.get("USDTUSD").remove(coinPriceList.get("USDTUSD").size()-1);
                    coinPriceList.get("USDTUSD").add(newPriceUSDTUSD);

                    List<String> newPriceUSDTCHF = new ArrayList<>();
                    newPriceUSDTCHF.add(Long.toString(currentTimeStamp*1000L));
                    newPriceUSDTCHF.add(client.getPrice("tether", "chf").get("tether").get("chf").toString());
                    coinPriceList.get("USDTCHF").remove(coinPriceList.get("USDTCHF").size()-1);
                    coinPriceList.get("USDTCHF").add(newPriceUSDTCHF);

                    //Update LTC

                    List<String> newPriceLTCEUR = new ArrayList<>();
                    newPriceLTCEUR.add(Long.toString(currentTimeStamp*1000L));
                    newPriceLTCEUR.add(client.getPrice("litecoin", "eur").get("litecoin").get("eur").toString());
                    coinPriceList.get("LTCEUR").remove(coinPriceList.get("LTCEUR").size()-1);
                    coinPriceList.get("LTCEUR").add(newPriceLTCEUR);

                    List<String> newPriceLTCUSD = new ArrayList<>();
                    newPriceLTCUSD.add(Long.toString(currentTimeStamp*1000L));
                    newPriceLTCUSD.add(client.getPrice("litecoin", "usd").get("litecoin").get("usd").toString());
                    coinPriceList.get("LTCUSD").remove(coinPriceList.get("LTCUSD").size()-1);
                    coinPriceList.get("LTCUSD").add(newPriceLTCUSD);

                    List<String> newPriceLTCCHF = new ArrayList<>();
                    newPriceLTCCHF.add(Long.toString(currentTimeStamp*1000L));
                    newPriceLTCCHF.add(client.getPrice("litecoin", "chf").get("litecoin").get("chf").toString());
                    coinPriceList.get("LTCCHF").remove(coinPriceList.get("LTCCHF").size()-1);
                    coinPriceList.get("LTCCHF").add(newPriceLTCCHF);

                    //Update BCH

                    List<String> newPriceBCHEUR = new ArrayList<>();
                    newPriceBCHEUR.add(Long.toString(currentTimeStamp*1000L));
                    newPriceBCHEUR.add(client.getPrice("bitcoin-cash", "eur").get("bitcoin-cash").get("eur").toString());
                    coinPriceList.get("BCHEUR").remove(coinPriceList.get("BCHEUR").size()-1);
                    coinPriceList.get("BCHEUR").add(newPriceBCHEUR);

                    List<String> newPriceBCHUSD = new ArrayList<>();
                    newPriceBCHUSD.add(Long.toString(currentTimeStamp*1000L));
                    newPriceBCHUSD.add(client.getPrice("bitcoin-cash", "usd").get("bitcoin-cash").get("usd").toString());
                    coinPriceList.get("BCHUSD").remove(coinPriceList.get("BCHUSD").size()-1);
                    coinPriceList.get("BCHUSD").add(newPriceBCHUSD);

                    List<String> newPriceBCHCHF = new ArrayList<>();
                    newPriceBCHCHF.add(Long.toString(currentTimeStamp*1000L));
                    newPriceBCHCHF.add(client.getPrice("bitcoin-cash", "chf").get("bitcoin-cash").get("chf").toString());
                    coinPriceList.get("BCHCHF").remove(coinPriceList.get("BCHCHF").size()-1);
                    coinPriceList.get("BCHCHF").add(newPriceBCHCHF);

                    //Update Doge

                    List<String> newPriceDOGEEUR = new ArrayList<>();
                    newPriceDOGEEUR.add(Long.toString(currentTimeStamp*1000L));
                    newPriceDOGEEUR.add(client.getPrice("dogecoin", "eur").get("dogecoin").get("eur").toString());
                    coinPriceList.get("DOGEEUR").remove(coinPriceList.get("DOGEEUR").size()-1);
                    coinPriceList.get("DOGEEUR").add(newPriceDOGEEUR);

                    List<String> newPriceDOGEUSD = new ArrayList<>();
                    newPriceDOGEUSD.add(Long.toString(currentTimeStamp*1000L));
                    newPriceDOGEUSD.add(client.getPrice("dogecoin", "usd").get("dogecoin").get("usd").toString());
                    coinPriceList.get("DOGEUSD").remove(coinPriceList.get("DOGEUSD").size()-1);
                    coinPriceList.get("DOGEUSD").add(newPriceDOGEUSD);

                    List<String> newPriceDOGECHF = new ArrayList<>();
                    newPriceDOGECHF.add(Long.toString(currentTimeStamp*1000L));
                    newPriceDOGECHF.add(client.getPrice("dogecoin", "chf").get("dogecoin").get("chf").toString());
                    coinPriceList.get("DOGECHF").remove(coinPriceList.get("DOGECHF").size()-1);
                    coinPriceList.get("DOGECHF").add(newPriceDOGECHF);

                    coinPrice.SetKeyMap(coinPriceList);
                    coinPrice.lastTimeStamp = Long.toString(currentTimeStamp);
                }
            }

            // Serialization
            try {
                //Saving of object in a file
                FileOutputStream file = new FileOutputStream(this.strCoinPriceData);
                ObjectOutputStream out = new ObjectOutputStream(file);

                // Method for serialization of object
                out.writeObject(coinPrice);
                out.close();
                file.close();
                this.coinPriceModel = coinPrice;
            } catch (IOException e) {
                SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
            }
        } catch (Exception e) {
            SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
        }
    }

    public String getCoinGeckoName(String tokenName){
        switch (tokenName) {
            case "DFI":
                tokenName = "defichain";
                break;
            case "ETH-DFI":
            case "ETH":
                tokenName = "ethereum";
                break;
            case "BTC":
            case "BTC-DFI":
                tokenName = "bitcoin";
                break;
            case "USDT":
            case "USDT-DFI":
                tokenName = "tether";
                break;
            case "DOGE":
            case "DOGE-DFI":
                tokenName = "dogecoin";
                break;
            case "LTC":
            case "LTC-DFI":
                tokenName = "litecoin";
                break;
            case "BCH":
            case "BCH-DFI":
                tokenName = "bitcoin-cash";
                break;
            default:
                tokenName = "-";
                break;
        }
        return tokenName;
    }

    public String getLastTimeStamp(String strCoinPricePath) {
        return getCoinPriceLocal(strCoinPricePath).lastTimeStamp;
    }

    public CoinPriceModel getCoinPriceLocal(String strCoinPricePath) {
        CoinPriceModel coinPrice = new CoinPriceModel();
        if (new File(strCoinPricePath).exists()) {
            try {
                // Reading the object from a file
                FileInputStream file = new FileInputStream(strCoinPricePath);
                ObjectInputStream in = new ObjectInputStream(file);

                // Method for deserialization of object
                coinPrice = (CoinPriceModel) in.readObject();
                this.coinPriceModel = coinPrice;
                in.close();
                file.close();

            } catch (IOException | ClassNotFoundException e) {
                SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
            }
        }
        return coinPrice;
    }

    public double getPriceFromTimeStamp(String coinFiatPair, Long timeStamp) {
        double price = 0;
        if(this.coinPriceModel != null){

        if( this.coinPriceModel.GetKeyMap().containsKey(coinFiatPair)){
            for (int i = this.coinPriceModel.GetKeyMap().get(coinFiatPair).size() - 1; i >= 0; i--)
                if (timeStamp > Long.parseLong(this.coinPriceModel.GetKeyMap().get(coinFiatPair).get(i).get(0))) {
                    return Double.parseDouble(this.coinPriceModel.GetKeyMap().get(coinFiatPair).get(i).get(1));
                }
        }
        }
        return price;
    }
}
