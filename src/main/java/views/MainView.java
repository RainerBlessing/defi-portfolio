package views;

import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import controllers.CheckConnection;
import controllers.MainViewController;
import controllers.SettingsController;
import controllers.TransactionController;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import models.PoolPairModel;
import models.TransactionModel;
import resourceprovider.CssProvider;
import resourceprovider.IconProvider;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

@Singleton
public class MainView implements Initializable {
    private TransactionController transactionController;
    private SettingsController settingsController;
    @FXML
    public NumberAxis yAxis;
    @FXML
    public AnchorPane mainAnchorPane;
    @FXML
    public AnchorPane leftAnchorPane;
    @FXML
    public Button btnRawData;
    @FXML
    public Button btnAnalyse;
    @FXML
    public Button btnUpdateDatabase;
    @FXML
    public Pane anchorPanelAnalyse, anchorPanelRawData;
    @FXML
    public Label strCurrentBlockLocally, strCurrentBlockOnBlockchain, strLastUpdate;
    @FXML
    public ComboBox<String> cmbCoins, cmbIntervall, cmbFiat, cmbPlotCurrency, cmbCoinsCom, cmbIntervallCom, cmbFiatCom, cmbPlotCurrencyCom, cmbIntervallOver;
    @FXML
    public ImageView coinImageRewards1,coinImageRewards2, coinImageCommissions1,coinImageCommissions2;
    @FXML
    public final DatePicker dateFrom = new DatePicker();
    @FXML
    public final DatePicker dateTo = new DatePicker();
    @FXML
    public final DatePicker dateFromCom = new DatePicker();
    @FXML
    public final DatePicker dateToCom = new DatePicker();
    @FXML
    public final DatePicker dateFromOver = new DatePicker();
    @FXML
    public final DatePicker dateToOver = new DatePicker();
    @FXML
    public final TabPane tabPane = new TabPane();
    @FXML
    public LineChart<Number, Number> plotRewards, plotCommissions1, plotCommissions2;
    @FXML
    public PieChart plotPortfolio1;
    @FXML
    public StackedAreaChart<Number, Number> plotOverview;
    @FXML
    public TableView<TransactionModel> rawDataTable;
    @FXML
    public TableView<PoolPairModel> plotTable;
    @FXML
    public TableColumn<TransactionModel, Long> blockTimeColumn;
    @FXML
    public TableColumn<TransactionModel, String> typeColumn;
    @FXML
    public TableColumn<TransactionModel, Double> cryptoValueColumn;
    @FXML
    public TableColumn<TransactionModel, String> cryptoCurrencyColumn;
    @FXML
    public TableColumn<TransactionModel, String> blockHashColumn;
    @FXML
    public TableColumn<TransactionModel, Integer> blockHeightColumn;
    @FXML
    public TableColumn<TransactionModel, String> poolIDColumn;
    @FXML
    public TableColumn<TransactionModel, String> ownerColumn;
    @FXML
    public TableColumn<TransactionModel, Double> fiatValueColumn;
    @FXML
    public TableColumn<TransactionModel, String> fiatCurrencyColumn;
    @FXML
    public TableColumn<TransactionModel, String> transactionColumn;
    @FXML
    public TableColumn<PoolPairModel, String> timeStampColumn;
    @FXML
    public TableColumn<PoolPairModel, Double> crypto1Column;
    @FXML
    public TableColumn<PoolPairModel, Double> crypto1FiatColumn;
    @FXML
    public TableColumn<PoolPairModel, Double> Commission2OverviewColumn;
    @FXML
    public TableColumn<PoolPairModel, Double> Commission2OverviewFiatColumn;
    @FXML
    public TableColumn<PoolPairModel, Double> crypto2Column;
    @FXML
    public TableColumn<PoolPairModel, Double> crypto2FiatColumn;
    @FXML
    public TableColumn<PoolPairModel, Double> fiatColumn;
    @FXML
    public TableColumn<PoolPairModel, String> poolPairColumn;
    @FXML
    public TableColumn<PoolPairModel, String> balanceFiatColumn;
    public Label CurrentBlock;
    public Label CurrentBlockChain;
    public Label LastUpdate;
    public Tab Rewards;
    public Tab Commissions;
    public Tab Overview;
    public Label StartDate;
    public Label EndDate;
    public Label EndDateCom;
    public Label StartDateCom;
    public Label StartDateOver;
    public Label EndDateOver;
    public Stage NoAddressesWarningView;

    public final MenuItem menuItemCopySelected = new MenuItem("Copy");
    public final MenuItem menuItemCopyHeaderSelected = new MenuItem("Copy with header");
    public final MenuItem menuItemExportSelected = new MenuItem("Export selected to CSV");
    public final MenuItem menuItemExportAllSelected = new MenuItem("Export all to CSV");
    public final MenuItem menuItemExportAllDailySelected = new MenuItem("Export all to CSV (Daily cumulated)");
    public final MenuItem menuItemExportCointracking = new MenuItem("Export to Cointracking");
    public final MenuItem menuItemExportToCSV = new MenuItem("Export to CSV");

    public final MenuItem menuItemCopySelectedPlot = new MenuItem("Copy");
    public final MenuItem menuItemCopyHeaderSelectedPlot = new MenuItem("Copy with header");
    public final MenuItem menuItemExportSelectedPlot = new MenuItem("Export selected to CSV");
    public final MenuItem menuItemExportAllSelectedPlot = new MenuItem("Export all to CSV");


    public Stage settingsStage, helpStage, donateStage, stageUpdateData;
    public boolean init = true;
    public Button btnSettings;
    public Button btnHelp;
    public Button btnDonate;
    public Button btnBuyDFI;
    public Label connectionLabel;
    public Button btnConnect;
    public Tab Portfolio;
    public Label UpdateText;
    public PieChart plotPortfolio11;
    public Label fieldTotal,fieldTotalYield,fieldTotalYieldRewards,fieldTotalYieldCommissions, tokenLabel,tokenLabelLM;
    private MainViewController mainViewController;
    private Stage AddressConfigStage;
    @Inject
    private GuiceFXMLLoader fxmlLoader;
    @Inject
    public MainView(MainViewController mainViewController, SettingsController settingsController, TransactionController transactionController) {
        this.settingsController = settingsController;
        this.mainViewController = mainViewController;
        this.transactionController = transactionController;
    }

    public MainView() {

    }
    
    public void updateHeader() {

        timeStampColumn.setText(settingsController.translationList.getValue().get("Date").toString());
        poolPairColumn.setText(settingsController.translationList.getValue().get("PoolPair").toString());

        Tab selectedItem = getSelectedItem(tabPane);
        if(selectedItem!=null){
            switch (selectedItem.getId()) {
                case "Portfolio":
                    timeStampColumn.setText(settingsController.translationList.getValue().get("Token").toString());
                    poolPairColumn.setText(settingsController.translationList.getValue().get("CryptoValue").toString());
                    balanceFiatColumn.setText(settingsController.translationList.getValue().get("FIATValue").toString()+" (" + settingsController.selectedFiatCurrency.getValue()+")");
                    crypto1Column.setVisible(false);
                    crypto1FiatColumn.setVisible(false);
                    crypto2Column.setVisible(false);
                    crypto2FiatColumn.setVisible(false);
                    Commission2OverviewColumn.setVisible(false);
                    Commission2OverviewFiatColumn.setVisible(false);
                    fiatColumn.setVisible(false);
                    balanceFiatColumn.setVisible(true);
                    break;
                case "Overview":
                    crypto1Column.setText(settingsController.translationList.getValue().get("Rewards").toString());
                    crypto1FiatColumn.setText(settingsController.translationList.getValue().get("Rewards") + " (" + settingsController.selectedFiatCurrency.getValue() + ")");
                    crypto2Column.setText(settingsController.translationList.getValue().get("Commissions") + " DFI");
                    crypto2FiatColumn.setText(settingsController.translationList.getValue().get("Commissions") + " DFI(" + settingsController.selectedFiatCurrency.getValue() + ")");
                    Commission2OverviewColumn.setText(settingsController.translationList.getValue().get("Commissions") + " 2");
                    Commission2OverviewFiatColumn.setText(settingsController.translationList.getValue().get("Commissions") + " 2(" + settingsController.selectedFiatCurrency.getValue() + ")");

                    balanceFiatColumn.setVisible(false);
                    crypto1Column.setVisible(true);
                    crypto1FiatColumn.setVisible(true);
                    crypto2Column.setVisible(true);
                    crypto2FiatColumn.setVisible(true);
                    Commission2OverviewColumn.setVisible(true);
                    Commission2OverviewFiatColumn.setVisible(true);
                    fiatColumn.setVisible(true);
                    break;
                case "Rewards":
                    crypto1Column.setText(settingsController.selectedCoin.getValue().split("-")[1]);
                    crypto1FiatColumn.setText(settingsController.selectedCoin.getValue().split("-")[1] + " (" + settingsController.selectedFiatCurrency.getValue() + ")");
                    crypto1Column.setVisible(true);
                    balanceFiatColumn.setVisible(false);
                    crypto1FiatColumn.setVisible(true);
                    crypto2Column.setVisible(false);
                    crypto2FiatColumn.setVisible(false);
                    Commission2OverviewColumn.setVisible(false);
                    Commission2OverviewFiatColumn.setVisible(false);
                    fiatColumn.setVisible(false);
                    break;
                case "Commissions":
                    crypto1Column.setText(settingsController.selectedCoin.getValue().split("-")[1]);
                    crypto1FiatColumn.setText(settingsController.selectedCoin.getValue().split("-")[1] + " (" + settingsController.selectedFiatCurrency.getValue() + ")");
                    crypto2Column.setText(settingsController.selectedCoin.getValue().split("-")[0]);
                    crypto2FiatColumn.setText(settingsController.selectedCoin.getValue().split("-")[0] + " (" + settingsController.selectedFiatCurrency.getValue() + ")");

                    crypto1Column.setVisible(true);
                    crypto1FiatColumn.setVisible(true);
                    crypto2Column.setVisible(true);
                    crypto2FiatColumn.setVisible(true);
                    balanceFiatColumn.setVisible(false);
                    Commission2OverviewColumn.setVisible(false);
                    Commission2OverviewFiatColumn.setVisible(false);
                    fiatColumn.setVisible(true);
                    break;
                default:
                    break;
            }
        }
    }

    public void btnAnalysePressed() {
        this.anchorPanelAnalyse.toFront();
        if (!this.init) {
            Tab selectedItem = getSelectedItem(this.tabPane);
            if (selectedItem != null) {
                mainViewController.plotUpdate(selectedItem.getId());
            }
            this.updateHeader();
        }
    }

    public void btnRawDataPressed() {
        this.anchorPanelRawData.toFront();
    }

    public void helpPressed() throws IOException {

        if (helpStage != null) helpStage.close();
        final Delta dragDelta = new Delta();
        Parent root = fxmlLoader.load(getClass().getResource("HelpView.fxml")).getRoot();
        Scene scene = new Scene(root);

        helpStage = new Stage();
        helpStage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = helpStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = helpStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            helpStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            helpStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        helpStage.getIcons().add(new Image(new File(System.getProperty("user.dir").replace("\\","/") + "/defi-portfolio/src/icons/help.png").toURI().toString()));
        helpStage.setTitle((settingsController.translationList.getValue().get("HelpTitle").toString()));
        helpStage.setScene(scene);
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            double stageWidth = newValue.doubleValue();
            helpStage.setX(mainAnchorPane.getScene().getWindow().getX() + mainAnchorPane.getScene().getWindow().getWidth() / 2 - stageWidth / 2);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            double stageHeight = newValue.doubleValue();
            helpStage.setY(mainAnchorPane.getScene().getWindow().getY() + mainAnchorPane.getScene().getWindow().getHeight() / 2 - stageHeight / 2);
        };

        helpStage.widthProperty().addListener(widthListener);
        helpStage.heightProperty().addListener(heightListener);

        helpStage.setOnShown(e -> {
            helpStage.widthProperty().removeListener(widthListener);
            helpStage.heightProperty().removeListener(heightListener);
        });
        helpStage.show();

        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            helpStage.getScene().getStylesheets().add(CssProvider.DARK_MODE);
        } else {
            helpStage.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
        }
    }

    public void openAccountInformation() throws IOException {
        if (donateStage != null) donateStage.close();
        final Delta dragDelta = new Delta();
        Parent root = fxmlLoader.load(getClass().getResource("DonateView.fxml")).getRoot();
        Scene scene = new Scene(root);
        donateStage = new Stage();
        donateStage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = donateStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = donateStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            donateStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            donateStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        donateStage.getIcons().add(new Image(new File(System.getProperty("user.dir").replace("\\","/") + "/defi-portfolio/src/icons/donate.png").toURI().toString()));
        donateStage.setTitle(settingsController.translationList.getValue().get("Donate").toString());
        donateStage.setScene(scene);
        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            double stageWidth = newValue.doubleValue();
            donateStage.setX(mainAnchorPane.getScene().getWindow().getX() + mainAnchorPane.getScene().getWindow().getWidth() / 2 - stageWidth / 2);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            double stageHeight = newValue.doubleValue();
            donateStage.setY(mainAnchorPane.getScene().getWindow().getY() + mainAnchorPane.getScene().getWindow().getHeight() / 2 - stageHeight / 2);
        };

        donateStage.widthProperty().addListener(widthListener);
        donateStage.heightProperty().addListener(heightListener);

        donateStage.setOnShown(e -> {
            donateStage.widthProperty().removeListener(widthListener);
            donateStage.heightProperty().removeListener(heightListener);
        });
        donateStage.show();

        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            donateStage.getScene().getStylesheets().add(CssProvider.DARK_MODE);
        } else {
            donateStage.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
        }
    }

    public void openSettingPressed() throws IOException {

        if (settingsStage != null) settingsStage.close();
        final Delta dragDelta = new Delta();
        Parent root = fxmlLoader.load(getClass().getResource("SettingsView.fxml")).getRoot();
        Scene scene = new Scene(root);
        settingsStage = new Stage();
        settingsStage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = settingsStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = settingsStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            settingsStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            settingsStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        settingsStage.getIcons().add(new Image(new File(System.getProperty("user.dir").replace("\\","/") + "/defi-portfolio/src/icons/settings.png").toURI().toString()));
        settingsStage.setTitle(settingsController.translationList.getValue().get("Settings").toString());
        settingsStage.setScene(scene);

        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
            double stageWidth = newValue.doubleValue();
            settingsStage.setX(mainAnchorPane.getScene().getWindow().getX() + mainAnchorPane.getScene().getWindow().getWidth() / 2 - stageWidth / 2);
        };
        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
            double stageHeight = newValue.doubleValue();
            settingsStage.setY(mainAnchorPane.getScene().getWindow().getY() + mainAnchorPane.getScene().getWindow().getHeight() / 2 - stageHeight / 2);
        };

        settingsStage.widthProperty().addListener(widthListener);
        settingsStage.heightProperty().addListener(heightListener);

        settingsStage.setOnShown(e -> {
            settingsStage.widthProperty().removeListener(widthListener);
            settingsStage.heightProperty().removeListener(heightListener);
        });

        settingsStage.show();

        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            settingsStage.getScene().getStylesheets().add(CssProvider.DARK_MODE);
        } else {
            settingsStage.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
        }
    }

    public void connectDefid(ActionEvent actionEvent) {
        if (mainViewController.transactionController.checkRpc()) {
            mainViewController.transactionController.startServer();
        }
    }

    public void btnOpenAdressConfig() throws IOException {
        if (AddressConfigStage != null) AddressConfigStage.close();
        final SettingsView.Delta dragDelta = new SettingsView.Delta();
        Parent root = fxmlLoader.load(getClass().getResource("AddAddresses.fxml")).getRoot();
        Scene scene = new Scene(root);
        AddressConfigStage = new Stage();
        AddressConfigStage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = AddressConfigStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = AddressConfigStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            AddressConfigStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            AddressConfigStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        //AddressConfigStage.getIcons().add(new Image(new File(System.getProperty("user.dir").replace("\\","/") + "/defi-portfolio/src/icons/settings.png").toURI().toString()));
        //AddressConfigStage.setTitle(settingsController.translationList.getValue().get("Settings").toString());
        AddressConfigStage.setScene(scene);

//        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
//            double stageWidth = newValue.doubleValue();
//            AddressConfigStage.setX(mainAnchorPane.getScene().getWindow().getX() + mainAnchorPane.getScene().getWindow().getWidth() / 2 - stageWidth / 2);
//        };
//        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
//            double stageHeight = newValue.doubleValue();
//            AddressConfigStage.setY(mainAnchorPane.getScene().getWindow().getY() + mainAnchorPane.getScene().getWindow().getHeight() / 2 - stageHeight / 2);
//        };

        //     AddressConfigStage.widthProperty().addListener(widthListener);
        //   AddressConfigStage.heightProperty().addListener(heightListener);

        AddressConfigStage.setOnShown(e -> {
            //    AddressConfigStage.widthProperty().removeListener(widthListener);
            //   AddressConfigStage.heightProperty().removeListener(heightListener);
        });

        AddressConfigStage.show();

        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            AddressConfigStage.getScene().getStylesheets().add(CssProvider.DARK_MODE);
        } else {
            AddressConfigStage.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
        }
    }

    static class Delta {
        double x, y;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        updateLanguage();

        coinImageRewards1.setImage(IconProvider.getImage( settingsController.selectedCoin.getValue().split("-")[0].toLowerCase() + "-icon.png"));
        coinImageRewards2.setImage(IconProvider.getImage( settingsController.selectedCoin.getValue().split("-")[1].toLowerCase() + "-icon.png"));
        coinImageCommissions1.setImage(IconProvider.getImage( settingsController.selectedCoin.getValue().split("-")[0].toLowerCase() + "-icon.png"));
        coinImageCommissions2.setImage(IconProvider.getImage( settingsController.selectedCoin.getValue().split("-")[1].toLowerCase() + "-icon.png"));
        updateStylesheet();

        settingsController.selectedStyleMode.addListener(style -> updateStylesheet());
        final Delta dragDelta = new Delta();

        this.tokenLabel.textProperty().bindBidirectional(settingsController.tokenBalance);
        this.tokenLabelLM.textProperty().bindBidirectional(settingsController.tokenBalanceLM);

        this.fieldTotal.textProperty().bindBidirectional(settingsController.tokenAmount);
        this.fieldTotalYield.textProperty().bindBidirectional(settingsController.tokenYield);
        this.fieldTotalYieldRewards.textProperty().bindBidirectional(settingsController.tokenYieldRewards);
        this.fieldTotalYieldCommissions.textProperty().bindBidirectional(settingsController.tokenYieldCommissions);
        this.strCurrentBlockLocally.textProperty().bindBidirectional(mainViewController.strCurrentBlockLocally);
        this.strCurrentBlockOnBlockchain.textProperty().bindBidirectional(mainViewController.strCurrentBlockOnBlockchain);
        this.strLastUpdate.textProperty().bindBidirectional(settingsController.lastUpdate);
        this.btnUpdateDatabase.setOnAction(e -> {
                switch(settingsController.selectedDefaultUpdateSource.getValue()) {
                case "Update data":
                    if(settingsController.listAddresses.size() > 0){
                        transactionController.updateDatabase();
                    }
                    else{
                        if (NoAddressesWarningView != null) NoAddressesWarningView.close();
                        Parent root = null;
                        try {
                            root = fxmlLoader.load(getClass().getResource("NoAddressesWarningView.fxml"), resources).getRoot();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        Scene scene = new Scene(root);
                        NoAddressesWarningView = new Stage();
                        NoAddressesWarningView.initStyle(StageStyle.UNDECORATED);
                        scene.setOnMousePressed(mouseEvent -> {
                            dragDelta.x = NoAddressesWarningView.getX() - mouseEvent.getScreenX();
                            dragDelta.y = NoAddressesWarningView.getY() - mouseEvent.getScreenY();
                        });
                        scene.setOnMouseDragged(mouseEvent -> {
                            NoAddressesWarningView.setX(mouseEvent.getScreenX() + dragDelta.x);
                            NoAddressesWarningView.setY(mouseEvent.getScreenY() + dragDelta.y);
                        });
                        NoAddressesWarningView.setScene(scene);

                        NoAddressesWarningView.show();

                        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
                            NoAddressesWarningView.getScene().getStylesheets().add(CssProvider.DARK_MODE);
                        } else {
                            NoAddressesWarningView.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
                        }
                    }
                    settingsController.selectedLaunchSync = true;
                    transactionController.startServer();
                    settingsController.runCheckTimer = true;
                    Timer checkTimer = new Timer("");
                    if (settingsController.getPlatform().equals("mac")) {
                        try {
                            FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + "/PortfolioData/" + "update.portfolio");
                            myWriter.write(settingsController.translationList.getValue().get("ConnectNode").toString());
                            myWriter.close();
                            try {
                                Process ps = null;
                                ps = Runtime.getRuntime().exec("./jre/bin/java -Xdock:icon=icons.icns -jar UpdateData.jar " + settingsController.selectedStyleMode.getValue().replace(" ", ""));
                            } catch (IOException r) {
                                settingsController.logger.warning("Exception occurred: " + r.toString());
                            }
                        } catch (IOException h) {
                            settingsController.logger.warning("Could not write to update.portfolio.");
                        }
                    } else {
                        transactionController.updateJFrame();
                        transactionController.jl.setText(settingsController.translationList.getValue().get("ConnectNode").toString());
                    }
                    checkTimer.scheduleAtFixedRate(new CheckConnection(mainViewController,settingsController,transactionController), 0, 30000);
                    break;
                case "Cake CSV":
                        transactionController.importCakeCSV();
                    break;
                case "Wallet CSV":
                       transactionController.importWalletCSV();
                    break;
                case "Show options":
                    if (this.stageUpdateData != null) this.stageUpdateData.close();
                    try {
                        Parent rootDisclaimer = null;
                        rootDisclaimer = fxmlLoader.load(getClass().getResource("ImportDataView.fxml")).getRoot();

                        Scene sceneUpdateData = new Scene(rootDisclaimer);
                        this.stageUpdateData = new Stage();
                   //     final Delta dragDelta = new Delta();
                        this.stageUpdateData.setScene(sceneUpdateData);
                        this.stageUpdateData.initStyle(StageStyle.UNDECORATED);
                        sceneUpdateData.setOnMousePressed(mouseEvent -> {
                            // record a delta distance for the drag and drop operation.
                            dragDelta.x = this.stageUpdateData.getX() - mouseEvent.getScreenX();
                            dragDelta.y = this.stageUpdateData.getY() - mouseEvent.getScreenY();
                        });
                        sceneUpdateData.setOnMouseDragged(mouseEvent -> {
                            this.stageUpdateData.setX(mouseEvent.getScreenX() + dragDelta.x);
                            this.stageUpdateData.setY(mouseEvent.getScreenY() + dragDelta.y);
                        });
                        this.stageUpdateData.getIcons().add(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/databaseprocess.png").toURI().toString()));
                        this.stageUpdateData.show();

                        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
                            this.stageUpdateData.getScene().getStylesheets().add(CssProvider.DARK_MODE);
                        } else {
                            this.stageUpdateData.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
                        }
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    break;
            }
        });

        tabPane.getSelectionModel().selectedItemProperty().addListener((ov, t, t1) ->
                {
                    if (!this.init)
                        mainViewController.plotUpdate(getSelectedItem(tabPane).getId());
                    cmbCoins.setVisible(true);
                    cmbFiat.setVisible(true);
                    cmbPlotCurrency.setVisible(true);
                    cmbCoinsCom.setVisible(true);
                    cmbFiatCom.setVisible(true);
                    cmbPlotCurrencyCom.setVisible(true);

                    this.updateHeader();
                }
        );

        this.cmbIntervall.valueProperty().bindBidirectional(settingsController.selectedInterval);
        this.cmbIntervall.valueProperty().addListener((ov, oldValue, newValue) ->
        {
            if (newValue != null) {
                settingsController.selectedIntervalInt = "Daily";
                if(settingsController.translationList.getValue().get("Daily").equals(newValue)){
                    settingsController.selectedIntervalInt = "Daily";
                }

                if(settingsController.translationList.getValue().get("Weekly").equals(newValue)){
                    settingsController.selectedIntervalInt = "Weekly";
                }

                if(settingsController.translationList.getValue().get("Monthly").equals(newValue)){
                    settingsController.selectedIntervalInt = "Monthly";
                }

                if(settingsController.translationList.getValue().get("Yearly").equals(newValue)){
                    settingsController.selectedIntervalInt = "Yearly";
                }
            }
            if (!this.init)
                mainViewController.plotUpdate(getSelectedItem(tabPane).getId());
            settingsController.saveSettings();
        });

        this.cmbIntervallCom.valueProperty().bindBidirectional(settingsController.selectedInterval);
        this.cmbIntervallOver.valueProperty().bindBidirectional(settingsController.selectedInterval);

        this.cmbCoins.getItems().addAll(settingsController.cryptoCurrencies);
        this.cmbCoins.valueProperty().bindBidirectional(settingsController.selectedCoin);
        this.cmbCoins.valueProperty().addListener((ov, oldValue, newValue) -> {
            if (!this.init)
                mainViewController.plotUpdate(getSelectedItem(tabPane).getId());

            this.updateHeader();
            settingsController.saveSettings();
            coinImageRewards1.setImage(IconProvider.getImage( settingsController.selectedCoin.getValue().split("-")[0].toLowerCase() + "-icon.png"));
            coinImageRewards2.setImage(IconProvider.getImage( settingsController.selectedCoin.getValue().split("-")[1].toLowerCase() + "-icon.png"));
            coinImageCommissions1.setImage(IconProvider.getImage( settingsController.selectedCoin.getValue().split("-")[0].toLowerCase() + "-icon.png"));
            coinImageCommissions2.setImage(IconProvider.getImage( settingsController.selectedCoin.getValue().split("-")[1].toLowerCase() + "-icon.png"));
        });

        this.cmbCoinsCom.getItems().addAll(settingsController.cryptoCurrencies);
        this.cmbCoinsCom.valueProperty().bindBidirectional(settingsController.selectedCoin);

        this.fiatColumn.setText(settingsController.translationList.getValue().get("Total") + " (" + settingsController.selectedFiatCurrency.getValue() + ")");
        this.crypto1Column.setText(settingsController.translationList.getValue().get("Rewards") + " (" + settingsController.selectedFiatCurrency.getValue() + ")");
        this.crypto2Column.setText(settingsController.translationList.getValue().get("Commissions") + " (" + settingsController.selectedFiatCurrency.getValue() + ")");
        this.crypto1FiatColumn.setText(settingsController.translationList.getValue().get("Rewards") + " (" + settingsController.selectedFiatCurrency.getValue() + ")");
        this.crypto2FiatColumn.setText(settingsController.translationList.getValue().get("Commissions") + " (" + settingsController.selectedFiatCurrency.getValue() + ")");


        settingsController.selectedFiatCurrency.addListener((ov, oldValue, newValue) ->

        {
            if (!oldValue.equals(newValue) & this.plotRewards != null) {
                if (!this.init) mainViewController.plotUpdate(getSelectedItem(tabPane).getId());
                settingsController.saveSettings();
                this.fiatColumn.setText(settingsController.translationList.getValue().get("Total") + " (" + newValue + ")");
                this.updateHeader();

            }

        });


        this.cmbFiatCom.getItems().addAll(settingsController.plotCurrency);
        this.cmbFiatCom.valueProperty().bindBidirectional(settingsController.selectedPlotCurrency);

        this.cmbFiat.getItems().addAll(settingsController.plotCurrency);
        this.cmbFiat.valueProperty().bindBidirectional(settingsController.selectedPlotCurrency);
        this.cmbFiat.valueProperty().addListener((ov, oldValue, newValue) ->
        {
            if (!this.init)
                mainViewController.plotUpdate(getSelectedItem(tabPane).getId());
            settingsController.saveSettings();

            this.updateHeader();

        });

        settingsController.selectedDecimal.addListener((ov, oldValue, newValue) ->
        {
            if (!oldValue.equals(newValue) & this.plotRewards != null) {
                mainViewController.plotUpdate(getSelectedItem(tabPane).getId());
            }
        });

        this.cmbPlotCurrency.valueProperty().bindBidirectional(settingsController.selectedPlotType);
        this.cmbPlotCurrency.valueProperty().addListener((ov, oldValue, newValue) ->
        {
            if (!this.init)
                mainViewController.plotUpdate(getSelectedItem(tabPane).getId());
        });

        this.cmbPlotCurrencyCom.valueProperty().bindBidirectional(settingsController.selectedPlotType);

        this.dateFrom.valueProperty().bindBidirectional(settingsController.dateFrom);
        this.dateFrom.valueProperty().addListener((ov, oldValue, newValue) ->
        {
            if (!this.init)
                mainViewController.plotUpdate(getSelectedItem(tabPane).getId());
        });

        this.dateFromCom.valueProperty().bindBidirectional(settingsController.dateFrom);
        this.dateFromOver.valueProperty().bindBidirectional(settingsController.dateFrom);

        this.dateFrom.setDayCellFactory(picker -> new
                DateCell() {
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || date.compareTo(today) > 0);
                    }
                });

        this.dateFromCom.setDayCellFactory(picker -> new
                DateCell() {
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || date.compareTo(today) > 0);
                    }
                });

        this.dateFromOver.setDayCellFactory(picker -> new
                DateCell() {
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || date.compareTo(today) > 0);
                    }
                });

        settingsController.selectedLanguage.addListener((ov, oldValue, newValue) -> this.

                updateLanguage());

        this.dateTo.valueProperty().

                bindBidirectional(settingsController.dateTo);
        this.dateTo.valueProperty().

                addListener((ov, oldValue, newValue) ->

                {
                    if (!this.init)
                        mainViewController.plotUpdate(getSelectedItem(tabPane).getId());
                });
        this.dateTo.setValue(LocalDate.now());
        this.dateTo.setDayCellFactory(picker -> new

                DateCell() {

                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || date.compareTo(today) > 0);
                    }
                });

        this.dateToOver.valueProperty().

                bindBidirectional(settingsController.dateTo);
        this.dateToOver.setValue(LocalDate.now());
        this.dateToOver.setDayCellFactory(picker -> new

                DateCell() {

                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || date.compareTo(today) > 0);
                    }
                });

        this.dateToCom.valueProperty().

                bindBidirectional(settingsController.dateTo);
        this.dateToCom.setValue(LocalDate.now());
        this.dateToCom.setDayCellFactory(picker -> new
                DateCell() {

                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        LocalDate today = LocalDate.now();
                        setDisable(empty || date.compareTo(today) > 0);
                    }
                });

        initializeTableViewContextMenu();

        initPlotTableContextMenu();

        rawDataTable.itemsProperty().

                set(mainViewController.getTransactionTable());

        rawDataTable.getSelectionModel().

                setSelectionMode(
                        SelectionMode.MULTIPLE
                );

        plotTable.itemsProperty().

                set(mainViewController.getPlotData());
        plotTable.getSelectionModel().

                setSelectionMode(
                        SelectionMode.MULTIPLE
                );
        timeStampColumn.setCellValueFactory(param -> param.getValue().

                getBlockTime());
        crypto1Column.setCellValueFactory(param -> param.getValue().

                getCryptoValue1().

                asObject());
        crypto2Column.setCellValueFactory(param -> param.getValue().

                getCryptoValue2().

                asObject());
        crypto1FiatColumn.setCellValueFactory(param -> param.getValue().

                getCryptoFiatValue1().

                asObject());
        crypto2FiatColumn.setCellValueFactory(param -> param.getValue().

                getCryptoFiatValue2().

                asObject());

        Commission2OverviewColumn.setCellValueFactory(param -> param.getValue().

                getcryptoCommission2Overview().

                asObject());
        Commission2OverviewFiatColumn.setCellValueFactory(param -> param.getValue().

                getcryptoCommission2FiaOtverview().

                asObject());
        fiatColumn.setCellValueFactory(param -> param.getValue().

                getFiatValue().

                asObject());
        poolPairColumn.setCellValueFactory(param -> param.getValue().
                getPoolPair());
        balanceFiatColumn.setCellValueFactory(param -> param.getValue().
                getBalanceFiat());
        ownerColumn.setCellValueFactory(param -> param.getValue().

                ownerProperty);
        blockTimeColumn.setCellValueFactory(param -> param.getValue().

                blockTimeProperty.

                asObject());
        typeColumn.setCellValueFactory(param -> param.getValue().

                typeProperty);
        cryptoCurrencyColumn.setCellValueFactory(param -> param.getValue().

                cryptoCurrencyProperty);
        cryptoValueColumn.setCellValueFactory(param -> param.getValue().

                cryptoValueProperty.

                asObject());
        blockHashColumn.setCellValueFactory(param -> param.getValue().

                blockHashProperty);
        blockHeightColumn.setCellValueFactory(param -> param.getValue().

                blockHeightProperty.

                asObject());
        poolIDColumn.setCellValueFactory(param -> param.getValue().

                poolIDProperty);
        fiatValueColumn.setCellValueFactory(param -> param.getValue().

                fiatValueProperty.

                asObject());
        fiatCurrencyColumn.setCellValueFactory(param -> param.getValue().

                fiatCurrencyProperty);
        transactionColumn.setCellValueFactory(param -> param.getValue().

                txIDProperty);


        Callback<TableColumn<TransactionModel, String>, TableCell<TransactionModel, String>> cellFactory0
                = (final TableColumn<TransactionModel, String> entry) -> new TableCell<TransactionModel, String>() {

            final Hyperlink hyperlink = new Hyperlink();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TransactionModel tempParam = rawDataTable.getItems().get(getIndex());
                    hyperlink.setText(item);
                    hyperlink.setOnAction((event) -> {
                        try {
                            if (settingsController.getPlatform().equals("linux")) {
                                // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
                                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://mainnet.defichain.io/#/DFI/mainnet/block/" + tempParam.blockHashProperty.getValue()});
                                } else {
                                    System.out.println("xdg-open is not supported!");
                                }
                            } else {
                                Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/block/" + tempParam.blockHashProperty.getValue()).toURI());
                            }
                        } catch (IOException | URISyntaxException e) {
                            settingsController.logger.warning("Exception occurred: " + e);
                        }
                    });
                    setGraphic(hyperlink);
                }
                setText(null);
            }
        };
        blockHashColumn.setCellFactory(cellFactory0);


        Callback<TableColumn<TransactionModel, String>, TableCell<TransactionModel, String>> cellFactory1
                = (final TableColumn<TransactionModel, String> entry) -> new TableCell<TransactionModel, String>() {

            final Hyperlink hyperlink = new Hyperlink();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TransactionModel tempParam = rawDataTable.getItems().get(getIndex());
                    hyperlink.setText(item);
                    hyperlink.setOnAction((event) -> {
                        try {
                            if (settingsController.getPlatform().equals("linux")) {
                                // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
                                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://mainnet.defichain.io/#/DFI/mainnet/address/" + tempParam.ownerProperty.getValue()});
                                } else {
                                    System.out.println("xdg-open is not supported!");
                                }
                            } else {
                                Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/address/" + tempParam.ownerProperty.getValue()).toURI());
                            }
                        } catch (IOException | URISyntaxException e) {
                            settingsController.logger.warning("Exception occurred: " + e);
                        }
                    });
                    setGraphic(hyperlink);
                }
                setText(null);
            }
        };
        ownerColumn.setCellFactory(cellFactory1);

        Callback<TableColumn<TransactionModel, Integer>, TableCell<TransactionModel, Integer>> cellFactory2
                = (final TableColumn<TransactionModel, Integer> entry) -> new TableCell<TransactionModel, Integer>() {

            final Hyperlink hyperlink = new Hyperlink();

            @Override
            public void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    TransactionModel tempParam = rawDataTable.getItems().get(getIndex());
                    hyperlink.setText(item.toString());
                    hyperlink.setOnAction((event) -> {
                        try {
                            if (settingsController.getPlatform().equals("linux")) {
                                // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
                                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://mainnet.defichain.io/#/DFI/mainnet/block/" + tempParam.blockHeightProperty.getValue()});
                                } else {
                                    System.out.println("xdg-open is not supported!");
                                }
                            } else {
                                Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/block/" + tempParam.blockHeightProperty.getValue()).toURI());
                            }
                        } catch (IOException | URISyntaxException e) {
                            settingsController.logger.warning("Exception occurred: " + e);
                        }
                    });
                    setGraphic(hyperlink);
                }
                setText(null);
            }
        };
        blockHeightColumn.setCellFactory(cellFactory2);

        Callback<TableColumn<TransactionModel, String>, TableCell<TransactionModel, String>> cellFactory3
                = (final TableColumn<TransactionModel, String> entry) -> new TableCell<TransactionModel, String>() {

            final Hyperlink hyperlink = new Hyperlink();

            @Override
            public void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    setText(null);
                } else {
                    TransactionModel tempParam = rawDataTable.getItems().get(getIndex());

                    if (tempParam.txIDProperty.getValue().equals("\"\"")) {
                        setText("-");
                        setGraphic(null);
                    } else {

                        hyperlink.setText(item);
                        hyperlink.setOnAction((event) -> {
                            try {
                                if (settingsController.getPlatform().equals("linux")) {
                                    // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
                                    if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                                        Runtime.getRuntime().exec(new String[]{"xdg-open", "https://mainnet.defichain.io/#/DFI/mainnet/tx/" + tempParam.txIDProperty.getValue()});
                                    } else {
                                        System.out.println("xdg-open is not supported!");
                                    }
                                } else {
                                    Desktop.getDesktop().browse(new URL("https://mainnet.defichain.io/#/DFI/mainnet/tx/" + tempParam.txIDProperty.getValue()).toURI());
                                }
                            } catch (IOException | URISyntaxException e) {
                                settingsController.logger.warning("Exception occurred: " + e);
                            }
                        });

                        setText(null);
                        setGraphic(hyperlink);
                    }
                }
            }
        };
        transactionColumn.setCellFactory(cellFactory3);


        poolIDColumn.setCellFactory(tc -> new TableCell<TransactionModel, String>() {
            @Override
            protected void updateItem(String poolID, boolean empty) {
                super.updateItem(poolID, empty);
                if (empty) {
                    setText(null);
                } else {

                    String pool = transactionController.getPoolPairFromId(poolID);
                    setText(pool);
                }
            }
        });

        fiatCurrencyColumn.setCellFactory(tc -> new TableCell<TransactionModel, String>() {
            @Override
            protected void updateItem(String fiatCurrency, boolean empty) {
                super.updateItem(fiatCurrency, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(settingsController.selectedFiatCurrency.getValue());
                }
            }
        });

        fiatColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double fiatValue, boolean empty) {
                super.updateItem(fiatValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", fiatValue));
                }
            }
        });

        fiatValueColumn.setCellFactory(tc -> new TableCell<TransactionModel, Double>() {
            @Override
            protected void updateItem(Double fiatValue, boolean empty) {
                super.updateItem(fiatValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", fiatValue));
                }
            }
        });

        crypto1Column.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

        crypto1FiatColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%,.2f", cryptoValue));
                }
            }
        });

        Commission2OverviewColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

        Commission2OverviewFiatColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%,.2f", cryptoValue));
                }
            }
        });

        crypto2Column.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

        crypto2FiatColumn.setCellFactory(tc -> new TableCell<PoolPairModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%,.2f", cryptoValue));
                }
            }
        });

        cryptoValueColumn.setCellFactory(tc -> new TableCell<TransactionModel, Double>() {
            @Override
            protected void updateItem(Double cryptoValue, boolean empty) {
                super.updateItem(cryptoValue, empty);
                if (empty) {
                    setText(null);
                } else {

                    Locale localeDecimal = Locale.GERMAN;
                    if (settingsController.selectedDecimal.getValue().equals(".")) {
                        localeDecimal = Locale.US;
                    }
                    setText(String.format(localeDecimal, "%.8f", cryptoValue));
                }
            }
        });

        blockTimeColumn.setCellFactory(tc -> new TableCell<TransactionModel, Long>() {
            @Override
            protected void updateItem(Long blockTime, boolean empty) {
                super.updateItem(blockTime, empty);
                if (empty) {
                    setText(null);
                } else {
                    Date date = new Date(blockTime*1000L );
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    setText(dateFormat.format(date));
                }
            }
        });
        this.init = false;
        this.btnAnalyse.fire();
    }

    private Tab getSelectedItem(TabPane tabPane) {
        return tabPane.getSelectionModel().getSelectedItem();
    }

    private void initializeTableViewContextMenu() {


        ContextMenu contextMenuRawData = new ContextMenu();

        //Init context menu of raw data table
        menuItemCopySelected.setOnAction(event -> mainViewController.copySelectedRawDataToClipboard(rawDataTable.selectionModelProperty().get().getSelectedItems(), false));
        menuItemCopyHeaderSelected.setOnAction(event -> mainViewController.copySelectedRawDataToClipboard(rawDataTable.selectionModelProperty().get().getSelectedItems(), true));
        menuItemExportCointracking.setOnAction(event -> mainViewController.exportTransactionToExcel(rawDataTable.getItems(), "COIN"));
        menuItemExportToCSV.setOnAction(event -> mainViewController.exportTransactionToExcel(rawDataTable));


        contextMenuRawData.getItems().add(menuItemCopySelected);
        contextMenuRawData.getItems().add(menuItemCopyHeaderSelected);
      //  contextMenuRawData.getItems().add(menuItemExportSelected);
     //   contextMenuRawData.getItems().add(menuItemExportAllSelected);
      //  contextMenuRawData.getItems().add(menuItemExportAllDailySelected);
        contextMenuRawData.getItems().add(menuItemExportCointracking);
        contextMenuRawData.getItems().add(menuItemExportToCSV);

        this.rawDataTable.contextMenuProperty().set(contextMenuRawData);
    }

    private void initPlotTableContextMenu() {

        //Init context menu of plot table

        ContextMenu contextMenuPlotData = new ContextMenu();
        menuItemCopySelectedPlot.setOnAction(event -> mainViewController.copySelectedDataToClipboard(plotTable.selectionModelProperty().get().getSelectedItems(), false));
        menuItemCopyHeaderSelectedPlot.setOnAction(event -> mainViewController.copySelectedDataToClipboard(plotTable.selectionModelProperty().get().getSelectedItems(), true));
        menuItemExportSelectedPlot.setOnAction(event -> mainViewController.exportPoolPairToExcel(plotTable.selectionModelProperty().get().getSelectedItems(), getSelectedItem(this.tabPane).getId()));
        menuItemExportAllSelectedPlot.setOnAction(event -> mainViewController.exportPoolPairToExcel(plotTable.getItems(), getSelectedItem(this.tabPane).getId()));
       // menuItemExportToCSV.setOnAction(event -> mainViewController.exportPoolPairToExcel(plotTable.getItems(), this.tabPane.getSelectionModel().getSelectedItem().getId()));


        contextMenuPlotData.getItems().add(menuItemCopySelectedPlot);
        contextMenuPlotData.getItems().add(menuItemCopyHeaderSelectedPlot);
        contextMenuPlotData.getItems().add(menuItemExportSelectedPlot);
        contextMenuPlotData.getItems().add(menuItemExportAllSelectedPlot);
     //   contextMenuPlotData.getItems().add(menuItemExportToCSV);

        this.plotTable.contextMenuProperty().set(contextMenuPlotData);
    }

    private void updateStylesheet() {

        this.mainAnchorPane.getStylesheets().clear();
        if (this.helpStage != null) this.helpStage.getScene().getStylesheets().clear();
        if (this.settingsStage != null) this.settingsStage.getScene().getStylesheets().clear();
        if (this.donateStage != null) this.donateStage.getScene().getStylesheets().clear();

        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            this.mainAnchorPane.getStylesheets().add(CssProvider.DARK_MODE);
            if (this.helpStage != null)
                this.helpStage.getScene().getStylesheets().add(CssProvider.DARK_MODE);
            if (this.settingsStage != null)
                this.settingsStage.getScene().getStylesheets().add(CssProvider.DARK_MODE);
            if (this.donateStage != null)
                this.donateStage.getScene().getStylesheets().add(CssProvider.DARK_MODE);
        } else {
            this.mainAnchorPane.getStylesheets().add(CssProvider.LIGHT_MODE);
            if (this.helpStage != null)
                this.helpStage.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
            if (this.settingsStage != null)
                this.settingsStage.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
            if (this.donateStage != null)
                this.donateStage.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
        }
    }

    private void updateLanguage() {
        settingsController.updateLanguage();
        this.btnRawData.textProperty().setValue(settingsController.translationList.getValue().get("RawData").toString());
        this.menuItemCopySelected.setText(settingsController.translationList.getValue().get("Copy").toString());
        this.menuItemCopyHeaderSelected.setText(settingsController.translationList.getValue().get("CopyHeader").toString());
        this.menuItemExportSelected.setText(settingsController.translationList.getValue().get("ExportSelected").toString());
        this.menuItemExportAllSelected.setText(settingsController.translationList.getValue().get("ExportAll").toString());
        this.menuItemExportAllDailySelected.setText(settingsController.translationList.getValue().get("ExportAllDaily").toString());
        this.menuItemCopySelectedPlot.setText(settingsController.translationList.getValue().get("Copy").toString());
        this.menuItemCopyHeaderSelectedPlot.setText(settingsController.translationList.getValue().get("CopyHeader").toString());
        this.menuItemExportSelectedPlot.setText(settingsController.translationList.getValue().get("ExportSelected").toString());
        this.menuItemExportAllSelectedPlot.setText(settingsController.translationList.getValue().get("ExportAll").toString());
        this.menuItemExportCointracking.setText(settingsController.translationList.getValue().get("ExportCointracking").toString());
        this.menuItemExportToCSV.setText(settingsController.translationList.getValue().get("ExportCSV").toString());
        this.CurrentBlock.setText(settingsController.translationList.getValue().get("CurrentBlock").toString());
        this.CurrentBlockChain.setText(settingsController.translationList.getValue().get("CurrentBlockBC").toString());
        this.LastUpdate.setText(settingsController.translationList.getValue().get("LastUpdate").toString());
        this.btnSettings.setText(settingsController.translationList.getValue().get("Settings").toString());
        this.btnDonate.setText(settingsController.translationList.getValue().get("Donate").toString());
        this.btnBuyDFI.setText(settingsController.translationList.getValue().get("BuyDFI").toString());
        this.Rewards.setText(settingsController.translationList.getValue().get("Rewards").toString());
        this.Commissions.setText(settingsController.translationList.getValue().get("Commissions").toString());
        this.Overview.setText(settingsController.translationList.getValue().get("Overview").toString());
        this.StartDate.setText(settingsController.translationList.getValue().get("StartDate").toString());
        this.EndDate.setText(settingsController.translationList.getValue().get("EndDate").toString());
        this.StartDateCom.setText(settingsController.translationList.getValue().get("StartDate").toString());
        this.EndDateCom.setText(settingsController.translationList.getValue().get("EndDate").toString());
        this.StartDateOver.setText(settingsController.translationList.getValue().get("StartDate").toString());
        this.EndDateOver.setText(settingsController.translationList.getValue().get("EndDate").toString());
        this.btnAnalyse.setText(settingsController.translationList.getValue().get("AnalyseData").toString());
        this.btnUpdateDatabase.setText(settingsController.translationList.getValue().get("UpdateData").toString());
        this.btnHelp.setText(settingsController.translationList.getValue().get("Help").toString());
        if (this.cmbIntervall.getItems().size() > 0) {
            this.cmbIntervallCom.getItems().set(0, settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervallCom.getItems().set(1, settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervallCom.getItems().set(2, settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervallCom.getItems().set(3, settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbIntervallOver.getItems().set(0, settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervallOver.getItems().set(1, settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervallOver.getItems().set(2, settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervallOver.getItems().set(3, settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbIntervall.getItems().set(0, settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervall.getItems().set(1, settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervall.getItems().set(2, settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervall.getItems().set(3, settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbPlotCurrency.getItems().set(0, settingsController.translationList.getValue().get("Individual").toString());
            this.cmbPlotCurrency.getItems().set(1, settingsController.translationList.getValue().get("Cumulated").toString());
            this.cmbPlotCurrencyCom.getItems().add(0, settingsController.translationList.getValue().get("Individual").toString());
            this.cmbPlotCurrencyCom.getItems().add(1, settingsController.translationList.getValue().get("Cumulated").toString());

        } else {

            this.cmbIntervallCom.getItems().add(settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervallCom.getItems().add(settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervallCom.getItems().add(settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervallCom.getItems().add(settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbIntervallOver.getItems().add(settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervallOver.getItems().add(settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervallOver.getItems().add(settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervallOver.getItems().add(settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbIntervall.getItems().add(settingsController.translationList.getValue().get("Daily").toString());
            this.cmbIntervall.getItems().add(settingsController.translationList.getValue().get("Weekly").toString());
            this.cmbIntervall.getItems().add(settingsController.translationList.getValue().get("Monthly").toString());
            this.cmbIntervall.getItems().add(settingsController.translationList.getValue().get("Yearly").toString());
            this.cmbPlotCurrency.getItems().add(settingsController.translationList.getValue().get("Individual").toString());
            this.cmbPlotCurrency.getItems().add(settingsController.translationList.getValue().get("Cumulated").toString());
            this.cmbPlotCurrencyCom.getItems().add(settingsController.translationList.getValue().get("Individual").toString());
            this.cmbPlotCurrencyCom.getItems().add(settingsController.translationList.getValue().get("Cumulated").toString());
        }
        this.btnConnect.setText(settingsController.translationList.getValue().get("openAddressConfig").toString());
//        this.connectionLabel.getTooltip().setText(settingsController.translationList.getValue().get("UpdateTooltip").toString());
        this.blockTimeColumn.setText(settingsController.translationList.getValue().get("Date").toString());
        this.timeStampColumn.setText(settingsController.translationList.getValue().get("Date").toString());
        this.typeColumn.setText(settingsController.translationList.getValue().get("Operation").toString());
        this.cryptoValueColumn.setText(settingsController.translationList.getValue().get("CryptoValue").toString());
        this.cryptoCurrencyColumn.setText(settingsController.translationList.getValue().get("CryptoCurrency").toString());
        this.fiatValueColumn.setText(settingsController.translationList.getValue().get("FIATValue").toString());
        this.fiatCurrencyColumn.setText(settingsController.translationList.getValue().get("FIATCurrency").toString());
        this.poolIDColumn.setText(settingsController.translationList.getValue().get("PoolPair").toString());
        this.poolPairColumn.setText(settingsController.translationList.getValue().get("PoolPair").toString());
        this.blockHeightColumn.setText(settingsController.translationList.getValue().get("BlockHeight").toString());
        this.blockHashColumn.setText(settingsController.translationList.getValue().get("BlockHash").toString());
        this.ownerColumn.setText(settingsController.translationList.getValue().get("Owner").toString());
        this.transactionColumn.setText(settingsController.translationList.getValue().get("TransactionHash").toString());
        this.fiatColumn.setText(settingsController.translationList.getValue().get("Total").toString());
        mainViewController.donateController.strDonateText.setValue(settingsController.translationList.getValue().get("DonateLabel").toString());
        settingsController.selectedPlotType.setValue(settingsController.translationList.getValue().get("Individual").toString());
        settingsController.selectedInterval.setValue(settingsController.translationList.getValue().get("Daily").toString());


        if (!this.init)
            this.updateHeader();
    }

    public void showMissingTransactionWindow(){
        Parent rootMissingTransaction = null;
        try {
            rootMissingTransaction = fxmlLoader.load(getClass().getResource("MissingTransactionView.fxml")).getRoot();
        } catch (IOException e) {
            settingsController.logger.warning(e.toString());
        }
        final Delta dragDelta = new Delta();
        Scene sceneMissingTransaction = new Scene(rootMissingTransaction);
        Stage stageMissingTransaction = new Stage();
        stageMissingTransaction.setTitle("DeFi-Portfolio Disclaimer");
        stageMissingTransaction.setScene(sceneMissingTransaction);
        stageMissingTransaction.initStyle(StageStyle.UNDECORATED);
        sceneMissingTransaction.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = stageMissingTransaction.getX() - mouseEvent.getScreenX();
            dragDelta.y = stageMissingTransaction.getY() - mouseEvent.getScreenY();
        });
        sceneMissingTransaction.setOnMouseDragged(mouseEvent -> {
            stageMissingTransaction.setX(mouseEvent.getScreenX() + dragDelta.x);
            stageMissingTransaction.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        stageMissingTransaction.show();
        stageMissingTransaction.setAlwaysOnTop(true);

        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            stageMissingTransaction.getScene().getStylesheets().add(CssProvider.DARK_MODE);
        } else {
            stageMissingTransaction.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
        }
    }

    public void showNoDataWindow(){
        Parent root = null;
        try {
            root = fxmlLoader.load(getClass().getResource("NoDataView.fxml")).getRoot();
            Scene scene = new Scene(root);
            Stage infoView = new Stage();
            infoView.initStyle(StageStyle.UNDECORATED);
            final Delta dragDelta = new Delta();
            scene.setOnMousePressed(mouseEvent -> {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = infoView.getX() - mouseEvent.getScreenX();
                dragDelta.y = infoView.getY() - mouseEvent.getScreenY();
            });
            scene.setOnMouseDragged(mouseEvent -> {
                infoView.setX(mouseEvent.getScreenX() + dragDelta.x);
                infoView.setY(mouseEvent.getScreenY() + dragDelta.y);
            });
            infoView.getIcons().add(new Image(new File(System.getProperty("user.dir") + "/defi-portfolio/src/icons/settings.png").toURI().toString()));
            infoView.setTitle(settingsController.translationList.getValue().get("Settings").toString());
            infoView.setScene(scene);

            if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
                infoView.getScene().getStylesheets().add(CssProvider.DARK_MODE);
            } else {
                infoView.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
            }

            infoView.show();
        } catch (IOException e) {
            settingsController.logger.warning(e.toString());
        }
    }
    public void openDFXHomepage(){
        if (settingsController.getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://dfx.swiss/app?code=DFI-POR"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://dfx.swiss/app?code=DFI-POR").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
    public void showFileTypeNotSupported(){
        Parent rootFileTypeNotSupported = null;
        try {
            rootFileTypeNotSupported = fxmlLoader.load(getClass().getResource("FileTypeNotSupportedView.fxml")).getRoot();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene sceneFileTypeNotSupported = new Scene(rootFileTypeNotSupported);
        Stage stageFileTypeNotSupported = new Stage();
        stageFileTypeNotSupported.setTitle("Filetype not supported!");
        stageFileTypeNotSupported.setScene(sceneFileTypeNotSupported);
        stageFileTypeNotSupported.initStyle(StageStyle.UNDECORATED);
        final Delta dragDelta = new Delta();
        sceneFileTypeNotSupported.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = stageFileTypeNotSupported.getX() - mouseEvent.getScreenX();
            dragDelta.y = stageFileTypeNotSupported.getY() - mouseEvent.getScreenY();
        });
        sceneFileTypeNotSupported.setOnMouseDragged(mouseEvent -> {
            stageFileTypeNotSupported.setX(mouseEvent.getScreenX() + dragDelta.x);
            stageFileTypeNotSupported.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        stageFileTypeNotSupported.show();
        stageFileTypeNotSupported.setAlwaysOnTop(true);

        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            stageFileTypeNotSupported.getScene().getStylesheets().add(CssProvider.DARK_MODE);
        } else {
            stageFileTypeNotSupported.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
        }
    }

    public void showRestartWindow(){
        Parent rootRestartTool = null;
        try {
            rootRestartTool = fxmlLoader.load(getClass().getResource("RestartToolView.fxml")).getRoot();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Scene sceneRestartTool = new Scene(rootRestartTool);
        Stage stageRestartTool = new Stage();
        stageRestartTool.setTitle("Restart Tool");
        stageRestartTool.setScene(sceneRestartTool);
        stageRestartTool.initStyle(StageStyle.UNDECORATED);
        final Delta dragDelta = new Delta();
        sceneRestartTool.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = stageRestartTool.getX() - mouseEvent.getScreenX();
            dragDelta.y = stageRestartTool.getY() - mouseEvent.getScreenY();
        });
        sceneRestartTool.setOnMouseDragged(mouseEvent -> {
            stageRestartTool.setX(mouseEvent.getScreenX() + dragDelta.x);
            stageRestartTool.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        stageRestartTool.show();
        stageRestartTool.setAlwaysOnTop(true);

        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            stageRestartTool.getScene().getStylesheets().add(CssProvider.DARK_MODE);
        } else {
            stageRestartTool.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
        }
    }
}
