package models;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import controllers.CoinPriceController;
import controllers.SettingsController;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

@Singleton
public class TransactionModelFactory {
    @Inject
    private CoinPriceController coinPriceController;
    @Inject
    private SettingsController settingsController;
    private final Set<String> poolIdSet = new HashSet<>();

    public String[] splitCoinsAndAmounts(String amountAndCoin) {
        return amountAndCoin.split("@");
    }

    public TransactionModelFactory() {
        createPoolList();
    }

    public void createPoolList() {
        String pool= "-";

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL("https://ocean.defichain.com/v0/mainnet/tokens?size=1000").openConnection();
            String jsonText = "";
            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                jsonText = br.readLine();
            } catch (Exception ex) {
                settingsController.logger.warning("Exception occurred: " + ex);
            }
            JSONObject obj = (JSONObject) JSONValue.parse(jsonText);
            if (obj.get("data") != null) {
                JSONArray data = (JSONArray) obj.get("data");

                for (Object token : data) {
                    JSONObject jsonToken = (JSONObject) token;
                    poolIdSet.add(jsonToken.get("symbol").toString());
                }
            }
        } catch (IOException e) {
            settingsController.logger.warning("Exception occurred: " + e);
        }
    }

    public String getIdFromPoolPair(String poolID) {
        return poolIdSet.stream().filter(symbol -> symbol.contains(poolID)).findFirst().orElse("-");
    }

    public TransactionModel create(Long blockTime, String owner, String type, String amounts, String blockHash, int blockHeight, String poolID, String txid,String rewardType, String poolRatioString){
        TransactionModel transactionModel = new TransactionModel(blockTime, owner, type, amounts, blockHash, blockHeight, txid, rewardType);

        transactionModel.setCryptoValueProperty(Double.parseDouble(splitCoinsAndAmounts(amounts)[0]));
        transactionModel.setCryptoCurrencyProperty(splitCoinsAndAmounts(amounts)[1]);
        if (amounts.split("@")[1].length() > 4 && (type.equals("RemovePoolLiquidity") || type.equals("AddPoolLiquidity"))) {
            transactionModel.setPoolIDProperty(getIdFromPoolPair(amounts.split("@")[1]));
        } else {
            transactionModel.setPoolIDProperty(poolID);
        }
        
        transactionModel.setFiatCurrencyProperty(settingsController.selectedFiatCurrency.getValue());

        if (amounts.split("@")[1].length() == 3 | amounts.split("@")[1].length() == 4) {
            transactionModel.setFiatValueProperty(Double.parseDouble(splitCoinsAndAmounts(amounts)[0]) * coinPriceController.getPriceFromTimeStamp(amounts.split("@")[1].equals("DUSD"), amounts.split("@")[1] + settingsController.selectedFiatCurrency.getValue(), blockTime * 1000L));
        } else {

            if (!poolRatioString.equals("-")) {
                double poolRatio = Double.parseDouble(poolRatioString);
                double token1 = Math.sqrt(poolRatio * Double.parseDouble(amounts.split("@")[0]) * Double.parseDouble(amounts.split("@")[0]));
                double token2 = Math.sqrt(Double.parseDouble(amounts.split("@")[0]) * Double.parseDouble(amounts.split("@")[0]) / poolRatio);

                Double price1 = coinPriceController.getPriceFromTimeStamp(amounts.split("@")[1].contains("DUSD"), amounts.split("@")[1].split("-")[0] + settingsController.selectedFiatCurrency.getValue(), blockTime * 1000L) * token1;
                Double price2 = coinPriceController.getPriceFromTimeStamp(amounts.split("@")[1].contains("DUSD"), amounts.split("@")[1].split("-")[1] + settingsController.selectedFiatCurrency.getValue(), blockTime * 1000L) * token2;
                transactionModel.setFiatValueProperty(price1 + price2);
            }
        }
        
        return transactionModel;
    }
}
