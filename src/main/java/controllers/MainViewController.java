package controllers;

import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.javafx.charts.Legend;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.*;
import org.json.simple.JSONObject;
import resourceprovider.CssProvider;
import resourceprovider.Utilities;
import services.ExportService;
import views.MainView;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
@Singleton
public class MainViewController implements InvalidationListener {
    private final GuiModel guiModel;
    private SettingsController settingsController;
    public final StringProperty strCurrentBlockLocally = new SimpleStringProperty("0");
    public final StringProperty strCurrentBlockOnBlockchain = new SimpleStringProperty("No connection");
    public StringProperty strProgressbar = new SimpleStringProperty("");
    public final BooleanProperty bDataBase = new SimpleBooleanProperty(true);

    //Table and plot lists
    public final List<PoolPairModel> poolPairModelList = new ArrayList<>();
    public ObservableList<PoolPairModel> poolPairList;

    //Init all controller and services
    public final DonateController donateController = DonateController.getInstance();
    public HelpController helpController = HelpController.getInstance();
    public CoinPriceController coinPriceController;
    public TransactionController transactionController;
    public ExportService expService;
    public boolean updateSingleton = true;
    final Delta dragDelta = new Delta();
    public Process defidProcess;

    @Inject
    private GuiceFXMLLoader fxmlLoader;

    @Inject
    private Utilities utilities;

    public boolean init = true;

    @Inject
    public MainViewController(SettingsController settingsController, CoinPriceController coinPriceController, TransactionController transactionController, ExportService exportService, GuiModel guiModel) {
        this.settingsController = settingsController;
        this.coinPriceController= coinPriceController;
        this.transactionController = transactionController;
        this.settingsController.logger.info("Start DeFi-Portfolio");
        if (this.settingsController.selectedLaunchDefid) {
            if (this.transactionController.checkRpc()) this.transactionController.startServer();
        }

        this.guiModel = guiModel;

        this.startStockUpdate();

        // init all relevant lists for tables and plots
        this.poolPairList = FXCollections.observableArrayList(this.poolPairModelList);
        this.expService = exportService;
        this.coinPriceController.updateCoinPriceData();
        this.coinPriceController.updateStockPriceData();
        Object a = this.coinPriceController.stockPriceMap;
        this.transactionController.updateBalanceList();
        // get last block locally
        this.strCurrentBlockLocally.set(Integer.toString(transactionController.getLocalBlockCount()));

        //Add listener to Fiat
        this.settingsController.selectedFiatCurrency.addListener(
                (ov, t, t1) -> {
                    this.transactionController.getPortfolioList().clear();
                    for (TransactionModel transactionModel : this.transactionController.getTransactionList()) {
                        if (!transactionModel.cryptoCurrencyProperty.getValue().contains("-")) {
                            transactionModel.fiatCurrencyProperty.set(t1);
                            transactionModel.fiatValueProperty.set(transactionModel.cryptoValueProperty.getValue() * this.coinPriceController.getPriceFromTimeStamp(transactionModel.cryptoCurrencyProperty.getValue().contains("DUSD"),transactionModel.cryptoCurrencyProperty.getValue() + t1, transactionModel.blockTimeProperty.getValue() * 1000L));
                        }

                        if (transactionModel.typeProperty.getValue().equals("Rewards") | transactionModel.typeProperty.getValue().equals("Commission")) {
                            this.transactionController.addToPortfolioModel(transactionModel);
                        }
                    }
                    for (BalanceModel balanceModel : this.transactionController.getBalanceList()) {
                        balanceModel.setFiat1(balanceModel.getCrypto1Value() * coinPriceController.getPriceFromTimeStamp(balanceModel.getToken1NameValue().contains("DUSD"),balanceModel.getToken1NameValue() + settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis()));
                        balanceModel.setFiat2(balanceModel.getCrypto2Value() * coinPriceController.getPriceFromTimeStamp(balanceModel.getToken2NameValue().contains("DUSD"),balanceModel.getToken2NameValue() + settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis()));
                    }
                }
        );
        startTimer();

        updateLanguage();
    }

    public void startStockUpdate(){
        //Start Python update

        try {
            File f = new File(settingsController.DEFI_PORTFOLIO_HOME +"StockPricesPythonUpdate.portfolio");
            f.createNewFile();

        } catch (Exception e) {
            settingsController.logger.warning("Could not write python update file."); }

        try {
            // Start skript
            switch (this.settingsController.getPlatform()) {
                case "mac":
                    // defidProcess = Runtime.getRuntime().exec("/usr/bin/open -a Terminal " + System.getProperty("user.dir").replace("\\", "/") + "/PortfolioData/./" + "defi.sh");
                    Runtime.getRuntime().exec("/usr/bin/open -a Terminal " + settingsController.DEFI_PORTFOLIO_HOME + "StockTokenPrices");
                    break;
                case "win":
                    String path = "lib\\StockTokenPrices.exe";
                    String[] commands = {"cmd", "/c", "start", "\"Update Portfolio\"", path,settingsController.DEFI_PORTFOLIO_HOME};
                    defidProcess = Runtime.getRuntime().exec(commands);
                    break;
                case "linux":
                    String pathlinux = System.getProperty("user.dir")+"/defi-portfolio/src/portfolio/libraries/StockTokenPrices ";
                    settingsController.logger.warning(pathlinux +" '"+ settingsController.DEFI_PORTFOLIO_HOME+"'");
                    int notfound = 0;
                    try {
                        defidProcess = Runtime.getRuntime().exec("/usr/bin/x-terminal-emulator -e " + pathlinux + settingsController.DEFI_PORTFOLIO_HOME);
                    } catch (Exception e) {
                        notfound++;
                    }
                    try {
                        defidProcess = Runtime.getRuntime().exec("/usr/bin/konsole -e " + pathlinux + settingsController.DEFI_PORTFOLIO_HOME);
                    } catch (Exception e) {
                        notfound++;
                    }
                    if (notfound == 2) {
                        JOptionPane.showMessageDialog(null, "Could not found /usr/bin/x-terminal-emulator or\n /usr/bin/konsole", "Terminal not found", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
            }


        } catch (Exception e) {
            this.settingsController.logger.warning("Exception occurred: " + e);
        }



    }

    public void startTimer() {
        this.settingsController.timer.scheduleAtFixedRate(new TimerController(this, settingsController), 0, 15000);
    }

    public void copySelectedRawDataToClipboard(List<TransactionModel> list, boolean withHeaders) {
//        StringBuilder sb = new StringBuilder();
//        Locale localeDecimal = Locale.GERMAN;
//        if (settingsController.selectedDecimal.getValue().equals(".")) {
//            localeDecimal = Locale.US;
//        }
//
//        if (withHeaders) {
//            for (TableColumn column : this.mainView.rawDataTable.getColumns()
//            ) {
//                sb.append(column.getId()).append(this.settingsController.selectedSeparator.getValue());
//            }
//            sb.setLength(sb.length() - 1);
//            sb.append("\n");
//        }
//        for (TransactionModel transaction : list) {
//            sb.append(this.transactionController.convertTimeStampToString(transaction.blockTimeProperty.getValue())).append(this.settingsController.selectedSeparator.getValue());
//            sb.append(transaction.typeProperty.getValue()).append(this.settingsController.selectedSeparator.getValue());
//            String[] CoinsAndAmounts = this.transactionController.splitCoinsAndAmounts(transaction.amountProperty.getValue());
//            sb.append(String.format(localeDecimal, "%.8f", Double.parseDouble(CoinsAndAmounts[0]))).append(this.settingsController.selectedSeparator.getValue());
//            sb.append(CoinsAndAmounts[1]).append(this.settingsController.selectedSeparator.getValue());
//            sb.append(String.format(localeDecimal, "%.8f", transaction.fiatValueProperty.getValue())).append(this.settingsController.selectedSeparator.getValue());
//            sb.append(this.settingsController.selectedFiatCurrency.getValue()).append(this.settingsController.selectedSeparator.getValue());
//            sb.append(transaction.poolIDProperty.getValue()).append(this.settingsController.selectedSeparator.getValue());
//            sb.append(transaction.blockHeightProperty.getValue()).append(this.settingsController.selectedSeparator.getValue());
//            sb.append(transaction.blockHashProperty.getValue()).append(this.settingsController.selectedSeparator.getValue());
//            sb.append(transaction.ownerProperty.getValue()).append(this.settingsController.selectedSeparator.getValue());
//            sb.append(transaction.txIDProperty.getValue());
//            sb.append("\n");
//        }
//        StringSelection stringSelection = new StringSelection(sb.toString());
//        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//        clipboard.setContents(stringSelection, null);
    }

    public void copySelectedDataToClipboard(List<PoolPairModel> list, boolean withHeaders) {
//        StringBuilder sb = new StringBuilder();
//        Locale localeDecimal = Locale.GERMAN;
//        if (settingsController.selectedDecimal.getValue().equals(".")) {
//            localeDecimal = Locale.US;
//        }
//
//        if (withHeaders) {
//            switch (this.mainView.tabPane.getSelectionModel().getSelectedItem().getId()) {
//                case "Portfolio":
//                    sb.append((this.mainView.plotTable.getColumns().get(0).getId() + "," + this.mainView.plotTable.getColumns().get(2).getId() + "," + this.mainView.plotTable.getColumns().get(9).getId()).replace(",", this.settingsController.selectedSeparator.getValue())).append("\n");
//                    break;
//                case "Overview":
//                    sb.append((this.mainView.plotTable.getColumns().get(0).getId() + "," + this.mainView.plotTable.getColumns().get(1).getId() + "," + this.mainView.plotTable.getColumns().get(2).getId() + "," + this.mainView.plotTable.getColumns().get(3).getId() + "," + this.mainView.plotTable.getColumns().get(4).getId() + "," + this.mainView.plotTable.getColumns().get(5).getId() + "," + this.mainView.plotTable.getColumns().get(6).getId() + "," + this.mainView.plotTable.getColumns().get(7).getId() + "," + this.mainView.plotTable.getColumns().get(8).getId()).replace(",", this.settingsController.selectedSeparator.getValue())).append("\n");
//                    break;
//                case "Commissions":
//                    sb.append((this.mainView.plotTable.getColumns().get(0).getId() + "," + this.mainView.plotTable.getColumns().get(1).getId() + "," + this.mainView.plotTable.getColumns().get(2).getId() + "," + this.mainView.plotTable.getColumns().get(3).getId() + "," + this.mainView.plotTable.getColumns().get(4).getId() + "," + this.mainView.plotTable.getColumns().get(5).getId() + "," + this.mainView.plotTable.getColumns().get(8).getId()).replace(",", this.settingsController.selectedSeparator.getValue())).append("\n");
//                    break;
//                case "Rewards":
//                    sb.append((this.mainView.plotTable.getColumns().get(0).getId() + "," + this.mainView.plotTable.getColumns().get(1).getId() + "," + this.mainView.plotTable.getColumns().get(2).getId() + "," + this.mainView.plotTable.getColumns().get(3).getId()).replace(",", this.settingsController.selectedSeparator.getValue())).append("\n");
//                    break;
//                default:
//                    break;
//            }
//        }
//
//        for (PoolPairModel poolPair : list
//        ) {
//            switch (this.mainView.tabPane.getSelectionModel().getSelectedItem().getId()) {
//                case "Portfolio":
//                    sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(poolPair.getPoolPair().getValue()).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(poolPair.getBalanceFiat().getValue()).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append("\n");
//                    break;
//                case "Overview":
//                    sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(poolPair.getPoolPair().getValue()).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue1().getValue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoFiatValue1().getValue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue2().getValue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoFiatValue2().getValue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getcryptoCommission2Overviewvalue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getcryptoCommission2FiatOverviewvalue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getFiatValue().getValue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append("\n");
//                    break;
//                case "Rewards":
//                case "Belohnungen":
//                    sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(poolPair.getPoolPair().getValue()).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue1().getValue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoFiatValue1().getValue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append("\n");
//                    break;
//                case "Commissions":
//                case "Kommissionen":
//                    sb.append(poolPair.getBlockTime().getValue()).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(poolPair.getPoolPair().getValue()).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue1().getValue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoFiatValue1().getValue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoValue2().getValue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getCryptoFiatValue2().getValue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append(String.format(localeDecimal, "%.8f", poolPair.getFiatValue().getValue())).append(this.settingsController.selectedSeparator.getValue());
//                    sb.append("\n");
//                    break;
//                default:
//                    break;
//            }
//        }
//        StringSelection stringSelection = new StringSelection(sb.toString());
//        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//        clipboard.setContents(stringSelection, null);
    }

    public void updateTransactionData() {
        transactionController.clearTransactionList();
        transactionController.clearPortfolioList();
        transactionController.updateBalanceList();


        try {
            FileWriter myWriter = new FileWriter(settingsController.DEFI_PORTFOLIO_HOME +"update.portfolio");
            myWriter.write("<html><body>"+ settingsController.getTranslationValue("UpdateData") +" </body></html>");
            myWriter.close();
        } catch (IOException e) {
            settingsController.logger.warning("Could not write to update.portfolio."); }
        //Start Python update
        try {
            // Start skript
            switch (this.settingsController.getPlatform()) {
                case "mac":
                    // String pathMac = System.getProperty("user.dir")+"/src/portfolio/libraries/updatePortfolio ";
                    // Runtime.getRuntime().exec("/usr/bin/open -a Terminal " + pathMac + settingsController.DEFI_PORTFOLIO_HOME +" "+ settingsController.PORTFOLIO_CONFIG_FILE_PATH);
                    Runtime.getRuntime().exec("/usr/bin/open -a Terminal " + settingsController.DEFI_PORTFOLIO_HOME + "updatePortfolio");
                    break;
                case "win":
                    defidProcess = utilities.updatePortfolio();
                    break;
                case "linux":
                    String pathLinux = System.getProperty("user.dir")+"/defi-portfolio/src/portfolio/libraries/updatePortfolio ";
                    settingsController.logger.warning(pathLinux + settingsController.DEFI_PORTFOLIO_HOME +" "+ settingsController.PORTFOLIO_CONFIG_FILE_PATH);

                    int notfound = 0;
                    try {
                        defidProcess = Runtime.getRuntime().exec("/usr/bin/x-terminal-emulator -e " + pathLinux + settingsController.DEFI_PORTFOLIO_HOME +" "+ settingsController.PORTFOLIO_CONFIG_FILE_PATH);
                    } catch (Exception e) {
                        notfound++;
                    }
                    try {
                        defidProcess = Runtime.getRuntime().exec("/usr/bin/konsole -e " + pathLinux + settingsController.DEFI_PORTFOLIO_HOME +" "+ settingsController.PORTFOLIO_CONFIG_FILE_PATH);
                    } catch (Exception e) {
                        notfound++;
                    }
                    if (notfound == 2) {
                        JOptionPane.showMessageDialog(null, "Could not found /usr/bin/x-terminal-emulator or\n /usr/bin/konsole", "Terminal not found", JOptionPane.ERROR_MESSAGE);
                    }
                    break;
            }


        } catch (Exception e) {
            this.settingsController.logger.warning("Exception occurred: " + e);
        }


        try {
            File f = new File(settingsController.DEFI_PORTFOLIO_HOME + "pythonUpdate.portfolio");
            f.createNewFile();

        } catch (Exception e) {
            settingsController.logger.warning("Could not write python update file."); }

       /* if (new File(this.settingsController.DEFI_PORTFOLIO_HOME + this.settingsController.strTransactionData).exists()) {
            int depth = Integer.parseInt(this.transactionController.getBlockCount()) - this.transactionController.getLocalBlockCount();
            return transactionController.updateTransactionData(depth);
        } else {
            return transactionController.updateTransactionData(Integer.parseInt(transactionController.getBlockCount()));
        }
*/

    }

    public void finishedUpdate(){
//        try {
//            FileWriter myWriter = new FileWriter(settingsController.DEFI_PORTFOLIO_HOME + "update.portfolio");
//            myWriter.write("<html><body>"+ settingsController.getTranslationValue("PreparingData") +"</body></html>");
//            myWriter.close();
//        } catch (IOException e) {
//            settingsController.logger.warning("Could not write to update.portfolio."); }
//
//        transactionController.updateTransactionList(this.transactionController.getLocalTransactionList());
//        int localBlockCount = this.transactionController.getLocalBlockCount();
//        int blockCount = Integer.parseInt(this.transactionController.getBlockCount());
//        this.strCurrentBlockLocally.set(Integer.toString(localBlockCount));
//        if (localBlockCount > blockCount) {
//            this.strCurrentBlockOnBlockchain.set(Integer.toString(localBlockCount));
//        } else {
//            this.strCurrentBlockOnBlockchain.set(Integer.toString(blockCount));
//        }
//        this.transactionController.calcImpermanentLoss();
//        Date date = new Date(System.currentTimeMillis());
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
//        this.settingsController.lastUpdate.setValue(dateFormat.format(date));
//        this.settingsController.saveSettings();
//        this.bDataBase.setValue(this.updateSingleton = true);
//        this.plotUpdate(this.mainView.tabPane.getSelectionModel().getSelectedItem().getId());
//        File file = new File(settingsController.DEFI_PORTFOLIO_HOME +  "update.portfolio");
//        if (file.exists()) file.delete();
//        transactionController.ps.destroy();
    }

    public void btnUpdateDatabasePressed() {
        if (this.updateSingleton) {
            this.bDataBase.setValue(this.updateSingleton = false);
            updateTransactionData();
        }

    }

    public void plotUpdate(String openedTab) {
        switch (openedTab) {
            case "Portfolio":
                updatePortfolio();
                break;
            case "Overview":
            case "Übersicht":
                updateOverview();
                break;
            case "Rewards":
            case "Belohnungen":
                updateRewards();
                break;
            case "Commissions":
            case "Kommissionen":
                updateCommissions();
                break;
            default:
                break;
        }
    }

    private String getColor(String tokenName) {
        switch (tokenName) {
            case "DFI":
                tokenName = "#FF00AF";
                break;
            case "ETH-DFI":
            case "ETH":
                tokenName = "#14044d";
                break;
            case "BTC":
            case "BTC-DFI":
                tokenName = "#f7931a";
                break;
            case "USDT":
            case "USDT-DFI":
                tokenName = "#0ecc8d";
                break;
            case "DOGE":
            case "DOGE-DFI":
                tokenName = "#cb9800";
                break;
            case "LTC":
            case "LTC-DFI":
                tokenName = "#00aeff";
                break;
            case "BCH":
            case "BCH-DFI":
                tokenName = "#478559";
                break;
            case "USDC":
            case "USDC-DFI":
                tokenName = "#2775CA";
                break;
            default:
                tokenName = "-";
                break;
        }
        return tokenName;
    }

    private void updatePortfolio() {


        this.poolPairModelList.clear();
        this.poolPairList.clear();

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        ObservableList<PieChart.Data> pieChartData2 = FXCollections.observableArrayList();
        String currency = "\u20ac";
        if (settingsController.selectedFiatCurrency.getValue().equals("USD")) {
            currency = "\u0024";
        } else if (settingsController.selectedFiatCurrency.getValue().equals("CHF")) {
            currency = "CHF";
        }
        double calculatedPortfolio = 0.0;
        double calculatedPortfolio2 = 0.0;
        Locale localeDecimal = Locale.GERMAN;
        if (this.settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }
        for (BalanceModel balanceModel : this.transactionController.getBalanceList()) {
            if ((balanceModel.getToken1NameValue().equals("DUSD") || balanceModel.getToken2NameValue().equals("DUSD")) &&  !balanceModel.getToken2NameValue().equals("-")) {
                double factor = 1.0;

                if(!settingsController.selectedFiatCurrency.getValue().contains("USD")) factor = this.transactionController.getCurrencyFactor();
                balanceModel.setFiat1(factor*balanceModel.getCrypto1Value()*Double.parseDouble(this.transactionController.getPrice(balanceModel.getToken1NameValue()+"-"+balanceModel.getToken2NameValue())));
                balanceModel.setFiat2(factor*balanceModel.getCrypto2Value());
            }

            if (balanceModel.getToken2NameValue().equals("-")) {
                pieChartData.add(new PieChart.Data(balanceModel.getToken1NameValue(), balanceModel.getFiat1Value()));
                this.poolPairModelList.add(new PoolPairModel(balanceModel.getToken1NameValue() + " (" + String.format(localeDecimal, "%1.2f", coinPriceController.getPriceFromTimeStamp(balanceModel.getToken2NameValue().equals("DUSD"),balanceModel.getToken1NameValue() + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis())) + currency + ")", 0.0, 0.0, 0.0, String.format(localeDecimal, "%1.8f", balanceModel.getCrypto1Value()), 0.0, 0.0, 0.0, 0.0, String.format(localeDecimal, "%,.2f", balanceModel.getFiat1Value())));
                calculatedPortfolio += balanceModel.getFiat1Value() + balanceModel.getFiat2Value();

            } else {
                pieChartData2.add(new PieChart.Data(balanceModel.getToken1NameValue() + "-" + balanceModel.getToken2NameValue(), balanceModel.getFiat1Value() + balanceModel.getFiat2Value()));
                this.poolPairModelList.add(new PoolPairModel(balanceModel.getToken1NameValue() + "-" + balanceModel.getToken2NameValue() + " (" + String.format(localeDecimal, "%1.2f", (balanceModel.getFiat1Value() + balanceModel.getFiat2Value()) / balanceModel.getShareValue()) + currency + ")", 0.0, 0.0, 0.0,
                        String.format(localeDecimal, "%1.8f", balanceModel.getShareValue()) + " (" + String.format(localeDecimal, "%1.8f", balanceModel.getCrypto1Value()) + " " + balanceModel.getToken1NameValue() + " + " + String.format(localeDecimal, "%1.8f", balanceModel.getCrypto2Value()) + balanceModel.getToken2NameValue() + ")",
                        0.0, 0.0, 0.0, 0.0, String.format(localeDecimal, "%,.2f", balanceModel.getFiat1Value() + balanceModel.getFiat1Value()) + " (" + String.format(localeDecimal, "%,.2f", balanceModel.getFiat1Value()) + " " + balanceModel.getToken1NameValue() + " + " + String.format(localeDecimal, "%,.2f", balanceModel.getFiat2Value()) + balanceModel.getToken2NameValue() + ")"));
                calculatedPortfolio2 += balanceModel.getFiat1Value() + balanceModel.getFiat2Value();
            }

        }

        double totalYield = 0;
        double totalYieldRewards = 0;
        double totalYieldCommissions = 0;

        for (String poolPair : this.settingsController.cryptoCurrencies) {

            double poolPair1Price = coinPriceController.getPriceFromTimeStamp(poolPair.contains("DUSD"),poolPair.split("-")[1] + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
            double poolPair2Price = coinPriceController.getPriceFromTimeStamp(poolPair.contains("DUSD"),poolPair.split("-")[0] + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());

            if (this.transactionController.getPortfolioList().containsKey(poolPair + "-" + this.settingsController.selectedIntervalInt)) {
                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(poolPair + "-" + this.settingsController.selectedIntervalInt).entrySet()) {
                    totalYield += (entry.getValue().getCoinCommissions1Value() * poolPair1Price) + (entry.getValue().getCoinCommissions2Value() * poolPair2Price) + (entry.getValue().getCoinRewards1Value() * poolPair1Price);
                    totalYieldRewards += entry.getValue().getCoinRewards1Value() * poolPair1Price;
                    totalYieldCommissions += (entry.getValue().getCoinCommissions1Value() * poolPair1Price) + (entry.getValue().getCoinCommissions2Value() * poolPair2Price);
                }
            }
        }

        this.settingsController.tokenYield.set(settingsController.getTranslationValue("TotalYield") + ":\n" + String.format(localeDecimal, "%,.2f", totalYield) + currency);
        this.settingsController.tokenYieldRewards.set(this.settingsController.getTranslationValue("TotalYieldRewards") + ":\n" + String.format(localeDecimal, "%,.2f", totalYieldRewards) + currency);
        this.settingsController.tokenYieldCommissions.set(this.settingsController.getTranslationValue("TotalYieldCommissions") + ":\n" + String.format(localeDecimal, "%,.2f", totalYieldCommissions) + currency);
        this.settingsController.tokenAmount.set(this.settingsController.getTranslationValue("TotalAmount") + ":\n" + String.format(localeDecimal, "%,.2f", calculatedPortfolio + calculatedPortfolio2) + currency);
        this.settingsController.tokenBalance.set("Token:\n" + String.format(localeDecimal, "%,.2f", calculatedPortfolio) + currency);
        this.settingsController.tokenBalanceLM.set("LM Token:\n" + String.format(localeDecimal, "%,.2f", calculatedPortfolio2) + currency);
//        this.mainView.plotPortfolio1.setData(pieChartData);
//        this.mainView.plotPortfolio11.setData(pieChartData2);
//
//
//        for (PieChart.Data data : this.mainView.plotPortfolio1.getData()
//        ) {
//            if(!getColor(data.getName()).equals("-"))  data.getNode().setStyle("-fx-pie-color: " + getColor(data.getName()) + ";");
//        }
//
//        for (Node n : this.mainView.plotPortfolio1.getChildrenUnmodifiable()
//        ) {
//            if (n instanceof Legend) {
//                for (Legend.LegendItem legendItem : ((Legend) n).getItems()) {
//                    if(!getColor(legendItem.getText()).equals("-")) legendItem.getSymbol().setStyle("-fx-background-color: " + getColor(legendItem.getText()) + ";");
//                }
//            }
//        }
//
//        for (PieChart.Data data : this.mainView.plotPortfolio11.getData()
//        ) {
//           if(!getColor(data.getName()).equals("-")) data.getNode().setStyle("-fx-pie-color: " + getColor(data.getName()) + ";");
//        }
//
//        for (Node n : this.mainView.plotPortfolio11.getChildrenUnmodifiable()
//        ) {
//            if (n instanceof Legend) {
//                for (Legend.LegendItem legendItem : ((Legend) n).getItems()) {
//                  if(!getColor(legendItem.getText()).equals("-"))  legendItem.getSymbol().setStyle("-fx-background-color: " + getColor(legendItem.getText()) + ";");
//                }
//            }
//        }
//
//        this.mainView.plotPortfolio1.getData().forEach(data -> {
//            Tooltip toolTip = new Tooltip(String.format("%1.2f", data.getPieValue()) + " " + settingsController.selectedFiatCurrency.getValue());
//            Tooltip.install(data.getNode(), toolTip);
//        });
//
//        this.mainView.plotPortfolio11.getData().forEach(data -> {
//            Tooltip toolTip = new Tooltip(String.format("%1.2f", data.getPieValue()) + " " + settingsController.selectedFiatCurrency.getValue());
//            Tooltip.install(data.getNode(), toolTip);
//        });
//
//        this.poolPairModelList.sort(Comparator.comparing(PoolPairModel::getBlockTimeValue));
//
//        this.poolPairList.clear();
//
//        if (poolPairModelList.size() > 0 & transactionController.impermanentLossList.size()>0) {
//
//            // add Impermanent Loss
//            this.poolPairModelList.add(new PoolPairModel("", 0.0, 0.0, 0.0, "", 0.0, 0.0, 0.0, 0.0, ""));
//            this.poolPairModelList.add(new PoolPairModel("Impermanent Loss", 0.0, 0.0, 0.0, "Value input coins" + " (" + settingsController.selectedFiatCurrency.getValue() + ")", 0.0, 0.0, 0.0, 0.0, "Value current coins" + " (" + settingsController.selectedFiatCurrency.getValue() + ")"));
//
//            double currentDFIPrice = coinPriceController.getPriceFromTimeStamp(false,"DFI" + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//            double currentDUSDPrice = coinPriceController.getPriceFromTimeStamp(true,"DUSD"+ this.settingsController.selectedFiatCurrency.getValue(),System.currentTimeMillis());
//            double currentCoin1Price  = currentDFIPrice;
//            TreeMap<String, ImpermanentLossModel> ilList = transactionController.impermanentLossList;
//            double inputTotal = 0.0;
//            double currentTotal = 0.0;
//            for (String key : ilList.keySet()) {
//                double currentCoin2Price = coinPriceController.getPriceFromTimeStamp(key.contains("DUSD"),key.split("-")[0] + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//
//                if(key.split("-")[1].equals("DFI")) {
//                    currentCoin1Price = currentDFIPrice;
//                }else{
//                    currentCoin1Price = currentDUSDPrice;
//                }
//
//                double valueInputCoins = currentCoin1Price * ilList.get(key).PoolCoin1 + currentCoin2Price * ilList.get(key).PoolCoin2;
//                double valuePool = 0.0;
//                for (BalanceModel balanceModel : this.transactionController.getBalanceList()) {
//                    if (!balanceModel.getToken2NameValue().equals("-")) {
//                        if (key.split("-")[0].equals(balanceModel.getToken1Name().getValue())) {
//                            valuePool = balanceModel.getFiat1Value() + balanceModel.getFiat2Value();
//                        }
//                    }
//                }
//
//                String lossValueString = "";
//                String valuePoolString = "";
//
//                if(valuePool == 0){
//                    lossValueString = "-";
//                    valuePoolString = "-";
//                }else{
//                    double lossValue = ((valuePool / valueInputCoins) - 1) * 100;
//                    if(lossValue > 0) {
//                        lossValue = lossValue * -1;
//                    }
//                    inputTotal += valueInputCoins;
//                    currentTotal += valuePool;
//
//                    lossValueString=  String.format(localeDecimal, "%,.2f", lossValue) + "%";
//                    valuePoolString= String.format(localeDecimal, "%,1.2f", valuePool);
//                }
//                this.poolPairModelList.add(new PoolPairModel(lossValueString + " (" + key + ")", 0.0, 0.0, 0.0, String.format(localeDecimal, "%,1.2f", valueInputCoins), 0.0, 0.0, 0.0, 0.0, valuePoolString));
//            }
//
//            this.poolPairModelList.add(new PoolPairModel(String.format(localeDecimal, "%,.2f", ((currentTotal / inputTotal) - 1) * 100) + "%" + " (Total)", 0.0, 0.0, 0.0, String.format(localeDecimal, "%,1.2f", inputTotal), 0.0, 0.0, 0.0, 0.0, String.format(localeDecimal, "%,1.2f", currentTotal)));
//            this.poolPairList.addAll(this.poolPairModelList);
//
//        }
    }


    public void updateOverview() {
//        try {
//
//            this.poolPairModelList.clear();
//            this.mainView.plotOverview.setLegendVisible(true);
//            this.mainView.plotOverview.getData().clear();
//            this.mainView.plotOverview.getYAxis().setLabel("Total (" + this.settingsController.selectedFiatCurrency.getValue() + ")");
//            double maxValue = 0;
//
//            for (String poolPair : this.settingsController.cryptoCurrencies) {
//
//                XYChart.Series<Number, Number> overviewSeries = new XYChart.Series();
//                overviewSeries.setName(poolPair);
//
//                if (this.transactionController.getPortfolioList().containsKey(poolPair + "-" + this.settingsController.selectedIntervalInt)) {
//
//                    for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(poolPair + "-" + this.settingsController.selectedIntervalInt).entrySet()) {
//                        if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervalInt)) >= 0 &&
//                                entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervalInt)) <= 0) {
//
//                            if (poolPair.equals(entry.getValue().getPoolPairValue())) {
//                                overviewSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatRewards1Value() + entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value()));
//                                this.poolPairModelList.add(new PoolPairModel(entry.getKey(), entry.getValue().getFiatRewards1Value() + entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), entry.getValue().getCoinRewards().getValue(), entry.getValue().getCoinCommissions1Value(), poolPair, entry.getValue().getFiatRewards1Value(), entry.getValue().getFiatCommissions1Value(), entry.getValue().getCoinCommissions2Value(), entry.getValue().getFiatCommissions2Value(), ""));
//                            }
//                        }
//                    }
//
//                    this.mainView.yAxis.setAutoRanging(false);
//
//                    if (overviewSeries.getData().size() > 0) {
//                        maxValue += overviewSeries.getData().stream().mapToDouble(d -> (Double) d.getYValue()).max().getAsDouble();
//                        this.mainView.yAxis.setUpperBound(maxValue * 1.1);
//                        this.mainView.plotOverview.getData().add(overviewSeries);
//                        this.mainView.plotOverview.setCreateSymbols(true);
//                    }
//                }
//
//            }
//            for (XYChart.Series<Number, Number> s : this.mainView.plotOverview.getData()) {
//                if (s != null) {
//                    for (XYChart.Data d : s.getData()) {
//                        if (d != null) {
//                            Tooltip t = new Tooltip(d.getYValue().toString());
//                            Tooltip.install(d.getNode(), t);
//                            d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
//                            d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
//                        }
//                    }
//                }
//            }
//
//            this.poolPairModelList.sort(Comparator.comparing(PoolPairModel::getBlockTimeValue));
//            this.poolPairList.clear();
//            this.poolPairList.addAll(this.poolPairModelList);
//
//        } catch (Exception e) {
//            this.settingsController.logger.warning(e.toString());
//        }
    }

    public void updateRewards() {

//        XYChart.Series<Number, Number> rewardsSeries = new XYChart.Series();
//        this.poolPairModelList.clear();
//        this.mainView.plotRewards.setLegendVisible(false);
//        this.mainView.plotRewards.getData().clear();
//
//        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
//            this.mainView.plotRewards.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1]);
//        } else if (this.settingsController.selectedPlotCurrency.getValue().equals("Daily Fiat") || this.settingsController.selectedPlotCurrency.getValue().equals("Current Fiat")) {
//            this.mainView.plotRewards.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1] + " (" + this.settingsController.selectedFiatCurrency.getValue() + ")");
//        }
//
//        if (this.transactionController.getPortfolioList().containsKey(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervalInt)) {
//
//            if (this.settingsController.selectedPlotType.getValue().equals(settingsController.getTranslationValue("Individual"))) {
//
//                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervalInt).entrySet()) {
//
//                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervalInt)) >= 0 &&
//                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervalInt)) <= 0) {
//
//                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
//                            rewardsSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinRewards1Value()));
//                        } else if (this.settingsController.selectedPlotCurrency.getValue().equals("Daily Fiat")) {
//                            rewardsSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatRewards1Value()));
//                        } else if (this.settingsController.selectedPlotCurrency.getValue().equals("Current Fiat")) {
//                            double currentDFIPrice = coinPriceController.getPriceFromTimeStamp(false,"DFI" + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//                            rewardsSeries.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinRewards1Value() * currentDFIPrice));
//                        }
//
//                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Current Fiat")) {
//                            double currentDFIPrice = coinPriceController.getPriceFromTimeStamp(false,"DFI" + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//                            this.poolPairModelList.add(new PoolPairModel(entry.getKey(), 1, entry.getValue().getCoinRewards1Value(), 1, this.settingsController.selectedCoin.getValue(), entry.getValue().getCoinRewards1Value() * currentDFIPrice, 1, 1.0, 1, ""));
//                        } else {
//                            this.poolPairModelList.add(new PoolPairModel(entry.getKey(), 1, entry.getValue().getCoinRewards1Value(), 1, this.settingsController.selectedCoin.getValue(), entry.getValue().getFiatRewards1Value(), 1, 1.0, 1, ""));
//                        }
//                    }
//                }
//
//
//                if (this.mainView.plotRewards.getData().size() == 1) {
//                    this.mainView.plotRewards.getData().remove(0);
//                }
//
//                this.mainView.plotRewards.getData().add(rewardsSeries);
//
//                for (XYChart.Series<Number, Number> s : this.mainView.plotRewards.getData()) {
//                    for (XYChart.Data d : s.getData()) {
//                        Tooltip t = new Tooltip(d.getYValue().toString());
//                        Tooltip.install(d.getNode(), t);
//                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
//                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
//                    }
//                }
//
//            } else {
//
//                XYChart.Series<Number, Number> rewardsCumulated = new XYChart.Series();
//
//                double cumulatedCoinValue = 0;
//                double cumulatedFiatValue = 0;
//
//                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervalInt).entrySet()) {
//                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervalInt)) >= 0 &&
//                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervalInt)) <= 0) {
//
//                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
//                            cumulatedCoinValue = cumulatedCoinValue + entry.getValue().getCoinRewards1Value();
//                            rewardsCumulated.getData().add(new XYChart.Data(entry.getKey(), cumulatedCoinValue));
//                        } else if (this.settingsController.selectedPlotCurrency.getValue().equals("Daily Fiat")) {
//                            cumulatedFiatValue = cumulatedFiatValue + entry.getValue().getFiatRewards1Value();
//                            rewardsCumulated.getData().add(new XYChart.Data(entry.getKey(), cumulatedFiatValue));
//                        } else if (this.settingsController.selectedPlotCurrency.getValue().equals("Current Fiat")) {
//                            double currentDFIPrice = coinPriceController.getPriceFromTimeStamp(false,"DFI" + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//                            cumulatedFiatValue = cumulatedFiatValue + (entry.getValue().getCoinRewards1Value() * currentDFIPrice);
//                            rewardsCumulated.getData().add(new XYChart.Data(entry.getKey(), cumulatedFiatValue));
//                        }
//                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Current Fiat")) {
//                            double currentDFIPrice = coinPriceController.getPriceFromTimeStamp(false,"DFI" + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//                            this.poolPairModelList.add(new PoolPairModel(entry.getKey(), 1, entry.getValue().getCoinRewards1Value(), 1, this.settingsController.selectedCoin.getValue(), entry.getValue().getCoinRewards1Value() * currentDFIPrice, 1, 1.0, 1, ""));
//                        } else {
//                            this.poolPairModelList.add(new PoolPairModel(entry.getKey(), 1, entry.getValue().getCoinRewards1Value(), 1, this.settingsController.selectedCoin.getValue(), entry.getValue().getFiatRewards1Value(), 1, 1.0, 1, ""));
//                        }
//                    }
//                }
//                if (this.mainView.plotRewards.getData().size() == 1) {
//                    this.mainView.plotRewards.getData().remove(0);
//                }
//
//                this.mainView.plotRewards.getData().add(rewardsCumulated);
//
//                for (XYChart.Series<Number, Number> s : this.mainView.plotRewards.getData()) {
//                    for (XYChart.Data d : s.getData()) {
//                        Tooltip t = new Tooltip(d.getYValue().toString());
//                        Tooltip.install(d.getNode(), t);
//                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
//                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
//                    }
//                }
//            }
//        }
//
//        this.poolPairModelList.sort(Comparator.comparing(PoolPairModel::getBlockTimeValue));
//        this.poolPairList.clear();
//        this.poolPairList.addAll(this.poolPairModelList);
    }


    public void updateCommissions() {

//        XYChart.Series<Number, Number> commissionsSeries1 = new XYChart.Series();
//        XYChart.Series<Number, Number> commissionsSeries2 = new XYChart.Series();
//        this.mainView.plotCommissions1.getData().clear();
//        this.mainView.plotCommissions2.getData().clear();
//        this.poolPairModelList.clear();
//        this.poolPairList.clear();
//        this.mainView.plotCommissions1.setLegendVisible(false);
//        this.mainView.plotCommissions2.setLegendVisible(false);
//
//        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
//            this.mainView.plotCommissions1.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1]);
//            this.mainView.plotCommissions2.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[0]);
//        } else if (this.settingsController.selectedPlotCurrency.getValue().equals("Daily Fiat") || this.settingsController.selectedPlotCurrency.getValue().equals("Current Fiat")) {
//            this.mainView.plotCommissions1.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[1] + " (" + this.settingsController.selectedFiatCurrency.getValue() + ")");
//            this.mainView.plotCommissions2.getYAxis().setLabel(this.settingsController.selectedCoin.getValue().split("-")[0] + " (" + this.settingsController.selectedFiatCurrency.getValue() + ")");
//        }
//
//        if (this.transactionController.getPortfolioList().containsKey(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervalInt)) {
//
//            if (this.settingsController.selectedPlotType.getValue().equals(this.settingsController.getTranslationValue("Individual"))) {
//
//                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervalInt).entrySet()) {
//                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervalInt)) >= 0 &&
//                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervalInt)) <= 0) {
//
//                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
//                            commissionsSeries1.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinCommissions1Value()));
//                            commissionsSeries2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinCommissions2Value()));
//                        } else if (this.settingsController.selectedPlotCurrency.getValue().equals("Daily Fiat")) {
//                            commissionsSeries1.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatCommissions1Value()));
//                            commissionsSeries2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getFiatCommissions2Value()));
//                        } else if (this.settingsController.selectedPlotCurrency.getValue().equals("Current Fiat")) {
//                            double poolPair1CurrentPrice = coinPriceController.getPriceFromTimeStamp(this.settingsController.selectedCoin.getValue().contains("DUSD"),this.settingsController.selectedCoin.getValue().split("-")[1] + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//                            double poolPair2CurrentPrice = coinPriceController.getPriceFromTimeStamp(this.settingsController.selectedCoin.getValue().contains("DUSD"),this.settingsController.selectedCoin.getValue().split("-")[0] + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//
//                            commissionsSeries1.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinCommissions1Value() * poolPair1CurrentPrice));
//                            commissionsSeries2.getData().add(new XYChart.Data(entry.getKey(), entry.getValue().getCoinCommissions2Value() * poolPair2CurrentPrice));
//                        }
//
//                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Current Fiat")) {
//                            double poolPair1CurrentPrice = coinPriceController.getPriceFromTimeStamp(this.settingsController.selectedCoin.getValue().split("-")[1].equals("DUSD"),this.settingsController.selectedCoin.getValue().split("-")[1] + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//                            double poolPair2CurrentPrice = coinPriceController.getPriceFromTimeStamp(this.settingsController.selectedCoin.getValue().split("-")[0].equals("DUSD"),this.settingsController.selectedCoin.getValue().split("-")[0] + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//
//                            this.poolPairModelList.add(new PoolPairModel(entry.getKey(), (entry.getValue().getCoinCommissions1Value() * poolPair1CurrentPrice) + (entry.getValue().getCoinCommissions2Value() * poolPair2CurrentPrice), entry.getValue().getCoinCommissions1Value(), entry.getValue().getCoinCommissions2Value(), this.settingsController.selectedCoin.getValue(), entry.getValue().getCoinCommissions1Value() * poolPair1CurrentPrice, entry.getValue().getCoinCommissions2Value() * poolPair2CurrentPrice, 1.0, 1, ""));
//                        } else {
//                            this.poolPairModelList.add(new PoolPairModel(entry.getKey(), entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), entry.getValue().getCoinCommissions1Value(), entry.getValue().getCoinCommissions2Value(), this.settingsController.selectedCoin.getValue(), entry.getValue().getFiatCommissions1Value(), entry.getValue().getFiatCommissions2Value(), 1.0, 1, ""));
//                        }
//                    }
//                }
//
//                this.mainView.plotCommissions1.getData().add(commissionsSeries1);
//                this.mainView.plotCommissions2.getData().add(commissionsSeries2);
//
//                for (XYChart.Series<Number, Number> s : this.mainView.plotCommissions1.getData()) {
//                    for (XYChart.Data d : s.getData()) {
//                        Tooltip t = new Tooltip(d.getYValue().toString());
//                        Tooltip.install(d.getNode(), t);
//                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
//                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
//                    }
//                }
//
//                for (XYChart.Series<Number, Number> s : this.mainView.plotCommissions2.getData()) {
//                    for (XYChart.Data d : s.getData()) {
//                        Tooltip t = new Tooltip(d.getYValue().toString());
//                        Tooltip.install(d.getNode(), t);
//                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
//                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
//                    }
//                }
//
//            } else {
//
//                XYChart.Series<Number, Number> rewardsCumulated1 = new XYChart.Series();
//                XYChart.Series<Number, Number> rewardsCumulated2 = new XYChart.Series();
//
//                double cumulatedCommissions1CoinValue = 0;
//                double cumulatedCommissions1FiatValue = 0;
//                double cumulatedCommissions2CoinValue = 0;
//                double cumulatedCommissions2FiatValue = 0;
//                for (HashMap.Entry<String, PortfolioModel> entry : this.transactionController.getPortfolioList().get(this.settingsController.selectedCoin.getValue() + "-" + this.settingsController.selectedIntervalInt).entrySet()) {
//                    if (entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateFrom.getValue().toString(), this.settingsController.selectedIntervalInt)) >= 0 &&
//                            entry.getValue().getDateValue().compareTo(this.transactionController.convertDateToIntervall(this.settingsController.dateTo.getValue().toString(), this.settingsController.selectedIntervalInt)) <= 0) {
//                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Coin")) {
//                            cumulatedCommissions1CoinValue = cumulatedCommissions1CoinValue + entry.getValue().getCoinCommissions1Value();
//                            cumulatedCommissions2CoinValue = cumulatedCommissions2CoinValue + entry.getValue().getCoinCommissions2Value();
//                            rewardsCumulated1.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions1CoinValue));
//                            rewardsCumulated2.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions2CoinValue));
//                        } else if (this.settingsController.selectedPlotCurrency.getValue().equals("Daily Fiat")) {
//                            cumulatedCommissions1FiatValue = cumulatedCommissions1FiatValue + entry.getValue().getFiatCommissions1Value();
//                            cumulatedCommissions2FiatValue = cumulatedCommissions2FiatValue + entry.getValue().getFiatCommissions2Value();
//                            rewardsCumulated1.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions1FiatValue));
//                            rewardsCumulated2.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions2FiatValue));
//                        } else if (this.settingsController.selectedPlotCurrency.getValue().equals("Current Fiat")) {
//                            double poolPair1CurrentPrice = coinPriceController.getPriceFromTimeStamp(this.settingsController.selectedCoin.getValue().contains("DUSD"),this.settingsController.selectedCoin.getValue().split("-")[1] + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//                            double poolPair2CurrentPrice = coinPriceController.getPriceFromTimeStamp(this.settingsController.selectedCoin.getValue().contains("DUSD"),this.settingsController.selectedCoin.getValue().split("-")[0] + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//
//                            cumulatedCommissions1FiatValue = cumulatedCommissions1FiatValue + (entry.getValue().getCoinCommissions1Value() * poolPair1CurrentPrice);
//                            cumulatedCommissions2FiatValue = cumulatedCommissions2FiatValue + (entry.getValue().getCoinCommissions2Value() * poolPair2CurrentPrice);
//                            rewardsCumulated1.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions1FiatValue));
//                            rewardsCumulated2.getData().add(new XYChart.Data(entry.getKey(), cumulatedCommissions2FiatValue));
//                        }
//                        if (this.settingsController.selectedPlotCurrency.getValue().equals("Current Fiat")) {
//                            double currentDFIPrice = coinPriceController.getPriceFromTimeStamp(false,"DFI" + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//                            double currentPairPrice = coinPriceController.getPriceFromTimeStamp(this.settingsController.selectedCoin.getValue().contains("DUSD"),this.settingsController.selectedCoin.getValue().split("-")[0] + this.settingsController.selectedFiatCurrency.getValue(), System.currentTimeMillis());
//
//                            this.poolPairModelList.add(new PoolPairModel(entry.getKey(), (entry.getValue().getFiatCommissions1Value() * currentDFIPrice) + (entry.getValue().getFiatCommissions2Value() * currentPairPrice), entry.getValue().getCoinCommissions1Value(), entry.getValue().getCoinCommissions2Value(), this.settingsController.selectedCoin.getValue(), entry.getValue().getCoinCommissions1Value() * currentDFIPrice, entry.getValue().getCoinCommissions2Value() * currentPairPrice, 1.0, 1, ""));
//                        } else {
//                            this.poolPairModelList.add(new PoolPairModel(entry.getKey(), entry.getValue().getFiatCommissions1Value() + entry.getValue().getFiatCommissions2Value(), entry.getValue().getCoinCommissions1Value(), entry.getValue().getCoinCommissions2Value(), this.settingsController.selectedCoin.getValue(), entry.getValue().getFiatCommissions1Value(), entry.getValue().getFiatCommissions2Value(), 1.0, 1, ""));
//                        }
//                    }
//                }
//
//                if (this.mainView.plotCommissions1.getData().size() == 1) {
//                    this.mainView.plotCommissions1.getData().remove(0);
//                }
//
//                if (this.mainView.plotCommissions2.getData().size() == 1) {
//                    this.mainView.plotCommissions2.getData().remove(0);
//                }
//
//                this.mainView.plotCommissions1.getData().add(rewardsCumulated1);
//                for (XYChart.Series<Number, Number> s : this.mainView.plotCommissions1.getData()) {
//                    for (XYChart.Data d : s.getData()) {
//                        Tooltip t = new Tooltip(d.getYValue().toString());
//                        //t.setShowDelay(Duration.seconds(0));
//                        Tooltip.install(d.getNode(), t);
//                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
//                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
//                    }
//                }
//
//                this.mainView.plotCommissions2.getData().add(rewardsCumulated2);
//                for (XYChart.Series<Number, Number> s : this.mainView.plotCommissions2.getData()) {
//                    for (XYChart.Data d : s.getData()) {
//                        Tooltip t = new Tooltip(d.getYValue().toString());
//                        //t.setShowDelay(Duration.seconds(0));
//                        Tooltip.install(d.getNode(), t);
//                        d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
//                        d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
//                    }
//                }
//
//            }
//            this.poolPairModelList.sort(Comparator.comparing(PoolPairModel::getBlockTimeValue));
//            this.poolPairList.clear();
//            this.poolPairList.addAll(this.poolPairModelList);
//        }

    }

    public ObservableList<TransactionModel> getTransactionTable() {
        return this.transactionController.getTransactionList();
    }

    public ObservableList<PoolPairModel> getPlotData() {
        return this.poolPairList;
    }

    public void exportTransactionToExcel(List<TransactionModel> list, String filter) {

        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv")
        );
        if (new File(this.settingsController.lastExportPath).isDirectory()) {
            fileChooser.setInitialDirectory(new File(this.settingsController.lastExportPath));
        }

        String exportPath = "";
        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (filter.equals("DAILY")) {
            exportPath = dateFormat.format(date) + "_Portfolio_Export_RawData";
        } else if (filter.equals("")) {
            exportPath = dateFormat.format(date) + "_Portfolio_Export_RawData";
        }else {
            exportPath = dateFormat.format(date) + "_Portfolio_Export_Cointracking";
        }


        fileChooser.setInitialFileName(exportPath);
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        extracted(list, filter, localeDecimal, selectedFile);
    }

    public void exportTransactionToExcel(TableView<TransactionModel> rawDataTable) {
        List<TransactionModel> list;
        String filter;
        if(settingsController.exportCSVVariant.getValue().equals("Export selected to CSV")){
            list = rawDataTable.selectionModelProperty().get().getSelectedItems();
            filter = "";
        }else if(settingsController.exportCSVVariant.getValue().equals("Export all to CSV"))
        {
            list = rawDataTable.getItems();
            filter = "";
        }else{
            list = rawDataTable.getItems();
            filter = "DAILY";
        }

        Locale localeDecimal = Locale.GERMAN;
        if (settingsController.selectedDecimal.getValue().equals(".")) {
            localeDecimal = Locale.US;
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSV files", "*.csv")
        );
        if (new File(this.settingsController.lastExportPath).isDirectory()) {
            fileChooser.setInitialDirectory(new File(this.settingsController.lastExportPath));
        }

        Date date = new Date(System.currentTimeMillis());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        fileChooser.setInitialFileName(dateFormat.format(date) + "_Portfolio_Export_RawData");
        File selectedFile = fileChooser.showSaveDialog(new Stage());

        extracted(list, filter, localeDecimal, selectedFile);
    }

    private void extracted(List<TransactionModel> list, String filter, Locale localeDecimal, File selectedFile) {
//        if (selectedFile != null) {
//            boolean success;
//            if (filter.equals("DAILY")) {
//                success = this.expService.exportTransactionToExcelDaily(list, selectedFile.getPath(), localeDecimal, this.settingsController.selectedSeparator.getValue(),mainView.rawDataTable.getColumns());
//            } else if (filter.equals("")) {
//                success = this.expService.exportTransactionToExcel(list, selectedFile.getPath(), localeDecimal, this.settingsController.selectedSeparator.getValue(),mainView.rawDataTable.getColumns());
//            } else {
//                ExportService.ExportResult result = this.expService.exportTransactionToCointracking(list, selectedFile.getPath(), localeDecimal, this.settingsController.selectedSeparator.getValue(), settingsController.exportCointracingVariant.getValue(),mainView.rawDataTable.getColumns());
//                success = result.result;
//                if(result.showMissingTransactionWindow){
//                    mainView.showMissingTransactionWindow();
//                }
//
//            }
//
//            if (success) {
//                this.settingsController.lastExportPath = selectedFile.getParent();
//                this.settingsController.saveSettings();
//                try {
//                    this.showExportSuccessfullWindow();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                try {
//                    this.showExportErrorWindow();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    public void exportPoolPairToExcel(List<PoolPairModel> list, String source) {

//        FileChooser fileChooser = new FileChooser();
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("CSV files", "*.csv")
//        );
//        if (new File(this.settingsController.lastExportPath).isDirectory()) {
//            fileChooser.setInitialDirectory(new File(this.settingsController.lastExportPath));
//        }
//        Date date = new Date(System.currentTimeMillis());
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        fileChooser.setInitialFileName(dateFormat.format(date) + "_Portfolio_Export_" + this.mainView.tabPane.getSelectionModel().getSelectedItem().getId());
//        File selectedFile = fileChooser.showSaveDialog(new Stage());
//
//        if (selectedFile != null) {
//            boolean success = this.expService.exportPoolPairToExcel(list, selectedFile.getPath(), source, this.mainView);
//
//            if (success) {
//                this.settingsController.lastExportPath = selectedFile.getParent();
//                this.settingsController.saveSettings();
//                try {
//                    this.showExportSuccessfullWindow();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            } else {
//                try {
//                    this.showExportErrorWindow();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }

    public void showExportSuccessfullWindow() throws IOException {
        Parent rootExportFinished = null;
        rootExportFinished = fxmlLoader.load(getClass().getResource("views/ExportSuccessfullView.fxml")).getRoot();
        Scene sceneExportFinished = new Scene(rootExportFinished);
        Stage stageExportFinished = new Stage();
        stageExportFinished.setScene(sceneExportFinished);
        stageExportFinished.initStyle(StageStyle.UNDECORATED);
        sceneExportFinished.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = stageExportFinished.getX() - mouseEvent.getScreenX();
            dragDelta.y = stageExportFinished.getY() - mouseEvent.getScreenY();
        });
        sceneExportFinished.setOnMouseDragged(mouseEvent -> {
            stageExportFinished.setX(mouseEvent.getScreenX() + dragDelta.x);
            stageExportFinished.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        stageExportFinished.show();
        stageExportFinished.setAlwaysOnTop(true);

        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            stageExportFinished.getScene().getStylesheets().add(CssProvider.DARK_MODE);
        } else {
            stageExportFinished.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
        }
    }
    public void showExportErrorWindow() throws IOException {
        Parent rootExportFinished = null;
        rootExportFinished = fxmlLoader.load(getClass().getResource("views/ExportErrorView.fxml")).getRoot();
        Scene sceneExportFinished = new Scene(rootExportFinished);
        Stage stageExportFinished = new Stage();
        stageExportFinished.setScene(sceneExportFinished);
        stageExportFinished.initStyle(StageStyle.UNDECORATED);
        sceneExportFinished.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = stageExportFinished.getX() - mouseEvent.getScreenX();
            dragDelta.y = stageExportFinished.getY() - mouseEvent.getScreenY();
        });
        sceneExportFinished.setOnMouseDragged(mouseEvent -> {
            stageExportFinished.setX(mouseEvent.getScreenX() + dragDelta.x);
            stageExportFinished.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        stageExportFinished.show();
        stageExportFinished.setAlwaysOnTop(true);

        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            stageExportFinished.getScene().getStylesheets().add(CssProvider.DARK_MODE);
        } else {
            stageExportFinished.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
        }
    }

    @Override
    public void invalidated(Observable observable) {

    }

    static class Delta { double x, y; }
/****************************************/
    private void updateLanguage() {
        settingsController.updateLanguage();

        Map<String,String> localisationMap = settingsController.getLocalisations("MainView");
//        mainView.setLocalisations(localisationMap);

//        if (mainView.cmbIntervall.getItems().size() > 0) {
//            ObservableList<String> cmbIntervallComItems = mainView.cmbIntervallCom.getItems();
//            cmbIntervallComItems.set(0, settingsController.getTranslationValue(("Daily")));
//            cmbIntervallComItems.set(1, settingsController.getTranslationValue(("Weekly")));
//            cmbIntervallComItems.set(2, settingsController.getTranslationValue(("Monthly")));
//            cmbIntervallComItems.set(3, settingsController.getTranslationValue(("Yearly")));
//
//            ObservableList<String> cmbIntervallOverItems = mainView.cmbIntervallOver.getItems();
//            cmbIntervallOverItems.set(0, settingsController.getTranslationValue(("Daily")));
//            cmbIntervallOverItems.set(1, settingsController.getTranslationValue(("Weekly")));
//            cmbIntervallOverItems.set(2, settingsController.getTranslationValue(("Monthly")));
//            cmbIntervallOverItems.set(3, settingsController.getTranslationValue(("Yearly")));
//
//
//            ObservableList<String> cmbIntervallItems = mainView.cmbIntervall.getItems();
//            cmbIntervallItems.set(0, settingsController.getTranslationValue(("Daily")));
//            cmbIntervallItems.set(1, settingsController.getTranslationValue(("Weekly")));
//            cmbIntervallItems.set(2, settingsController.getTranslationValue(("Monthly")));
//            cmbIntervallItems.set(3, settingsController.getTranslationValue(("Yearly")));
//
//            ObservableList<String> cmbPlotCurrencyItems = mainView.cmbPlotCurrency.getItems();
//            cmbPlotCurrencyItems.set(0, settingsController.getTranslationValue(("Individual")));
//            cmbPlotCurrencyItems.set(1, settingsController.getTranslationValue(("Cumulated")));
//
//            ObservableList<String> cmbPlotCurrencyComItems = mainView.cmbPlotCurrencyCom.getItems();
//            cmbPlotCurrencyComItems.add(0, settingsController.getTranslationValue(("Individual")));
//            cmbPlotCurrencyComItems.add(1, settingsController.getTranslationValue(("Cumulated")));
//
//        } else {
//
//            ObservableList<String> cmbIntervallComItems = mainView.cmbIntervallCom.getItems();
//            cmbIntervallComItems.add(settingsController.getTranslationValue(("Daily")));
//            cmbIntervallComItems.add(settingsController.getTranslationValue(("Weekly")));
//            cmbIntervallComItems.add(settingsController.getTranslationValue(("Monthly")));
//            cmbIntervallComItems.add(settingsController.getTranslationValue(("Yearly")));
//
//            ObservableList<String> cmbIntervallOverItems = mainView.cmbIntervallOver.getItems();
//            cmbIntervallOverItems.add(settingsController.getTranslationValue(("Daily")));
//            cmbIntervallOverItems.add(settingsController.getTranslationValue(("Weekly")));
//            cmbIntervallOverItems.add(settingsController.getTranslationValue(("Monthly")));
//            cmbIntervallOverItems.add(settingsController.getTranslationValue(("Yearly")));
//
//            ObservableList<String> cmbIntervallItems = mainView.cmbIntervall.getItems();
//            cmbIntervallItems.add(settingsController.getTranslationValue(("Daily")));
//            cmbIntervallItems.add(settingsController.getTranslationValue(("Weekly")));
//            cmbIntervallItems.add(settingsController.getTranslationValue(("Monthly")));
//            cmbIntervallItems.add(settingsController.getTranslationValue(("Yearly")));
//
//            ObservableList<String> cmbPlotCurrencyItems = mainView.cmbPlotCurrency.getItems();
//            cmbPlotCurrencyItems.add(settingsController.getTranslationValue(("Individual")));
//            cmbPlotCurrencyItems.add(settingsController.getTranslationValue(("Cumulated")));
//
//            ObservableList<String> cmbPlotCurrencyComItems = mainView.cmbPlotCurrencyCom.getItems();
//            cmbPlotCurrencyComItems.add(settingsController.getTranslationValue(("Individual")));
//            cmbPlotCurrencyComItems.add(settingsController.getTranslationValue(("Cumulated")));
//        }
//
//        mainView.btnConnect.setText(settingsController.getTranslationValue(("openAddressConfig")));
////        this.connectionLabel.getTooltip().setText(settingsController.getTranslationValue(("UpdateTooltip")));
//        mainView.blockTimeColumn.setText(settingsController.getTranslationValue(("Date")));
//        mainView.timeStampColumn.setText(settingsController.getTranslationValue(("Date")));
//        mainView.typeColumn.setText(settingsController.getTranslationValue(("Operation")));
//        mainView.cryptoValueColumn.setText(settingsController.getTranslationValue(("CryptoValue")));
//        mainView.cryptoCurrencyColumn.setText(settingsController.getTranslationValue(("CryptoCurrency")));
//        mainView.fiatValueColumn.setText(settingsController.getTranslationValue(("FIATValue")));
//        mainView.fiatCurrencyColumn.setText(settingsController.getTranslationValue(("FIATCurrency")));
//        mainView.poolIDColumn.setText(settingsController.getTranslationValue(("PoolPair")));
//        mainView.poolPairColumn.setText(settingsController.getTranslationValue(("PoolPair")));
//        mainView.blockHeightColumn.setText(settingsController.getTranslationValue(("BlockHeight")));
//        mainView.blockHashColumn.setText(settingsController.getTranslationValue(("BlockHash")));
//        mainView.ownerColumn.setText(settingsController.getTranslationValue(("Owner")));
//        mainView.transactionColumn.setText(settingsController.getTranslationValue(("TransactionHash")));
//        mainView.fiatColumn.setText(settingsController.getTranslationValue(("Total")));
//        donateController.strDonateText.setValue(settingsController.getTranslationValue(("DonateLabel")));
//        settingsController.selectedPlotType.setValue(settingsController.getTranslationValue(("Individual")));
//        settingsController.selectedInterval.setValue(settingsController.getTranslationValue(("Daily")));
//
//
//        if (!this.init)
            this.updateHeader();
    }
        private Tab getSelectedItem(TabPane tabPane) {
        return tabPane.getSelectionModel().getSelectedItem();
    }
    public void updateHeader() {
//        Tab selectedItem = getSelectedItem(mainView.tabPane);
//        timeStampColumn.setText(settingsController.getTranslationValue("Date").toString());
//        poolPairColumn.setText(settingsController.getTranslationValue("PoolPair").toString());
//
//        Tab selectedItem = getSelectedItem(tabPane);
//        if(selectedItem!=null){
//            switch (selectedItem.getId()) {
//                case "Portfolio":
//                    timeStampColumn.setText(settingsController.getTranslationValue("Token").toString());
//                    poolPairColumn.setText(settingsController.getTranslationValue("CryptoValue").toString());
//                    balanceFiatColumn.setText(settingsController.getTranslationValue("FIATValue").toString()+" (" + settingsController.selectedFiatCurrency.getValue()+")");
//                    crypto1Column.setVisible(false);
//                    crypto1FiatColumn.setVisible(false);
//                    crypto2Column.setVisible(false);
//                    crypto2FiatColumn.setVisible(false);
//                    Commission2OverviewColumn.setVisible(false);
//                    Commission2OverviewFiatColumn.setVisible(false);
//                    fiatColumn.setVisible(false);
//                    balanceFiatColumn.setVisible(true);
//                    break;
//                case "Overview":
//                    crypto1Column.setText(settingsController.getTranslationValue("Rewards").toString());
//                    crypto1FiatColumn.setText(settingsController.getTranslationValue("Rewards") + " (" + settingsController.selectedFiatCurrency.getValue() + ")");
//                    crypto2Column.setText(settingsController.getTranslationValue("Commissions") + " DFI");
//                    crypto2FiatColumn.setText(settingsController.getTranslationValue("Commissions") + " DFI(" + settingsController.selectedFiatCurrency.getValue() + ")");
//                    Commission2OverviewColumn.setText(settingsController.getTranslationValue("Commissions") + " 2");
//                    Commission2OverviewFiatColumn.setText(settingsController.getTranslationValue("Commissions") + " 2(" + settingsController.selectedFiatCurrency.getValue() + ")");
//
//                    balanceFiatColumn.setVisible(false);
//                    crypto1Column.setVisible(true);
//                    crypto1FiatColumn.setVisible(true);
//                    crypto2Column.setVisible(true);
//                    crypto2FiatColumn.setVisible(true);
//                    Commission2OverviewColumn.setVisible(true);
//                    Commission2OverviewFiatColumn.setVisible(true);
//                    fiatColumn.setVisible(true);
//                    break;
//                case "Rewards":
//                    crypto1Column.setText(settingsController.selectedCoin.getValue().split("-")[1]);
//                    crypto1FiatColumn.setText(settingsController.selectedCoin.getValue().split("-")[1] + " (" + settingsController.selectedFiatCurrency.getValue() + ")");
//                    crypto1Column.setVisible(true);
//                    balanceFiatColumn.setVisible(false);
//                    crypto1FiatColumn.setVisible(true);
//                    crypto2Column.setVisible(false);
//                    crypto2FiatColumn.setVisible(false);
//                    Commission2OverviewColumn.setVisible(false);
//                    Commission2OverviewFiatColumn.setVisible(false);
//                    fiatColumn.setVisible(false);
//                    break;
//                case "Commissions":
//                    crypto1Column.setText(settingsController.selectedCoin.getValue().split("-")[1]);
//                    crypto1FiatColumn.setText(settingsController.selectedCoin.getValue().split("-")[1] + " (" + settingsController.selectedFiatCurrency.getValue() + ")");
//                    crypto2Column.setText(settingsController.selectedCoin.getValue().split("-")[0]);
//                    crypto2FiatColumn.setText(settingsController.selectedCoin.getValue().split("-")[0] + " (" + settingsController.selectedFiatCurrency.getValue() + ")");
//
//                    crypto1Column.setVisible(true);
//                    crypto1FiatColumn.setVisible(true);
//                    crypto2Column.setVisible(true);
//                    crypto2FiatColumn.setVisible(true);
//                    balanceFiatColumn.setVisible(false);
//                    Commission2OverviewColumn.setVisible(false);
//                    Commission2OverviewFiatColumn.setVisible(false);
//                    fiatColumn.setVisible(true);
//                    break;
//                default:
//                    break;
//            }
//        }
    }
}
