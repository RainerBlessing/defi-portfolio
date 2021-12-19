package portfolio.models;

import javafx.beans.property.*;
import portfolio.controllers.TransactionController;
import portfolio.services.ExportService;

public class TransactionModel {

    public StringProperty ownerProperty = new SimpleStringProperty("");
    public IntegerProperty blockHeightProperty = new SimpleIntegerProperty(0);
    public StringProperty blockHashProperty = new SimpleStringProperty("");
    public LongProperty blockTimeProperty = new SimpleLongProperty(0L);
    public StringProperty typeProperty = new SimpleStringProperty("");
    public StringProperty poolIDProperty = new SimpleStringProperty("");
    public StringProperty amountProperty = new SimpleStringProperty("");
    public StringProperty cryptoCurrencyProperty = new SimpleStringProperty("");
    public DoubleProperty cryptoValueProperty = new SimpleDoubleProperty(0.0);
    public DoubleProperty fiatValueProperty = new SimpleDoubleProperty(0.0);
    public StringProperty fiatCurrencyProperty = new SimpleStringProperty("");
    public StringProperty txIDProperty = new SimpleStringProperty("");
    public StringProperty rewardType = new SimpleStringProperty("");
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
        if (this.amountProperty.getValue().split("@")[1].length() == 3 | this.amountProperty.getValue().split("@")[1].length() == 4)
            this.fiatValueProperty.set(this.cryptoValueProperty.getValue() * transactionController.getCoinPriceController().getPriceFromTimeStamp(this.amountProperty.getValue().split("@")[1].contains("DUSD"),this.amountProperty.getValue().split("@")[1] + transactionController.getSettingsController().selectedFiatCurrency.getValue(), this.blockTimeProperty.getValue() * 1000L));

    }

}

