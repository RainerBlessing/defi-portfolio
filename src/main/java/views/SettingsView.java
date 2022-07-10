package views;

import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.inject.Inject;
import controllers.MainViewController;
import controllers.SettingsController;
import controllers.TransactionController;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

public class SettingsView implements Initializable {
    public Button btnSaveAndApply;
    public Label labelLanguage;
    public Label CSV;
    public Label prefferedCurrency;
    public Label prefferedStyle;
    public Label labelDec;
    public Label lblLaunchDefid;
    public Label lblDeleteData;
    public AnchorPane anchorPane;
    public Label labelDataSource;
    public Label lblCloseDefid;
    public Label lblOpenProjectFolder;
    public Label lblOpenInstallationFolder;
    public Label lblOpenAdressConfig;
    public Label labelCointrackingExport;
    public Label labelCSVExport;
    public Label lblDefaultDataSource;
    public Button btnCloseDefid;
    public Button btnOpenProjectFolder;
    public Button btnOpenInstallationFolder;
    public Button btnOpenAdressConfig;
    public Label lblFrom;
    public Label lblTo;
    public DatePicker exportFrom;
    public DatePicker exportTo;
    public Stage AddressConfigStage;
    @FXML
    public StackPane stack;
    @FXML
    public Button switchButton;
    @FXML
    public Button btnDeleteData;
    @FXML
    private ComboBox<String> cmbLanguage, cmbPrefCurrency, cmbDecSeperator, cmbCSVSeperator, cmbPrefferedStyle, dataSourceCmb,cmbDefaultDataSource,cointrackingExportCmb, CSVExportcmb;
    @Inject
    private TransactionController transactionController;
    @Inject
    private SettingsController settingsController;
    @Inject
    private MainViewController mainViewController;
    @Inject
    private GuiceFXMLLoader fxmlLoader;

    public void btnSaveAndApplyPressed() {
        this.settingsController.saveSettings();
        Stage stage = (Stage) btnSaveAndApply.getScene().getWindow();
        stage.close();
    }

    public void btnDeletePressed() {
        boolean result = false;
        try {
            result = Files.deleteIfExists(new File(settingsController.DEFI_PORTFOLIO_HOME + "/transactionData.portfolio").toPath());
            result = Files.deleteIfExists(new File(settingsController.DEFI_PORTFOLIO_HOME + "/portfolioData.portfolio").toPath());

        } catch (IOException e) {
            settingsController.logger.warning("Exception occurred: " + e);
        }
        if(result){
//            transactionController.clearTransactionList();
//            transactionController.clearPortfolioList();
//            mainViewController.poolPairList.clear();
//            mainViewController.plotUpdate(mainViewController.mainView.tabPane.getSelectionModel().getSelectedItem().getId());
//            mainViewController.strCurrentBlockLocally.set("0");
//            transactionController.clearBalanceList();
        }else{
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.labelLanguage.setText(settingsController.getTranslationValue("LanguageLabel").toString());
      //  this.CSV.setText(settingsController.getTranslationValue("CSV").toString());
        this.prefferedCurrency.setText(settingsController.getTranslationValue("PrefferedCurrency").toString());
      //  this.labelDec.setText(settingsController.getTranslationValue("Decimal").toString());
        this.lblLaunchDefid.setText(settingsController.getTranslationValue("LaunchDefid").toString());
        this.cmbLanguage.getItems().addAll(this.settingsController.languages);
        this.cmbLanguage.valueProperty().bindBidirectional(this.settingsController.selectedLanguage);
        this.cointrackingExportCmb.valueProperty().bindBidirectional(this.settingsController.exportCointracingVariant);
        this.cointrackingExportCmb.getItems().addAll(this.settingsController.cointrackingExportVariants);
        this.CSVExportcmb.valueProperty().bindBidirectional(this.settingsController.exportCSVVariant);
        this.CSVExportcmb.getItems().addAll(this.settingsController.csvExportVariants);

        this.lblDeleteData.setText(settingsController.getTranslationValue("DeleteLabel").toString());
        this.btnDeleteData.setText(settingsController.getTranslationValue("DeleteButton").toString());
        this.labelCointrackingExport.setText(settingsController.getTranslationValue("CointrackingLabel").toString());
        this.labelCSVExport.setText(settingsController.getTranslationValue("CSVLabel").toString());
        this.lblFrom.setText(settingsController.getTranslationValue("ToLabel").toString());
        this.lblTo.setText(settingsController.getTranslationValue("FromLabel").toString());

        this.exportFrom.valueProperty().bindBidirectional(this.settingsController.exportFrom);
        this.exportTo.valueProperty().bindBidirectional(this.settingsController.exportTo);

        this.cmbPrefCurrency.getItems().addAll(this.settingsController.currencies);
        this.cmbPrefCurrency.valueProperty().bindBidirectional(this.settingsController.selectedFiatCurrency);
//        this.cmbDecSeperator.getItems().addAll(this.settingsController.decSeperators);
//        this.cmbDecSeperator.valueProperty().bindBidirectional(this.settingsController.selectedDecimal);
//
//        this.cmbCSVSeperator.getItems().addAll(this.settingsController.csvSeperators);
//        this.cmbCSVSeperator.valueProperty().bindBidirectional(this.settingsController.selectedSeperator);

        this.cmbPrefferedStyle.getItems().addAll(this.settingsController.styleModes);
        this.cmbPrefferedStyle.valueProperty().bindBidirectional(this.settingsController.selectedStyleMode);

        this.dataSourceCmb.getItems().addAll(this.settingsController.datasources);
        this.dataSourceCmb.valueProperty().bindBidirectional(this.settingsController.selectedSource);

        this.lblCloseDefid.setText(settingsController.getTranslationValue("CloseDefid").toString());
        this.lblOpenProjectFolder.setText(settingsController.getTranslationValue("OpenProjectFolder").toString());
        this.lblOpenInstallationFolder.setText(settingsController.getTranslationValue("OpenInstallFolder").toString());
        this.btnCloseDefid.setText(settingsController.getTranslationValue("CloseButton").toString());
        this.btnOpenProjectFolder.setText(settingsController.getTranslationValue("Open").toString());
        this.btnOpenInstallationFolder.setText(settingsController.getTranslationValue("Open").toString());
        this.btnOpenAdressConfig.setText(settingsController.getTranslationValue("Open").toString());
        this.lblOpenAdressConfig.setText(settingsController.getTranslationValue("openAddressConfig").toString());

        this.lblDefaultDataSource.setText(settingsController.getTranslationValue("DefaultDataUpdate").toString());
        this.cmbDefaultDataSource.getItems().addAll(this.settingsController.defaultUpdateSource);
        this.cmbDefaultDataSource.valueProperty().bindBidirectional(this.settingsController.selectedDefaultUpdateSource);
        this.SwitchButton();
    }

    public void changeLanguage() {
        this.labelLanguage.setText(settingsController.getTranslationValue("LanguageLabel").toString());
   //     this.CSV.setText(settingsController.getTranslationValue("CSV").toString());
        this.prefferedCurrency.setText(settingsController.getTranslationValue("PrefferedCurrency").toString());
  //      this.labelDec.setText(settingsController.getTranslationValue("Decimal").toString());
        this.lblLaunchDefid.setText(settingsController.getTranslationValue("LaunchDefid").toString());
        this.lblDeleteData.setText(settingsController.getTranslationValue("DeleteLabel").toString());
        this.btnDeleteData.setText(settingsController.getTranslationValue("DeleteButton").toString());
        this.labelDataSource.setText(settingsController.getTranslationValue("DataSourceLabel").toString());
        this.lblCloseDefid.setText(settingsController.getTranslationValue("CloseDefid").toString());
        this.lblOpenProjectFolder.setText(settingsController.getTranslationValue("OpenProjectFolder").toString());
        this.lblOpenInstallationFolder.setText(settingsController.getTranslationValue("OpenInstallFolder").toString());
        this.btnCloseDefid.setText(settingsController.getTranslationValue("CloseButton").toString());
        this.btnOpenProjectFolder.setText(settingsController.getTranslationValue("Open").toString());
        this.btnOpenInstallationFolder.setText(settingsController.getTranslationValue("Open").toString());
        this.labelCointrackingExport.setText(settingsController.getTranslationValue("CointrackingLabel").toString());
        this.labelCSVExport.setText(settingsController.getTranslationValue("CSVLabel").toString());
        this.lblFrom.setText(settingsController.getTranslationValue("ToLabel").toString());
        this.lblTo.setText(settingsController.getTranslationValue("FromLabel").toString());
        this.lblDefaultDataSource.setText(settingsController.getTranslationValue("DefaultDataUpdate").toString());
        this.btnOpenAdressConfig.setText(settingsController.getTranslationValue("Open").toString());
        this.lblOpenAdressConfig.setText(settingsController.getTranslationValue("openAddressConfig").toString());
    }

    private final Rectangle back = new Rectangle(35, 15, Color.RED);
    private final Rectangle backSync = new Rectangle(35, 15, Color.RED);
    private final String buttonStyleOff = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: #d6cecc;";
    private final String buttonStyleOn = "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 0.2, 0.0, 0.0, 2); -fx-background-color: #FF00AF;"; //00893d

    private void init() {
        stack.getChildren().addAll(back, switchButton);
        stack.setMinSize(35, 15);
        back.maxWidth(35);
        back.minWidth(30);
        back.maxHeight(15);
        back.minHeight(10);
        back.setArcHeight(back.getHeight());
        back.setArcWidth(back.getHeight());
        back.setFill(Color.valueOf("#d6cecc"));//Grau
        double r = 3.0;
        switchButton.setShape(new Circle(r));

        switchButton.setMaxSize(20, 20);
        switchButton.setMinSize(20, 20);

        if (this.settingsController.selectedLaunchDefid) {
            switchButton.setStyle(buttonStyleOn);
            back.setFill(Color.valueOf("#FF00AF"));//Weiß
            StackPane.setAlignment(switchButton, Pos.CENTER_RIGHT);
        } else {
            switchButton.setStyle(buttonStyleOff);
            back.setFill(Color.valueOf("#d6cecc"));//Rosa
            StackPane.setAlignment(switchButton, Pos.CENTER_LEFT);

        }
    }


    public void updateSwitchButton() {

        if (this.settingsController.selectedLaunchDefid) {
            switchButton.setStyle(buttonStyleOff);
            back.setFill(Color.valueOf("#d6cecc"));//Weiß
            StackPane.setAlignment(switchButton, Pos.CENTER_LEFT);
            this.settingsController.selectedLaunchDefid = false;
        } else {
            switchButton.setStyle(buttonStyleOn);
            back.setFill(Color.valueOf("#FF00AF"));//Rosa
            StackPane.setAlignment(switchButton, Pos.CENTER_RIGHT);
            this.settingsController.selectedLaunchDefid = true;
        }

    }

    public void SwitchButton() {
        init();
        EventHandler<Event> click = e -> updateSwitchButton();
        switchButton.setFocusTraversable(false);
        switchButton.setOnMouseClicked(click);
        stack.setOnMouseClicked(click);
    }

    public void btnCloseDefidPressed(){
        transactionController.stopServer();
    }
    public void btnOpenProjectFolderPressed(){

        if (settingsController.getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            File file = new File(settingsController.DEFI_PORTFOLIO_HOME);
            try {
                Runtime.getRuntime().exec(new String[]{"xdg-open", file.getAbsolutePath()});
            } catch (IOException e) {
                settingsController.logger.warning(e.toString());
            }
        } else {
            try {
                Desktop.getDesktop().open(new File(settingsController.DEFI_PORTFOLIO_HOME));
        } catch (IOException e) {
            settingsController.logger.warning(e.toString());
        }
        }
    }
    public void btnOpenInstallationFolderPressed(){

        if (settingsController.getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            File file = new File(System.getProperty("user.dir"));
            try {
                Runtime.getRuntime().exec(new String[]{"xdg-open", file.getAbsolutePath()});
            } catch (IOException e) {
                settingsController.logger.warning(e.toString());
            }
        } else {
            try {
                Desktop.getDesktop().open(new File(System.getProperty("user.dir")));
            } catch (IOException e) {
                settingsController.logger.warning(e.toString());
            }
        }
    }
    public void btnOpenAdressConfig() throws IOException {
        if (AddressConfigStage != null) AddressConfigStage.close();
        final Delta dragDelta = new Delta();
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
        //AddressConfigStage.setTitle(this.mainViewController.settingsController.getTranslationValue("Settings").toString());
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

        File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
        File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            AddressConfigStage.getScene().getStylesheets().add(darkMode.toURI().toString());
        } else {
            AddressConfigStage.getScene().getStylesheets().add(lightMode.toURI().toString());
        }
    }
    static class Delta {
        double x, y;
    }
}





