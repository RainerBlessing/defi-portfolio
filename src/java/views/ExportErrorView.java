package views;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import controllers.SettingsController;

import java.net.URL;
import java.util.ResourceBundle;

public class ExportErrorView implements Initializable {
    public Button btnClose;
    @FXML
    public Label lblText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblText.setText(SettingsController.getInstance().translationList.getValue().get("CSVExportedError").toString());
        btnClose.setText(SettingsController.getInstance().translationList.getValue().get("Close").toString());
    }

    public void btnClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }


}


