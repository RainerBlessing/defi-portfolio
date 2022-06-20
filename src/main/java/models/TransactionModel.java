package models;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import controllers.CoinPriceController;
import javafx.beans.property.*;
import controllers.SettingsController;
import controllers.TransactionController;
import services.ExportService;

public class TransactionModel {
    private CoinPriceController coinPriceController;
    private SettingsController settingsController;
    private ExportService exportService;
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

    public TransactionModel(Long blockTime, String owner, String type, String amounts, String blockHash, int blockHeight, String txid,String rewardType) {
        this.blockTimeProperty.set(blockTime);
        this.ownerProperty.set(owner);
        this.typeProperty.set(type);
        this.amountProperty.set(amounts);
        this.blockHashProperty.set(blockHash);
        this.blockHeightProperty.set(blockHeight);

        this.txIDProperty.set(txid);
        this.rewardType.set(rewardType);
    }

    public void setCryptoValueProperty(double cryptoValue) {
        this.cryptoValueProperty.set(cryptoValue);
    }

    public void setCryptoCurrencyProperty(String cryptoCurrency) {
        this.cryptoCurrencyProperty.set(cryptoCurrency);
    }

    public void setPoolIDProperty(String poolID) {
        this.poolIDProperty.set(poolID);
    }

    public void setFiatCurrencyProperty(String fiatCurrency) {
        this.fiatCurrencyProperty.set(fiatCurrency);
    }

    public void setFiatValueProperty(double fiatValue) {
        this.fiatValueProperty.set(fiatValue);
    }
}

