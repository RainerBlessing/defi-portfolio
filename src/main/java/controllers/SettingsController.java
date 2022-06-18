package controllers;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import javafx.beans.property.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


@Singleton
public class SettingsController {
    public final String Version = "V1.6.2";

    private LocalisationProvider localisationProvider;
//    private static SettingsController OBJ = null;
//
//    static {
//        try {
//            OBJ = new SettingsController();
//        } catch (IOException e) {
//            logger.warning("Exception occurred: " + e);
//        }
//    }

    public final StringProperty selectedLanguage = new SimpleStringProperty("English");
    public final StringProperty selectedFiatCurrency = new SimpleStringProperty("EUR");
    public final StringProperty selectedDecimal = new SimpleStringProperty(".");
    public final StringProperty selectedSeperator = new SimpleStringProperty(",");
    public final StringProperty selectedStyleMode = new SimpleStringProperty("Dark Mode");
    public final StringProperty selectedCoin = new SimpleStringProperty("BTC-DFI");
    public final StringProperty selectedPlotCurrency = new SimpleStringProperty("Coin");
    public final StringProperty selectedDefaulUpdateSource = new SimpleStringProperty("Show options");
    public final StringProperty selectedPlotType = new SimpleStringProperty();
    public final StringProperty selectedIntervall = new SimpleStringProperty();
    public final StringProperty tokenBalance = new SimpleStringProperty();
    public final StringProperty tokenBalanceLM = new SimpleStringProperty();
    public final StringProperty tokenAmount = new SimpleStringProperty();
    public final StringProperty tokenYield = new SimpleStringProperty();
    public final StringProperty tokenYieldRewards = new SimpleStringProperty();
    public final StringProperty tokenYieldCommissions = new SimpleStringProperty();
    public final ObjectProperty<LocalDate> dateFrom = new SimpleObjectProperty("2020-11-30");
    public final ObjectProperty<LocalDate> dateTo = new SimpleObjectProperty();
    public final ObjectProperty<LocalDate> exportFrom = new SimpleObjectProperty();
    public final ObjectProperty<LocalDate> exportTo = new SimpleObjectProperty();
    public final ObjectProperty<JSONObject> translationList = new SimpleObjectProperty();
    public String selectedIntervallInt = "Daily";
    public final StringProperty selectedSource = new SimpleStringProperty("Active Wallet");
    public final StringProperty exportCointracingVariante = new SimpleStringProperty();
    public final StringProperty exportCSVCariante = new SimpleStringProperty();
    public boolean showDisclaim = true;
    public boolean showMissingTransaction = true;
    public boolean selectedLaunchDefid = false;
    public boolean selectedLaunchSync = true;
    public boolean checkCointracking = false;
    public boolean updatePython = false;
    public final ArrayList<String> listAddresses = new ArrayList();

    public final StringProperty lastUpdate = new SimpleStringProperty("-");
    //Combo box filling
    public final String[] cryptoCurrencies = new String[]{"BTC-DFI", "ETH-DFI", "USDT-DFI", "LTC-DFI", "BCH-DFI", "DOGE-DFI", "USDC-DFI","DUSD-DFI","TSLA-DUSD","SPY-DUSD","QQQ-DUSD","PLTR-DUSD","AAPL-DUSD","GME-DUSD","GOOGL-DUSD","BABA-DUSD","ARKK-DUSD","TLT-DUSD","GLD-DUSD","SLV-DUSD","PDBC-DUSD","URTH-DUSD","NVDA-DUSD","AMZN-DUSD","EEM-DUSD","COIN-DUSD","INTC-DUSD","DIS-DUSD","MSFT-DUSD","NFLX-DUSD","VOO-DUSD","MSTR-DUSD","FB-DUSD","MCHI-DUSD"};
    public final String[] plotCurrency = new String[]{"Coin", "Daily Fiat", "Current Fiat"};
    public final String[] styleModes = new String[]{"Light Mode", "Dark Mode"};
    public final String[] datasources = new String[]{"Active Wallet", "All Wallets"};
    public final String[] cointrackingExportVariants = new String[]{"Cumulate All", "Cumulate None","Cumulate Pool Pair","Cumulate Rewards and Commisions"};
    public final String[] csvExportVariants = new String[]{"Export selected to CSV","Export all to CSV","Export all to CSV (Daily cumulated)"};
    public final String[] defaultUpdateSource = new String[]{"Show options", "Update data","Wallet CSV"};

    //All relevant paths and files
    public final String USER_HOME_PATH = System.getProperty("user.home").replace("\\","/");
    public final String BINARY_FILE_NAME = getPlatform().equals("win") ? "defid.exe" : "defid";
    public final String BINARY_FILE_PATH = System.getProperty("user.dir").replace("\\","/") + "/PortfolioData/" + BINARY_FILE_NAME;
    public final String CONFIG_FILE_PATH =getPlatform() == "win" ?
            System.getProperty("user.home").replace("\\","/") + "/.defi/defi.conf" : //WIN PATH
    getPlatform() == "mac" ? System.getProperty("user.home").replace("\\","/")  + "/Library/Application Support/DeFi/.defi/defi.conf" : //MAC PATH
    getPlatform() == "linux" ? System.getProperty("user.home").replace("\\","/")  + "/.defi/defi.conf" : //LINUX PATH
    "";
    public final String DEFI_PORTFOLIO_HOME = getPlatform().equals("win") ?
            System.getenv("APPDATA").replace("\\","/") + "/defi-portfolio/" : //WIN PATH
            getPlatform().equals("mac") ? System.getProperty("user.dir").replace("\\","/") + "/PortfolioData/" : //MAC PATH
                    getPlatform().equals("linux") ? System.getProperty("user.dir").replace("\\","/") + "/PortfolioData/" : //LINUX PATH;
                            "";
    public final String PORTFOLIO_CONFIG_FILE_PATH = System.getProperty("user.dir").replace("\\","/") + "/PortfolioData/defi.conf";
    public final String SETTING_FILE_PATH = DEFI_PORTFOLIO_HOME + "settings.csv";
    public final String PORTFOLIO_FILE_PATH = DEFI_PORTFOLIO_HOME + "portfolioData.portfolio";
    public final String INCOMPLETE_FILE_PATH = DEFI_PORTFOLIO_HOME + "incompleteList.portfolio";
    public final String strTransactionData = "transactionData.portfolio";
    public final String strCoinPriceData = "coinPriceData.portfolio";
    public final String strStockPriceData = "stockTockenPrices.portfolio";
    public final String[] languages = new String[]{"English", "Deutsch","Espa\u00F1ol","Bokm\u00E5l","Nederlands"};
    public final String[] currencies = new String[]{"EUR", "USD", "CHF"};
    public String[] decSeperators = new String[]{".", ","};
    public String[] csvSeperators = new String[]{",", ";"};
    public final Logger logger = Logger.getLogger("Logger");
    public String rpcauth;
    public String rpcuser;
    public String rpcpassword;
    public String rpcbind;
    public String rpcport;

    public boolean runTimer = true;
    public boolean debouncer = false;
    public String auth;

    public final Timer timer = new Timer("Timer");

    public String lastExportPath = USER_HOME_PATH;
    public String lastWalletCSVImportPath;
    public boolean runCheckTimer;
    public int errorBouncer = 0;

    @Inject
    public SettingsController(LocalisationProvider localisationProvider) throws IOException {
        this.localisationProvider = localisationProvider;

        FileHandler fh;

        File directory = new File(DEFI_PORTFOLIO_HOME);
        if (!directory.exists()) {
            directory.mkdir();
        }

        fh = new FileHandler(DEFI_PORTFOLIO_HOME + "log.txt");
        this.logger.addHandler(fh);
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        this.loadSettings();
        this.loadAddresses();
        updateLanguage();
        getConfig();
    }

    public void loadAddresses(){
        String savePath = this.DEFI_PORTFOLIO_HOME + "Addresses.csv";
        File f = new File(savePath);
        if(f.exists() && !f.isDirectory()) {
            this.listAddresses.clear();
            try (BufferedReader br = new BufferedReader(new FileReader(savePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    this.listAddresses.add(line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateLanguage() {

        try {
            this.translationList.setValue(localisationProvider.readLanguageFile(selectedLanguage.getValue()));
        } catch (ParseException | IOException e) {
            logger.warning("Exception occurred: " + e);
        }
    }

    public String getPlatform() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        if ((OS.contains("mac")) || (OS.contains("darwin"))) {
            return "mac";
        } else if (OS.contains("win")) {
            return "win";
        } else if (OS.contains("nux")) {
            return "linux";
        } else {
            return "win";
        }
    }

    public void loadSettings() throws IOException {
        File f = new File(SETTING_FILE_PATH);
        if (f.exists() && !f.isDirectory()) {
            File configFile = new File(SETTING_FILE_PATH);
            Properties configProps = new Properties();
            try (FileInputStream i = new FileInputStream(configFile)) {
                configProps.load(i);
            }

            try {
                if(configProps.getProperty("SelectedLanguage").contains("Espa")) {
                    this.selectedLanguage.setValue("Espa\u00F1ol");
                }
                else if(configProps.getProperty("SelectedLanguage").contains("Bokm")){
                    this.selectedLanguage.setValue("Bokm\u00E5l");
                }else{
                    this.selectedLanguage.setValue(configProps.getProperty("SelectedLanguage"));
                }
                this.selectedFiatCurrency.setValue(configProps.getProperty("SelectedFiatCurrency"));
                this.selectedDecimal.setValue(configProps.getProperty("SelectedDecimal"));
                this.selectedSeperator.setValue(configProps.getProperty("SelectedSeperator"));
                this.selectedCoin.setValue(configProps.getProperty("SelectedCoin"));
                this.selectedPlotCurrency.setValue(configProps.getProperty("SelectedPlotCurrency"));
                this.selectedStyleMode.setValue(configProps.getProperty("SelectedStyleMode"));
                this.exportCointracingVariante.setValue(configProps.getProperty("ExportCointrackinVariante"));
                this.exportCSVCariante.setValue(configProps.getProperty("ExportCSVVariante"));
                this.exportFrom.setValue(LocalDate.parse(configProps.getProperty("ExportFrom")));
                this.exportTo.setValue(LocalDate.parse(LocalDate.now().toString()));
                this.dateFrom.setValue(LocalDate.parse(configProps.getProperty("SelectedDate")));
                if (!configProps.getProperty("LastUsedExportPath").equals(""))
                    this.lastExportPath = configProps.getProperty("LastUsedExportPath");
                this.showDisclaim = configProps.getProperty("ShowDisclaim").equals("true");
                this.selectedLaunchDefid = configProps.getProperty("SelectedLaunchDefid").equals("true");
                if (configProps.getProperty("SelectedLaunchSync") != null) {
                    this.selectedLaunchSync = configProps.getProperty("SelectedLaunchSync").equals("true");
                } else {
                    this.selectedLaunchSync = false;
                }
                if(configProps.getProperty("SelectedSource") !=null){
                    this.selectedSource.setValue(configProps.getProperty("SelectedSource"));
                }else{
                    this.selectedSource.setValue("Active Wallet");
                }
                if(configProps.getProperty("LastUpdate") !=null){
                    this.lastUpdate.setValue(configProps.getProperty("LastUpdate"));
                }else{
                    this.lastUpdate.setValue("-");
                }
                if(configProps.getProperty("DefaultDataSource") !=null){
                    this.selectedDefaulUpdateSource.setValue(configProps.getProperty("DefaultDataSource"));
                }else{
                    this.selectedDefaulUpdateSource.setValue("Show options");
                }
                this.showMissingTransaction = configProps.getProperty("MissingTransaction").equals("true");
                this.lastWalletCSVImportPath = configProps.getProperty("LastWalletCSVImportPath");
            } catch (Exception e) {
                logger.warning("Exception occurred: " + e);
                saveSettings();
            }
        }
    }

    public void saveSettings() {

        FileWriter csvWriter;
        try {
            csvWriter = new FileWriter(SETTING_FILE_PATH);
            csvWriter.append("SelectedLanguage=").append(this.selectedLanguage.getValue()).append("\n");
            csvWriter.append("SelectedFiatCurrency=").append(this.selectedFiatCurrency.getValue()).append("\n");
            csvWriter.append("SelectedDecimal=").append(this.selectedDecimal.getValue()).append("\n");
            csvWriter.append("SelectedSeperator=").append(this.selectedSeperator.getValue()).append("\n");
            csvWriter.append("SelectedCoin=").append(this.selectedCoin.getValue()).append("\n");
            csvWriter.append("SelectedPlotCurrency=").append(this.selectedPlotCurrency.getValue()).append("\n");
            csvWriter.append("SelectedStyleMode=").append(this.selectedStyleMode.getValue()).append("\n");
            csvWriter.append("SelectedDate=").append(String.valueOf(this.dateFrom.getValue())).append("\n");
            csvWriter.append("LastUsedExportPath=").append(this.lastExportPath.replace("\\", "/")).append("\n");
            csvWriter.append("ShowDisclaim=").append(String.valueOf(this.showDisclaim)).append("\n");
            csvWriter.append("SelectedLaunchDefid=").append(String.valueOf(this.selectedLaunchDefid)).append("\n");
            csvWriter.append("SelectedLaunchSync=").append(String.valueOf(this.selectedLaunchSync)).append("\n");
            csvWriter.append("SelectedSource=").append(this.selectedSource.getValue()).append("\n");
            csvWriter.append("LastUpdate=").append(this.lastUpdate.getValue()).append("\n");
            csvWriter.append("ExportCointrackinVariante=").append(this.exportCointracingVariante.getValue()).append("\n");
            csvWriter.append("ExportCSVVariante=").append(this.exportCSVCariante.getValue()).append("\n");
            csvWriter.append("ExportFrom=").append(String.valueOf(this.exportFrom.getValue())).append("\n");
            csvWriter.append("ExportTo=").append(String.valueOf(this.exportTo.getValue())).append("\n");
            csvWriter.append("MissingTransaction=").append(String.valueOf(this.showMissingTransaction)).append("\n");
            csvWriter.append("DefaultDataSource=").append(this.selectedDefaulUpdateSource.getValue()).append("\n");
            csvWriter.append("LastWalletCSVImportPath=").append(this.lastWalletCSVImportPath).append("\n");
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {
            this.logger.warning("Exception occurred: " + e.toString());
        }
    }

    public void getConfig() {

        // copy config file
        try {
            File pathConfig = new File(this.CONFIG_FILE_PATH);
            if (pathConfig.exists()) {
                File pathPortfoliohDataConfig = new File(this.PORTFOLIO_CONFIG_FILE_PATH);
                Files.copy(pathConfig.toPath(), pathPortfoliohDataConfig.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            logger.warning("Exception occurred: " + e);
        }
        // adapt port
        Path path = Paths.get(this.PORTFOLIO_CONFIG_FILE_PATH);
        Charset charset = StandardCharsets.UTF_8;
        try {
            File configFile = new File(this.PORTFOLIO_CONFIG_FILE_PATH);
            Properties configProps = new Properties();
            try (FileInputStream i = new FileInputStream(configFile)) {
                configProps.load(i);
            } catch (IOException e) {
                logger.warning("Exception occurred: " + e);
            }

            String rpcportConfig = configProps.getProperty("rpcport");
            String rpcBindConfig = configProps.getProperty("rpcbind");
            String rpcConnectConfig = configProps.getProperty("rpcconnect");
            String content = Files.readString(path, charset);
            if(rpcportConfig != null)content = content.replaceAll(rpcportConfig, "8554");
            if(rpcBindConfig != null)content = content.replaceAll(rpcBindConfig, "127.0.0.1");
            if(rpcConnectConfig != null)content = content.replaceAll(rpcConnectConfig, "127.0.0.1");
            Files.writeString(path, content, charset);
        } catch (Exception e) {
            logger.warning("Exception occurred: " + e.toString());
        }

            File configFile = new File(this.PORTFOLIO_CONFIG_FILE_PATH);
            Properties configProps = new Properties();
            try (FileInputStream i = new FileInputStream(configFile)) {
                configProps.load(i);
            } catch (IOException e) {
                logger.warning("Exception occurred: " + e.toString());
            }
            this.rpcauth = configProps.getProperty("rpcauth");
            this.rpcuser = configProps.getProperty("rpcuser");
            this.rpcpassword = configProps.getProperty("rpcpassword");
            this.rpcbind = configProps.getProperty("rpcbind");
            this.rpcport = configProps.getProperty("rpcport");
            this.auth = this.rpcuser + ":" + this.rpcpassword;

    }
}