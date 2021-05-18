package portfolio.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import portfolio.controllers.SettingsController;

import java.net.URL;
import java.util.ResourceBundle;

public class MissingTransactionView implements Initializable {
    @FXML
    public Button btnClose;
    public Button btnOpen;
    @FXML
    public CheckBox hCheckBox;
    @FXML
    public Label lblTransaction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblTransaction.setText(SettingsController.getInstance().translationList.getValue().get("MissingTransaction").toString());
        hCheckBox.setText(SettingsController.getInstance().translationList.getValue().get("DisclaimCheck").toString());
        btnClose.setText(SettingsController.getInstance().translationList.getValue().get("Close").toString());
        btnClose.setText(SettingsController.getInstance().translationList.getValue().get("Open").toString());
    }

    public void btnClose() {
        SettingsController.getInstance().showMissingTransaction = !hCheckBox.isSelected();
        SettingsController.getInstance().saveSettings();
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}


