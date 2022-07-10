package models;

import controllers.CoinPriceController;
import controllers.SettingsController;
import controllers.TransactionController;
import javafx.beans.property.StringProperty;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.*;

public class TransactionModelTest {

    @Test
    public void constructor(){
        Long blockTime=0l;
        String owner = null;
        String type = null;
        String amounts="1@BTC";
        String blockHash ="";
        int blockHeight=0;
        String poolID="";
        String txId = null;
        String rewardType = null;
        TransactionController transactionController = mock(TransactionController.class);
        SettingsController settingsController = mock(SettingsController.class);
        StringProperty selectedFiatCurrency = mock(StringProperty.class);
        when(settingsController.selectedFiatCurrency).thenReturn(selectedFiatCurrency);
        CoinPriceController coinPriceController=mock(CoinPriceController.class);

        new TransactionModel(blockTime,owner,type,amounts,blockHash,blockHeight,txId, rewardType);
    }

}