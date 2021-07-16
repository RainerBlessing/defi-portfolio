package portfolio.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import portfolio.controllers.MainViewController;
import portfolio.controllers.SettingsController;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import portfolio.controllers.TransactionController;
import sun.security.krb5.internal.crypto.Des;

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
    public Label labelCointrackingExport;
    public Label labelCSVExport;
    public Label lblDefaultDataSource;
    public Button btnCloseDefid;
    public Button btnOpenProjectFolder;
    public Button btnOpenInstallationFolder;
    public Label lblFrom;
    public Label lblTo;
    public DatePicker exportFrom;
    public DatePicker exportTo;
    @FXML
    public StackPane stack;
    @FXML
    public Button switchButton;
    @FXML
    public Button btnDeleteData;
    @FXML
    private ComboBox<String> cmbLanguage, cmbPrefCurrency, cmbDecSeperator, cmbCSVSeperator, cmbPrefferedStyle, dataSourceCmb,cmbDefaultDataSource,cointrackingExportCmb, CSVExportcmb;
    SettingsController settingsController = SettingsController.getInstance();

    public void btnSaveAndApplyPressed() {
        this.settingsController.saveSettings();
        Stage stage = (Stage) btnSaveAndApply.getScene().getWindow();
        stage.close();
    }

    public void btnDeletePressed() {
        boolean result = false;
        try {
            result = Files.deleteIfExists(new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + "/transactionData.portfolio").toPath());
            result = Files.deleteIfExists(new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + "/portfolioData.portfolio").toPath());

        } catch (IOException e) {
            SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
        }
        if(result == true){
            TransactionController.getInstance().clearTransactionList();
            TransactionController.getInstance().clearPortfolioList();
            MainViewController.getInstance().poolPairList.clear();
            MainViewController.getInstance().plotUpdate(MainViewController.getInstance().mainView.tabPane.getSelectionModel().getSelectedItem().getId());
            MainViewController.getInstance().strCurrentBlockLocally.set("0");
            TransactionController.getInstance().clearBalanceList();
        }else{
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        this.labelLanguage.setText(this.settingsController.translationList.getValue().get("LanguageLabel").toString());
      //  this.CSV.setText(this.settingsController.translationList.getValue().get("CSV").toString());
        this.prefferedCurrency.setText(this.settingsController.translationList.getValue().get("PrefferedCurrency").toString());
      //  this.labelDec.setText(this.settingsController.translationList.getValue().get("Decimal").toString());
        this.lblLaunchDefid.setText(this.settingsController.translationList.getValue().get("LaunchDefid").toString());
        this.cmbLanguage.getItems().addAll(this.settingsController.languages);
        this.cmbLanguage.valueProperty().bindBidirectional(this.settingsController.selectedLanguage);
        this.cointrackingExportCmb.valueProperty().bindBidirectional(this.settingsController.exportCointracingVariante);
        this.cointrackingExportCmb.getItems().addAll(this.settingsController.cointrackingExportVariants);
        this.CSVExportcmb.valueProperty().bindBidirectional(this.settingsController.exportCSVCariante);
        this.CSVExportcmb.getItems().addAll(this.settingsController.csvExportVariants);

        this.lblDeleteData.setText(this.settingsController.translationList.getValue().get("DeleteLabel").toString());
        this.btnDeleteData.setText(this.settingsController.translationList.getValue().get("DeleteButton").toString());
        this.labelCointrackingExport.setText(this.settingsController.translationList.getValue().get("CointrackingLabel").toString());
        this.labelCSVExport.setText(this.settingsController.translationList.getValue().get("CSVLabel").toString());
        this.lblFrom.setText(this.settingsController.translationList.getValue().get("ToLabel").toString());
        this.lblTo.setText(this.settingsController.translationList.getValue().get("FromLabel").toString());

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

        this.lblCloseDefid.setText(this.settingsController.translationList.getValue().get("CloseDefid").toString());
        this.lblOpenProjectFolder.setText(this.settingsController.translationList.getValue().get("OpenProjectFolder").toString());
        this.lblOpenInstallationFolder.setText(this.settingsController.translationList.getValue().get("OpenInstallFolder").toString());
        this.btnCloseDefid.setText(this.settingsController.translationList.getValue().get("CloseButton").toString());
        this.btnOpenProjectFolder.setText(this.settingsController.translationList.getValue().get("Open").toString());
        this.btnOpenInstallationFolder.setText(this.settingsController.translationList.getValue().get("Open").toString());

        this.lblDefaultDataSource.setText(this.settingsController.translationList.getValue().get("DefaultDataUpdate").toString());
        this.cmbDefaultDataSource.getItems().addAll(this.settingsController.defaultUpdateSource);
        this.cmbDefaultDataSource.valueProperty().bindBidirectional(this.settingsController.selectedDefaulUpdateSource);
        this.SwitchButton();
    }

    public void changeLanguage() {
        this.labelLanguage.setText(this.settingsController.translationList.getValue().get("LanguageLabel").toString());
   //     this.CSV.setText(this.settingsController.translationList.getValue().get("CSV").toString());
        this.prefferedCurrency.setText(this.settingsController.translationList.getValue().get("PrefferedCurrency").toString());
  //      this.labelDec.setText(this.settingsController.translationList.getValue().get("Decimal").toString());
        this.lblLaunchDefid.setText(this.settingsController.translationList.get().get("LaunchDefid").toString());
        this.lblDeleteData.setText(this.settingsController.translationList.getValue().get("DeleteLabel").toString());
        this.btnDeleteData.setText(this.settingsController.translationList.getValue().get("DeleteButton").toString());
        this.labelDataSource.setText(this.settingsController.translationList.getValue().get("DataSourceLabel").toString());
        this.lblCloseDefid.setText(this.settingsController.translationList.getValue().get("CloseDefid").toString());
        this.lblOpenProjectFolder.setText(this.settingsController.translationList.getValue().get("OpenProjectFolder").toString());
        this.lblOpenInstallationFolder.setText(this.settingsController.translationList.getValue().get("OpenInstallFolder").toString());
        this.btnCloseDefid.setText(this.settingsController.translationList.getValue().get("CloseButton").toString());
        this.btnOpenProjectFolder.setText(this.settingsController.translationList.getValue().get("Open").toString());
        this.btnOpenInstallationFolder.setText(this.settingsController.translationList.getValue().get("Open").toString());
        this.labelCointrackingExport.setText(this.settingsController.translationList.getValue().get("CointrackingLabel").toString());
        this.labelCSVExport.setText(this.settingsController.translationList.getValue().get("CSVLabel").toString());
        this.lblFrom.setText(this.settingsController.translationList.getValue().get("ToLabel").toString());
        this.lblTo.setText(this.settingsController.translationList.getValue().get("FromLabel").toString());
        this.lblDefaultDataSource.setText(this.settingsController.translationList.getValue().get("DefaultDataUpdate").toString());
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
        Double r = 3.0;
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
        EventHandler<Event> click = new EventHandler<Event>() {
            @Override
            public void handle(Event e) {
                updateSwitchButton();
            }
        };
        switchButton.setFocusTraversable(false);
        switchButton.setOnMouseClicked(click);
        stack.setOnMouseClicked(click);
    }

    public void btnCloseDefidPressed(){
        TransactionController.getInstance().stopServer();
    }
    public void btnOpenProjectFolderPressed(){

        if (SettingsController.getInstance().getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            File file = new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME);
            try {
                Runtime.getRuntime().exec(new String[]{"xdg-open", file.getAbsolutePath()});
            } catch (IOException e) {
                SettingsController.getInstance().logger.warning(e.toString());
            }
        } else {
            try {
                Desktop.getDesktop().open(new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME));
        } catch (IOException e) {
            SettingsController.getInstance().logger.warning(e.toString());
        }
        }
    }
    public void btnOpenInstallationFolderPressed(){

        if (SettingsController.getInstance().getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            File file = new File(System.getProperty("user.dir"));
            try {
                Runtime.getRuntime().exec(new String[]{"xdg-open", file.getAbsolutePath()});
            } catch (IOException e) {
                SettingsController.getInstance().logger.warning(e.toString());
            }
        } else {
            try {
                Desktop.getDesktop().open(new File(System.getProperty("user.dir")));
            } catch (IOException e) {
                SettingsController.getInstance().logger.warning(e.toString());
            }
        }
    }
}





