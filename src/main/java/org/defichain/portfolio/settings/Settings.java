package org.defichain.portfolio.settings;

import org.defichain.portfolio.settings.properties.*;

import java.text.DecimalFormat;

public class Settings {
    private Languages language = Languages.ENGLISH;
    private Currency fiatCurrency = Currency.EURO;
    private DecimalFormat decimalFormat = new DecimalFormat("#.00");
    private CoinPairs coin = CoinPairs.BTC_DFI;
    private String plotCurrency= "Coin";
    private StyleMode styleMode;
    private String date;
    private String exportPath;
    private boolean showDisclaimer = true;
    private boolean launchDefid;
    private boolean launchSync;
    private Sources source = Sources.ACTIVE_WALLET;
    private String lastUpdate;
    private String exportCointrackerVariant;
    private String exportCsvVariant;
    private String exportFrom;
    private String exportTo;
    private boolean missingTransaction = true;
    private DataSource dataSource = DataSource.SHOW_OPTIONS;
    private String csvImportPath;
}
