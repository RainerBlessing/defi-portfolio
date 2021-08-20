package portfolio.views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import portfolio.controllers.SettingsController;

import java.net.URL;
import java.util.ResourceBundle;

public class FileTypeNotSupportedView implements Initializable {
    public Button btnClose;
    @FXML
    public Label lblDisclaimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblDisclaimer.setText(SettingsController.getInstance().translationList.getValue().get("FileTypeNotSupported").toString());
        btnClose.setText(SettingsController.getInstance().translationList.getValue().get("Close").toString());
    }

    public void btnClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}