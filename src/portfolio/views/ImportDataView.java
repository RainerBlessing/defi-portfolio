package portfolio.views;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import portfolio.controllers.CheckConnection;
import portfolio.controllers.MainViewController;
import portfolio.controllers.SettingsController;
import portfolio.controllers.TransactionController;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;

public class ImportDataView {

    @FXML
    public Button btnUpdateData,btnCakeCSV,btnWalletCSV,btnClose;
    @FXML
    public CheckBox cmbSaveDefault;


    public void btnUpdateDataPressed(){
        TransactionController.getInstance().updateDatabase();
        this.btnClosePressed();
    }
    public void btnCakeCSVPressed(){
        this.btnClosePressed();
        TransactionController.getInstance().importCakeCSV();
    }
    public void btnWalletCSVPressed(){
        this.btnClosePressed();
        TransactionController.getInstance().importWalletCSV();
    }
    public void btnClosePressed(){
        Stage stage = (Stage) btnUpdateData.getScene().getWindow();
        stage.close();
    }
    public void defaultChanged(){
        SettingsController.getInstance().saveSettings();
    }

}
