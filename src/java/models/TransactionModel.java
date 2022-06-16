package models;

import javafx.beans.property.*;
import controllers.SettingsController;
import controllers.TransactionController;
import services.ExportService;

public class TransactionModel {

    public final StringProperty ownerProperty = new SimpleStringProperty("");
    public final IntegerProperty blockHeightProperty = new SimpleIntegerProperty(0);
    public final StringProperty blockHashProperty = new SimpleStringProperty("");
    public final LongProperty blockTimeProperty = new SimpleLongProperty(0L);
    public final StringProperty typeProperty = new SimpleStringProperty("");
    public final StringProperty poolIDProperty = new SimpleStringProperty("");
    public final StringProperty amountProperty = new SimpleStringProperty("");
    public final StringProperty cryptoCurrencyProperty = new SimpleStringProperty("");
    public final DoubleProperty cryptoValueProperty = new SimpleDoubleProperty(0.0);
    public final DoubleProperty fiatValueProperty = new SimpleDoubleProperty(0.0);
    public final StringProperty fiatCurrencyProperty = new SimpleStringProperty("");
    public final StringProperty txIDProperty = new SimpleStringProperty("");
    public final StringProperty rewardType = new SimpleStringProperty("");
    public boolean exportCointracking = false;

    public Long getBlockTime(){
        return blockTimeProperty.getValue();
    }

    public TransactionModel(Long blockTime, String owner, String type, String amounts, String blockHash, int blockHeight, String poolID, String txid,String rewardType, TransactionController transactionController) {
        this.blockTimeProperty.set(blockTime);
        this.ownerProperty.set(owner);
        this.typeProperty.set(type);
        this.amountProperty.set(amounts);
        this.blockHashProperty.set(blockHash);
        this.blockHeightProperty.set(blockHeight);
        this.cryptoValueProperty.set(Double.parseDouble(transactionController.splitCoinsAndAmounts(amounts)[0]));
        this.cryptoCurrencyProperty.set(transactionController.splitCoinsAndAmounts(amounts)[1]);
        if (this.amountProperty.getValue().split("@")[1].length() > 4 && (type.equals("RemovePoolLiquidity") || type.equals("AddPoolLiquidity"))) {
            this.poolIDProperty.set(ExportService.getIdFromPoolPair(this.amountProperty.getValue().split("@")[1]));
        } else {
            this.poolIDProperty.set(poolID);
        }
        this.txIDProperty.set(txid);
        this.rewardType.set(rewardType);
        this.fiatCurrencyProperty.set(transactionController.getSettingsController().selectedFiatCurrency.getValue());

        if (this.amountProperty.getValue().split("@")[1].length() == 3 | this.amountProperty.getValue().split("@")[1].length() == 4){
            this.fiatValueProperty.set(this.cryptoValueProperty.getValue() * transactionController.getCoinPriceController().getPriceFromTimeStamp(this.amountProperty.getValue().split("@")[1].equals("DUSD"),this.amountProperty.getValue().split("@")[1] + transactionController.getSettingsController().selectedFiatCurrency.getValue(), this.blockTimeProperty.getValue() * 1000L));
        }else{

            String poolRatioString = transactionController.getPoolRatio(transactionController.getIdFromPoolPair(this.amountProperty.getValue().split("@")[1]), "ab");
            if(!poolRatioString.equals("-")){
                double poolRatio = Double.parseDouble(poolRatioString);
                double token1 = Math.sqrt(poolRatio * Double.parseDouble(this.amountProperty.getValue().split("@")[0]) * Double.parseDouble(this.amountProperty.getValue().split("@")[0]));
                double token2 = Math.sqrt(Double.parseDouble(this.amountProperty.getValue().split("@")[0]) * Double.parseDouble(this.amountProperty.getValue().split("@")[0]) / poolRatio);

                Double price1 = transactionController.getCoinPriceController().getPriceFromTimeStamp(this.amountProperty.getValue().split("@")[1].contains("DUSD"), this.amountProperty.getValue().split("@")[1].split("-")[0] + SettingsController.getInstance().selectedFiatCurrency.getValue(), blockTime* 1000L) * token1;
                Double price2 = transactionController.getCoinPriceController().getPriceFromTimeStamp(this.amountProperty.getValue().split("@")[1].contains("DUSD"), this.amountProperty.getValue().split("@")[1].split("-")[1] + SettingsController.getInstance().selectedFiatCurrency.getValue(), blockTime* 1000L) * token2;
                this.fiatValueProperty.set(price1 + price2);
            }
            }
    }

}

